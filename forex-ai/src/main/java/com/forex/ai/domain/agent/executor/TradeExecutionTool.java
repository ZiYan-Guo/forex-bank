package com.forex.ai.domain.agent.executor;

import com.forex.ai.domain.agent.core.AgentTool;
import com.forex.ai.domain.agent.registry.ToolRegistry;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

@Component
@RequiredArgsConstructor
public class TradeExecutionTool implements AgentTool {

    private final ToolRegistry toolRegistry;

    @PostConstruct
    public void init() {
        toolRegistry.register(this);
    }

    @Override
    public String name() {
        return "place_trade";
    }

    @Override
    public String description() {
        return "执行外汇或贵金属交易。支持即期、远期、掉期、期权等交易类型。参数: query (自然语言交易指令), direction (BUY/SELL), amount, currencyPair, tenor (SPOT/FORWARD/SWAP/OPTION), product (FOREX/METAL)";
    }

    @Override
    public Map<String, Object> execute(Map<String, Object> parameters) {
        String query = (String) parameters.getOrDefault("query", "");
        String direction = (String) parameters.getOrDefault("direction", "BUY");
        String product = (String) parameters.getOrDefault("product", "FOREX");

        Map<String, Object> result = new LinkedHashMap<>();
        result.put("tradeId", UUID.randomUUID().toString());
        result.put("tradeNo", "TRD" + System.currentTimeMillis());
        result.put("product", product);
        result.put("direction", direction);
        result.put("amount", parameters.getOrDefault("amount", "100000"));
        result.put("currencyPair", parameters.getOrDefault("currencyPair", "USD/CNY"));
        result.put("tenor", parameters.getOrDefault("tenor", "SPOT"));
        result.put("status", "SUBMITTED");
        result.put("confirmUrl", "/api/trading/confirm");
        result.put("message", product + "交易指令已提交，请确认。");
        return result;
    }
}
