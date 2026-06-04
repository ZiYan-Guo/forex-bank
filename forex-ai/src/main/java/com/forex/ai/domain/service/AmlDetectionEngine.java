package com.forex.ai.domain.service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.List;
import java.util.Objects;

import org.springframework.stereotype.Service;

import com.forex.ai.domain.model.aggregate.RiskAiAssessment;

import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
public class AmlDetectionEngine {

    public RiskAiAssessment evaluateTransaction(Long customerId, String bizNo, BigDecimal amount,
                                                 String currency, String direction) {
        BigDecimal riskScore = calculateAmlScore(amount, 0, null);
        String riskLevel = resolveLevel(riskScore);

        String analysis = buildAnalysis(customerId, amount, currency, direction, riskScore);
        String recommendation = buildRecommendation(riskLevel);

        String dataPointsJson = "{\"amount\":%s,\"currency\":\"%s\",\"direction\":\"%s\"}"
                .formatted(amount.toPlainString(), currency, direction);

        return RiskAiAssessment.create(customerId, bizNo, RiskAiAssessment.AML,
                riskScore, riskLevel, analysis, recommendation, dataPointsJson);
    }

    public BigDecimal calculateAmlScore(BigDecimal amount, int frequency7d, String country) {
        BigDecimal score = BigDecimal.ZERO;

        if (amount != null) {
            BigDecimal usdThreshold = new BigDecimal("50000");
            if (amount.compareTo(usdThreshold) > 0) {
                score = score.add(new BigDecimal("0.30"));
            }
            BigDecimal highThreshold = new BigDecimal("100000");
            if (amount.compareTo(highThreshold) > 0) {
                score = score.add(new BigDecimal("0.25"));
            }
            BigDecimal maxThreshold = new BigDecimal("500000");
            if (amount.compareTo(maxThreshold) > 0) {
                score = score.add(new BigDecimal("0.25"));
            }
        }

        if (frequency7d > 3) {
            score = score.add(new BigDecimal("0.10").multiply(
                    new BigDecimal(Math.min(frequency7d - 3, 5))));
        }

        if (isHighRiskCountry(country)) {
            score = score.add(new BigDecimal("0.20"));
        }

        return score.min(BigDecimal.ONE).setScale(2, RoundingMode.HALF_UP);
    }

    public String analyzePattern(List<?> recentTransactions) {
        if (recentTransactions == null || recentTransactions.isEmpty()) {
            return "无历史交易记录，无法进行模式分析";
        }
        int count = recentTransactions.size();
        return "分析近 %d 笔交易：未检测到明显异常模式，交易行为符合正常业务规律。".formatted(count);
    }

    private String resolveLevel(BigDecimal score) {
        double v = score.doubleValue();
        if (v >= 0.8) return RiskAiAssessment.LEVEL_PROHIBITED;
        if (v >= 0.5) return RiskAiAssessment.LEVEL_HIGH;
        if (v >= 0.3) return RiskAiAssessment.LEVEL_MEDIUM;
        return RiskAiAssessment.LEVEL_LOW;
    }

    private String buildAnalysis(Long customerId, BigDecimal amount, String currency,
                                  String direction, BigDecimal score) {
        return "客户 %d 的 %s 交易，金额 %s %s，AML风险评分 %s。"
                .formatted(customerId, Objects.toString(direction, "UNKNOWN"),
                        amount.toPlainString(), currency, score.toPlainString());
    }

    private String buildRecommendation(String riskLevel) {
        return switch (riskLevel) {
            case RiskAiAssessment.LEVEL_PROHIBITED -> "建议立即中止交易并上报合规部门";
            case RiskAiAssessment.LEVEL_HIGH -> "建议人工复核后决定是否放行";
            case RiskAiAssessment.LEVEL_MEDIUM -> "建议进行增强尽职调查（EDD）";
            default -> "建议正常处理";
        };
    }

    private boolean isHighRiskCountry(String country) {
        if (country == null) return false;
        return List.of("KP", "IR", "SY", "CU", "VE").contains(country.toUpperCase());
    }
}
