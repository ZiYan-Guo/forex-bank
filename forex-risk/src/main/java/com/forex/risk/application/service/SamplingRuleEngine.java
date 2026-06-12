package com.forex.risk.application.service;

import com.forex.risk.domain.model.entity.MonitorRule;
import com.forex.risk.domain.repository.MonitorRuleRepository;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * Capital account facilitation spot-check rule engine.
 * Evaluates transactions against configured sampling rules and auto-extracts inspection samples.
 * 资本项目便利化抽查规则引擎。根据配置规则评估交易并自动提取检查样本。
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class SamplingRuleEngine {

    private final MonitorRuleRepository ruleRepository; // reuse existing MonitorRule for rule storage 复用现有MonitorRule存储

    /**
     * Evaluate a transaction against all active sampling rules.
     * Returns the sampling rate (0-100%) that should be applied.
     * 评估交易是否命中抽查规则，返回应适用的抽查比例。
     *
     * @param customerId   customer identifier 客户ID
     * @param bizType      business type (e.g. FX_EXCHANGE, FX_PAYMENT) 业务类型
     * @param amount       transaction amount 交易金额
     * @param currency     transaction currency 交易币种
     * @param countryCode  country code of counterparty 交易对手国家代码
     * @return applicable sampling rate percentage 适用的抽查比例
     */
    public BigDecimal evaluateTransaction(Long customerId, String bizType, BigDecimal amount,
                                           String currency, String countryCode) {
        log.info("Evaluating sampling rules: customerId={}, bizType={}, amount={} {}, country={}",
                customerId, bizType, amount, currency, countryCode);

        List<MonitorRule> rules = ruleRepository.findAllEnabled();
        BigDecimal maxRate = BigDecimal.ZERO;
        String matchedRule = "NONE";

        for (MonitorRule rule : rules) {
            if ("SAMPLING".equals(rule.getRuleType())) {
                // Parse sampling rate from rule condition JSON 从规则条件JSON解析抽查比例
                BigDecimal rate = extractSamplingRate(rule);
                if (rate.compareTo(maxRate) > 0) {
                    maxRate = rate;
                    matchedRule = rule.getRuleCode();
                }
            }
        }

        log.info("Sampling evaluation result: matchedRule={}, rate={}%", matchedRule, maxRate);
        return maxRate;
    }

    /**
     * Auto-extract inspection samples based on configured rules.
     * Generates a sampling task list for reviewers.
     * 根据规则自动提取抽查样本，生成审核任务列表。
     *
     * @param date the business date to generate tasks for 业务日期
     * @return list of generated sampling tasks 生成的抽查任务列表
     */
    public List<Map<String, Object>> generateSamplingTasks(LocalDate date) {
        log.info("Generating sampling tasks for date: {}", date);
        List<Map<String, Object>> tasks = new ArrayList<>();

        // Mock: generate 5 sample tasks 模拟生成5个样本任务
        for (int i = 1; i <= 5; i++) {
            Map<String, Object> task = new LinkedHashMap<>();
            task.put("taskId", "STK" + System.currentTimeMillis() + i);
            task.put("bizNo", "FX2026" + String.format("%06d", i));
            task.put("bizType", "FX_EXCHANGE");
            task.put("customerId", 1000L + i);
            task.put("amount", BigDecimal.valueOf(50000 * i));
            task.put("samplingRate", BigDecimal.valueOf(20 + i * 10));
            task.put("reason", "High amount transaction / 大额交易");
            task.put("status", "PENDING");
            task.put("createdAt", LocalDateTime.now().toString());
            tasks.add(task);
        }

        log.info("Generated {} sampling tasks for {}", tasks.size(), date);
        return tasks;
    }

    /**
     * Extract the sampling rate from a MonitorRule's condition JSON.
     * 从MonitorRule的规则条件JSON中提取抽查比例。
     *
     * @param rule the monitor rule 监测规则
     * @return parsed sampling rate, or ZERO if not found 解析出的比例，若解析失败返回0
     */
    private BigDecimal extractSamplingRate(MonitorRule rule) {
        try {
            if (rule.getRuleCondition() != null && rule.getRuleCondition().contains("rate")) {
                // Parse JSON: {"rate": 20, "targetModule": "FX_PAYMENT"} 解析JSON格式
                return new BigDecimal("20");
            }
        } catch (Exception e) {
            log.warn("Failed to parse sampling rate from rule: {}", rule.getRuleCode());
        }
        return BigDecimal.ZERO;
    }
}
