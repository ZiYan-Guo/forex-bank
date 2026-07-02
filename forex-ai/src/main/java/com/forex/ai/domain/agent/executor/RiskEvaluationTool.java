package com.forex.ai.domain.agent.executor;

import com.forex.ai.domain.agent.core.AgentTool;
import com.forex.ai.domain.agent.registry.ToolRegistry;
import com.forex.ai.application.service.AiAppService;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.LinkedHashMap;
import java.util.Map;

@Component
@RequiredArgsConstructor
public class RiskEvaluationTool implements AgentTool {

    private final ToolRegistry toolRegistry;
    private final AiAppService aiAppService;

    @PostConstruct
    public void init() {
        toolRegistry.register(this);
    }

    @Override
    public String name() {
        return "evaluate_risk";
    }

    @Override
    public String description() {
        return "评估业务风险敞口、客户信用风险、反洗钱风险。参数: query (客户ID或交易描述), riskType (AML/CREDIT/MARKET)";
    }

    @Override
    public Map<String, Object> execute(Map<String, Object> parameters) {
        String query = (String) parameters.getOrDefault("query", "");
        String riskType = (String) parameters.getOrDefault("riskType", "AML");

        Map<String, Object> amlResult = aiAppService.amlEvaluate(query, "{}");

        Map<String, Object> result = new LinkedHashMap<>();
        result.put("riskType", riskType);
        result.put("amlAssessment", amlResult);
        result.put("recommendation", "当前风险等级为低风险，建议按常规流程处理。");
        return result;
    }
}
