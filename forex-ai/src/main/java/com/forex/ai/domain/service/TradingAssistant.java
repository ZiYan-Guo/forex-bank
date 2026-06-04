package com.forex.ai.domain.service;

import java.time.LocalDate;

import org.springframework.stereotype.Service;

import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
public class TradingAssistant {

    public String processNaturalLanguageTrade(String userInput) {
        log.info("Processing natural language trade: {}", userInput);
        if (userInput == null || userInput.isBlank()) {
            return "请输入交易指令";
        }
        StringBuilder result = new StringBuilder();
        result.append("解析结果:\n");

        String lower = userInput.toLowerCase();
        if (lower.contains("买入") || lower.contains("buy")) {
            result.append("- 交易方向: 买入\n");
        } else if (lower.contains("卖出") || lower.contains("sell")) {
            result.append("- 交易方向: 卖出\n");
        }
        if (lower.contains("美元") || lower.contains("usd")) {
            result.append("- 货币对识别: USD\n");
        }
        if (lower.contains("欧元") || lower.contains("eur")) {
            result.append("- 货币对识别: EUR\n");
        }
        if (lower.contains("即期") || lower.contains("spot")) {
            result.append("- 交易类型: 即期交易\n");
        } else if (lower.contains("远期") || lower.contains("forward")) {
            result.append("- 交易类型: 远期交易\n");
        }
        result.append("- 原始输入: ").append(userInput);
        return result.toString();
    }

    public String recommendHedging(Long customerId, String businessType, String riskPreference) {
        log.info("Recommending hedging for customer {}, type {}, preference {}",
                customerId, businessType, riskPreference);
        StringBuilder rec = new StringBuilder();
        rec.append("套期保值建议 (客户: ").append(customerId).append("):\n\n");

        switch (businessType) {
            case "IMPORT" -> {
                rec.append("- 建议: 远期购汇锁定成本\n");
                rec.append("- 工具: 买入远期外汇合约\n");
                rec.append("- 期限: 3-6个月\n");
            }
            case "EXPORT" -> {
                rec.append("- 建议: 远期结汇锁定收益\n");
                rec.append("- 工具: 卖出远期外汇合约\n");
                rec.append("- 期限: 3-6个月\n");
            }
            case "INVESTMENT" -> {
                if ("HIGH".equalsIgnoreCase(riskPreference)) {
                    rec.append("- 建议: 外汇期权组合策略\n");
                } else {
                    rec.append("- 建议: 远期合约 + 货币互换\n");
                }
            }
            default -> rec.append("- 建议: 即期交易为主，搭配远期锁汇\n");
        }
        return rec.toString();
    }

    public String generateRiskReport(Long customerId, String period) {
        log.info("Generating risk report for customer {}, period {}", customerId, period);
        LocalDate now = LocalDate.now();
        return """
                ## 外汇风险报告
                **客户ID:** %d
                **报告周期:** %s
                **生成日期:** %s
                
                ### 风险评估
                - 汇率风险: 中等
                - 利率风险: 低
                - 操作风险: 低
                
                ### 建议
                1. 关注美元/人民币汇率波动
                2. 评估远期敞口对冲比例
                3. 定期审查交易对手信用风险
                """.formatted(customerId, period, now.toString());
    }
}
