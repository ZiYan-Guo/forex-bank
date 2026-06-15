package com.forex.trading.application.service;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.springframework.stereotype.Service;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
/**
 * Option strategy recommendation service. // 期权策略推荐服务。
 * Provides structured option strategy suggestions based on customer risk
 * preference and FX exposure. // 根据客户风险偏好和外汇敞口提供结构化的期权策略建议。
 */
public class OptionStrategyService {

    /**
     * Recommend option strategies based on customer profile and exposure. //
     * 根据客户画像和敞口推荐期权策略。
     */
    public Map<String, Object> recommendStrategies(Long customerId, String riskPreference, BigDecimal exposure,
            String currencyPair) {
        log.info("Recommending option strategies: customerId={}, risk={}, exposure={} {}, pair={}",
                customerId, riskPreference, exposure, exposure != null ? exposure.getClass().getSimpleName() : "null",
                currencyPair);

        // Build available strategy list // 构建可用策略列表
        List<Map<String, Object>> strategies = new ArrayList<>();
        strategies.add(Map.of(
                "name", "区间宝 Range Forward", // 区间宝
                "description", "Buy call + sell put with 0 cost", // 零成本买入看涨+卖出看跌
                "suitableRisk", "CONSERVATIVE",
                "cost", 0,
                "maxProtection", "85%"));
        strategies.add(Map.of(
                "name", "风险逆转 Risk Reversal", // 风险逆转
                "description", "Buy call financed by selling put", // 卖出看跌融资买入看涨
                "suitableRisk", "BALANCED",
                "cost", "moderate",
                "maxProtection", "70%"));
        strategies.add(Map.of(
                "name", "领口期权 Collar", // 领口期权
                "description", "Buy put + sell call to cap both sides", // 买入看跌+卖出看涨锁定两端
                "suitableRisk", "AGGRESSIVE",
                "cost", "low",
                "maxProtection", "60%"));

        // Determine recommended strategy based on risk preference // 根据风险偏好确定推荐策略
        String recommended = riskPreference != null && riskPreference.contains("CONSERV")
                ? "区间宝 Range Forward"
                : "风险逆转 Risk Reversal";

        Map<String, Object> result = new LinkedHashMap<>();
        result.put("customerId", customerId);
        result.put("exposure", exposure);
        result.put("currencyPair", currencyPair);
        result.put("strategies", strategies); // 策略列表
        result.put("recommendedStrategy", recommended); // 推荐策略
        log.info("Strategy recommendation: recommended={}", result.get("recommendedStrategy"));
        return result;
    }
}
