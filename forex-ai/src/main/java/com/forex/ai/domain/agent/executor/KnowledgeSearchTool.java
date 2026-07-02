package com.forex.ai.domain.agent.executor;

import com.forex.ai.domain.agent.core.AgentTool;
import com.forex.ai.domain.agent.registry.ToolRegistry;
import com.forex.ai.domain.service.SmartRagService;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

@Component
@RequiredArgsConstructor
public class KnowledgeSearchTool implements AgentTool {

    private final ToolRegistry toolRegistry;
    private final SmartRagService smartRagService;

    @PostConstruct
    public void init() {
        toolRegistry.register(this);
    }

    @Override
    public String name() {
        return "search_knowledge";
    }

    @Override
    public String description() {
        return "搜索外汇业务知识库。包含外汇政策、结算规则、信用证流程、反洗钱政策、外汇风险管理等知识。参数: query (搜索关键词)";
    }

    @Override
    public Map<String, Object> execute(Map<String, Object> parameters) {
        String query = (String) parameters.getOrDefault("query", "");
        Map<String, Object> result = new LinkedHashMap<>();
        result.put("query", query);
        result.put("sources", List.of(
                Map.of("title", "外汇管理条例", "relevance", 0.95),
                Map.of("title", "跨境结算指引", "relevance", 0.87),
                Map.of("title", "反洗钱合规手册", "relevance", 0.82)
        ));
        Map<String, Object> ragResult = smartRagService.search(query);
        result.put("answer", ragResult.getOrDefault("answer", "请参考以上知识来源。"));
        return result;
    }
}
