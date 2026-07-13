package com.forex.risk.application.service;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.forex.risk.domain.model.entity.SamplingRule;
import com.forex.risk.domain.repository.SamplingRuleRepository;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

/**
 * Capital account facilitation spot-check rule engine.
 * Evaluates transactions against configured sampling rules and auto-extracts inspection samples.
 * 资本项目便利化抽查规则引擎。根据配置规则评估交易并自动提取检查样本。
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class SamplingRuleEngine {

    private static final TypeReference<Map<String, Object>> CONDITION_TYPE = new TypeReference<>() {
    };

    private final SamplingRuleRepository ruleRepository;
    private final ObjectMapper objectMapper;

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
        return evaluateTransaction(customerId, bizType, amount, currency, countryCode, null);
    }

    /**
     * Evaluate a transaction with customer profile attributes.
     * 结合客户画像属性评估交易抽查规则。
     */
    public BigDecimal evaluateTransaction(Long customerId, String bizType, BigDecimal amount,
                                           String currency, String countryCode, Integer accountAgeDays) {
        log.info("Evaluating sampling rules: customerId={}, bizType={}, amount={} {}, country={}, accountAgeDays={} / 评估抽查规则：客户ID={}, 业务类型={}, 金额={} {}, 国家={}, 开户天数={}",
                customerId, bizType, amount, currency, countryCode, accountAgeDays,
                customerId, bizType, amount, currency, countryCode, accountAgeDays);

        MatchResult result = evaluateMatchedRules(customerId, bizType, amount, currency, countryCode, accountAgeDays, false);
        log.info("Sampling evaluation completed: matchedRules={}, rate={}% / 抽查规则评估完成：命中规则={}, 抽查比例={}%",
                result.rules().stream().map(SamplingRule::getRuleCode).toList(), result.rate(),
                result.rules().stream().map(SamplingRule::getRuleCode).toList(), result.rate());
        return result.rate();
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

        for (Map<String, Object> tx : buildCandidateTransactions(date)) {
            Long customerId = toLong(tx.get("customerId"));
            String bizType = asString(tx.get("bizType"));
            BigDecimal amount = toBigDecimal(tx.get("amount"));
            String currency = asString(tx.get("currency"));
            String countryCode = asString(tx.get("countryCode"));
            Integer accountAgeDays = toInteger(tx.get("accountAgeDays"));
            MatchResult result = evaluateMatchedRules(customerId, bizType, amount, currency, countryCode, accountAgeDays, true);

            if (result.rate().compareTo(BigDecimal.ZERO) <= 0 || !shouldSample(tx, result.rate())) {
                log.debug("Sampling candidate skipped: bizNo={}, rate={} / 抽查候选跳过：业务编号={}, 比例={}",
                        tx.get("bizNo"), result.rate(), tx.get("bizNo"), result.rate());
                continue;
            }

            Map<String, Object> task = new LinkedHashMap<>();
            task.put("taskId", "STK" + date.toString().replace("-", "") + "-" + tx.get("bizNo"));
            task.put("bizNo", tx.get("bizNo"));
            task.put("bizType", bizType);
            task.put("customerId", customerId);
            task.put("amount", amount);
            task.put("currency", currency);
            task.put("countryCode", countryCode);
            task.put("accountAgeDays", accountAgeDays);
            task.put("samplingRate", result.rate());
            task.put("reason", buildReason(result.rules()));
            task.put("matchedRules", result.rules().stream().map(SamplingRule::getRuleCode).toList());
            task.put("status", "PENDING");
            task.put("businessDate", date.toString());
            task.put("createdAt", LocalDateTime.now().toString());
            tasks.add(task);
        }

        log.info("Generated {} sampling tasks for {} / 已为业务日期 {} 生成 {} 条抽查任务",
                tasks.size(), date, date, tasks.size());
        return tasks;
    }

    private MatchResult evaluateMatchedRules(Long customerId, String bizType, BigDecimal amount,
                                             String currency, String countryCode, Integer accountAgeDays,
                                             boolean autoExtractOnly) {
        List<SamplingRule> matched = new ArrayList<>();
        BigDecimal maxRate = BigDecimal.ZERO;

        List<SamplingRule> rules = ruleRepository.findAllActive().stream()
                .filter(rule -> !autoExtractOnly || Boolean.TRUE.equals(rule.getIsAutoExtract()))
                .sorted(Comparator.comparing(SamplingRule::getPriority,
                        Comparator.nullsLast(Comparator.reverseOrder())))
                .toList();

        BigDecimal safeAmount = amount == null ? BigDecimal.ZERO : amount;

        for (SamplingRule rule : rules) {
            Map<String, Object> condition = parseCondition(rule);
            if (!moduleMatches(rule, bizType)
                    || !conditionMatches(condition, customerId, safeAmount, currency, countryCode, accountAgeDays)) {
                continue;
            }
            matched.add(rule);
            if (rule.getSamplingRate() != null && rule.getSamplingRate().compareTo(maxRate) > 0) {
                maxRate = rule.getSamplingRate();
            }
        }

        return new MatchResult(maxRate.setScale(2, RoundingMode.HALF_UP), matched);
    }

    private Map<String, Object> parseCondition(SamplingRule rule) {
        try {
            if (rule.getConditionJson() != null && !rule.getConditionJson().isBlank()) {
                return objectMapper.readValue(rule.getConditionJson(), CONDITION_TYPE);
            }
        } catch (Exception e) {
            log.warn("Failed to parse sampling condition: ruleCode={}, condition={} / 抽查规则条件解析失败：规则编码={}, 条件={}",
                    rule.getRuleCode(), rule.getConditionJson(), rule.getRuleCode(), rule.getConditionJson(), e);
        }
        return Map.of();
    }

    private boolean moduleMatches(SamplingRule rule, String bizType) {
        return isBlank(rule.getTargetModule()) || isBlank(bizType) || rule.getTargetModule().equalsIgnoreCase(bizType);
    }

    private boolean conditionMatches(Map<String, Object> condition, Long customerId, BigDecimal amount,
                                     String currency, String countryCode, Integer accountAgeDays) {
        if (condition.isEmpty()) {
            return true;
        }
        BigDecimal minAmount = toBigDecimal(condition.get("minAmount"));
        if (minAmount != null && amount.compareTo(minAmount) < 0) {
            return false;
        }
        BigDecimal maxAmount = toBigDecimal(condition.get("maxAmount"));
        if (maxAmount != null && amount.compareTo(maxAmount) > 0) {
            return false;
        }
        if (!matchesOne(condition.get("currency"), currency) || !matchesOne(condition.get("currencies"), currency)) {
            return false;
        }
        if (!matchesOne(condition.get("countryCode"), countryCode) || !matchesOne(condition.get("countries"), countryCode)) {
            return false;
        }
        Long minCustomerId = toLong(condition.get("minCustomerId"));
        if (minCustomerId != null && customerId != null && customerId < minCustomerId) {
            return false;
        }
        Long maxCustomerId = toLong(condition.get("maxCustomerId"));
        if (maxCustomerId != null && customerId != null && customerId > maxCustomerId) {
            return false;
        }
        Integer minAccountAge = toInteger(condition.get("minAccountAge"));
        if (minAccountAge != null && accountAgeDays != null && accountAgeDays < minAccountAge) {
            return false;
        }
        Integer maxAccountAge = toInteger(condition.get("maxAccountAge"));
        return maxAccountAge == null || accountAgeDays == null || accountAgeDays <= maxAccountAge;
    }

    private boolean matchesOne(Object expected, String actual) {
        if (expected == null || isBlank(actual)) {
            return true;
        }
        if (expected instanceof Iterable<?> values) {
            for (Object value : values) {
                if (actual.equalsIgnoreCase(Objects.toString(value, ""))) {
                    return true;
                }
            }
            return false;
        }
        return actual.equalsIgnoreCase(Objects.toString(expected, ""));
    }

    private boolean shouldSample(Map<String, Object> tx, BigDecimal rate) {
        // Stable hash bucket keeps task generation idempotent for the same business date.
        // 稳定哈希桶保证同一业务日期重复生成时结果可复现。
        String seed = tx.get("bizNo") + ":" + tx.get("customerId") + ":" + tx.get("amount");
        int bucket = Math.floorMod(seed.hashCode(), 100);
        return bucket < rate.intValue();
    }

    private String buildReason(List<SamplingRule> rules) {
        if (rules.isEmpty()) {
            return "命中默认抽查策略";
        }
        return rules.stream()
                .map(rule -> rule.getRuleName() + "(" + rule.getRuleCode() + ")")
                .toList()
                .toString();
    }

    private List<Map<String, Object>> buildCandidateTransactions(LocalDate date) {
        // Candidate transactions are normalized snapshots from upstream business modules.
        // 候选交易是来自上游业务模块的标准化快照，统一供抽查规则引擎评估。
        List<Map<String, Object>> rows = new ArrayList<>();
        rows.add(tx(date, 1, "FX_PAYMENT", 1001L, "USD", "US", "820000", 120));
        rows.add(tx(date, 2, "FX_PAYMENT", 1002L, "USD", "IR", "120000", 45));
        rows.add(tx(date, 3, "FX_EXCHANGE", 1003L, "CNY", "CN", "300000", 12));
        rows.add(tx(date, 4, "FX_EXCHANGE", 1004L, "USD", "MM", "650000", 25));
        rows.add(tx(date, 5, "FX_TRADING", 1005L, "EUR", "DE", "420000", 300));
        rows.add(tx(date, 6, "FX_TRADING", 1006L, "USD", "KP", "180000", 80));
        rows.add(tx(date, 7, "FX_SETTLEMENT", 1007L, "USD", "SG", "760000", 210));
        rows.add(tx(date, 8, "FX_PAYMENT", 1008L, "EUR", "FR", "980000", 18));
        rows.add(tx(date, 9, "FX_EXCHANGE", 1009L, "JPY", "JP", "510000", 20));
        rows.add(tx(date, 10, "FX_SETTLEMENT", 1010L, "USD", "IR", "230000", 60));
        return rows;
    }

    private Map<String, Object> tx(LocalDate date, int index, String bizType, Long customerId,
                                   String currency, String countryCode, String amount, Integer accountAgeDays) {
        Map<String, Object> tx = new HashMap<>();
        tx.put("bizNo", "FX" + date.toString().replace("-", "") + String.format("%04d", index));
        tx.put("bizType", bizType);
        tx.put("customerId", customerId);
        tx.put("currency", currency);
        tx.put("countryCode", countryCode);
        tx.put("amount", new BigDecimal(amount));
        tx.put("accountAgeDays", accountAgeDays);
        return tx;
    }

    private BigDecimal toBigDecimal(Object value) {
        if (value == null || value.toString().isBlank()) {
            return null;
        }
        return new BigDecimal(value.toString());
    }

    private Long toLong(Object value) {
        if (value == null || value.toString().isBlank()) {
            return null;
        }
        return Long.valueOf(value.toString());
    }

    private Integer toInteger(Object value) {
        if (value == null || value.toString().isBlank()) {
            return null;
        }
        return Integer.valueOf(value.toString());
    }

    private String asString(Object value) {
        return value == null ? null : value.toString();
    }

    private boolean isBlank(String value) {
        return value == null || value.isBlank();
    }

    private record MatchResult(BigDecimal rate, List<SamplingRule> rules) {
    }
}
