package com.forex.ai.domain.agent.core;

import com.forex.ai.domain.agent.registry.ToolRegistry;
import com.forex.ai.infrastructure.config.ModelProviderConfig;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.*;

@Slf4j
@Service
@RequiredArgsConstructor
public class AgentOrchestrator {

    private final ToolRegistry toolRegistry;
    private final ModelProviderConfig modelProviderConfig;
    private final ObjectMapper objectMapper;

    private static final int MAX_ITERATIONS = 5;

    public AgentResult execute(String userQuery, String sessionId) {
        List<AgentAction> actionHistory = new ArrayList<>();
        List<Map<String, Object>> conversationHistory = new ArrayList<>();
        conversationHistory.add(Map.of("role", "user", "content", userQuery));

        String systemPrompt = buildSystemPrompt();

        for (int iteration = 0; iteration < MAX_ITERATIONS; iteration++) {
            log.info("Agent iteration {} for session {}", iteration, sessionId);

            AgentAction action = think(systemPrompt, conversationHistory);
            actionHistory.add(action);

            if (action.isFinalAnswer()) {
                return AgentResult.builder()
                        .answer(action.getFinalAnswer())
                        .actionHistory(actionHistory)
                        .iterations(iteration + 1)
                        .success(true)
                        .build();
            }

            if (action.requiresTool()) {
                Map<String, Object> observation = executeTool(action);
                action.setObservation(objectMapper.valueToTree(observation).toString());
                conversationHistory.add(Map.of(
                        "role", "tool",
                        "tool_name", action.getTool(),
                        "result", observation
                ));
            } else {
                log.warn("Agent action neither final nor tool call at iteration {}", iteration);
                return AgentResult.builder()
                        .answer("无法完成请求，请提供更多信息。")
                        .actionHistory(actionHistory)
                        .iterations(iteration + 1)
                        .success(false)
                        .build();
            }
        }

        return AgentResult.builder()
                .answer("请求处理超时，请稍后重试。")
                .actionHistory(actionHistory)
                .iterations(MAX_ITERATIONS)
                .success(false)
                .build();
    }

    private String buildSystemPrompt() {
        StringBuilder sb = new StringBuilder();
        sb.append("你是一个银行外汇业务智能助手。你可以使用以下工具来帮助用户：\n\n");
        sb.append("## 可用工具\n\n");
        for (AgentTool tool : toolRegistry.getAllTools()) {
            sb.append("- **").append(tool.name()).append("**: ").append(tool.description()).append("\n");
        }
        sb.append("\n## 响应格式\n\n");
        sb.append("如果可以直接回答，请回复：{\"finalAnswer\": \"你的答案\"}\n");
        sb.append("如果需要调用工具，请回复：{\"thought\": \"思考过程\", \"tool\": \"工具名\", \"parameters\": {}}\n\n");
        sb.append("每次只调用一个工具。观察结果后决定下一步。\n");
        return sb.toString();
    }

    private AgentAction think(String systemPrompt, List<Map<String, Object>> history) {
        // In production, this calls the LLM with system prompt + conversation history
        // For now, use a heuristic approach based on query keywords
        String lastUserMessage = "";
        for (int i = history.size() - 1; i >= 0; i--) {
            if ("user".equals(history.get(i).get("role"))) {
                lastUserMessage = history.get(i).get("content").toString();
                break;
            }
        }

        return parseIntent(lastUserMessage);
    }

    private AgentAction parseIntent(String query) {
        String q = query.toLowerCase();

        if (q.contains("汇率") || q.contains("rate") || q.contains("价格")) {
            return AgentAction.builder()
                    .thought("用户想查询汇率信息")
                    .tool("query_exchange_rate")
                    .parameters(Map.of("query", query))
                    .build();
        }
        if (q.contains("报告") || q.contains("报表") || q.contains("report") || q.contains("指标") || q.contains("下载")) {
            return AgentAction.builder()
                    .thought("用户想生成或下载报告/指标")
                    .tool("generate_report")
                    .parameters(Map.of("query", query, "format", detectFormat(query)))
                    .build();
        }
        if (q.contains("交易") || q.contains("trade") || q.contains("下单") || q.contains("买入") || q.contains("卖出") || q.contains("购买") || q.contains("黄金") || q.contains("白银") || q.contains("铂金") || q.contains("贵金属")) {
            return AgentAction.builder()
                    .thought("用户想进行交易操作")
                    .tool("place_trade")
                    .parameters(Map.of("query", query))
                    .build();
        }
        if (q.contains("风险") || q.contains("risk") || q.contains("敞口") || q.contains("限额")) {
            return AgentAction.builder()
                    .thought("用户想查询风险评估")
                    .tool("evaluate_risk")
                    .parameters(Map.of("query", query))
                    .build();
        }
        if (q.contains("支付") || q.contains("转账") || q.contains("汇款") || q.contains("payment")) {
            return AgentAction.builder()
                    .thought("用户想进行支付操作")
                    .tool("initiate_payment")
                    .parameters(Map.of("query", query))
                    .build();
        }
        if (q.contains("查询") || q.contains("了解") || q.contains("是什么") || q.contains("如何") || q.contains("怎么") || q.contains("知识") || q.contains("faq")) {
            return AgentAction.builder()
                    .thought("用户想查询知识库")
                    .tool("search_knowledge")
                    .parameters(Map.of("query", query))
                    .build();
        }

        return AgentAction.builder()
                .finalAnswer("您好，我是银行外汇业务智能助手。我可以帮您：\n"
                        + "1. 查询实时汇率\n"
                        + "2. 生成业务报告和指标下载\n"
                        + "3. 执行外汇/贵金属交易\n"
                        + "4. 评估业务风险敞口\n"
                        + "5. 发起跨境支付\n"
                        + "6. 查询业务知识\n\n"
                        + "请告诉我您的具体需求。")
                .build();
    }

    private String detectFormat(String query) {
        if (query.contains("excel") || query.contains("xls")) return "EXCEL";
        if (query.contains("pdf")) return "PDF";
        if (query.contains("csv")) return "CSV";
        return "PDF";
    }

    private Map<String, Object> executeTool(AgentAction action) {
        try {
            return toolRegistry.execute(action.getTool(), action.getParameters());
        } catch (Exception e) {
            log.error("Tool execution failed: {}", action.getTool(), e);
            return Map.of("error", e.getMessage());
        }
    }
}
