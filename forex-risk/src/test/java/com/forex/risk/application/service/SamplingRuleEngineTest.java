package com.forex.risk.application.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.forex.risk.domain.model.entity.SamplingRule;
import com.forex.risk.domain.repository.SamplingRuleRepository;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;

/**
 * Unit tests for SamplingRuleEngine.
 * 抽查规则引擎单元测试。
 */
class SamplingRuleEngineTest {

    private StubSamplingRuleRepository ruleRepository;
    private SamplingRuleEngine samplingRuleEngine;

    @BeforeEach
    void setUp() {
        ruleRepository = new StubSamplingRuleRepository();
        samplingRuleEngine = new SamplingRuleEngine(ruleRepository, new ObjectMapper());
    }

    @Test
    @DisplayName("Evaluate transaction with no matching rules returns zero")
    void testEvaluateTransaction_NoRules() {
        ruleRepository.rules = List.of();

        BigDecimal result = samplingRuleEngine.evaluateTransaction(
                1001L, "FX_PAYMENT", new BigDecimal("100000"), "USD", "US");

        assertEquals(new BigDecimal("0.00"), result);
    }

    @Test
    @DisplayName("Evaluate transaction returns the highest matched sampling rate")
    void testEvaluateTransaction_WithSamplingRules() {
        SamplingRule rule1 = createRule(1L, "SMP_01", "Low Amount", "{\"minAmount\":10000}", "10", "FX_PAYMENT");
        SamplingRule rule2 = createRule(2L, "SMP_02", "High Amount", "{\"minAmount\":50000}", "50", "FX_PAYMENT");
        ruleRepository.rules = List.of(rule1, rule2);

        BigDecimal result = samplingRuleEngine.evaluateTransaction(
                1001L, "FX_PAYMENT", new BigDecimal("100000"), "USD", "US");

        assertEquals(new BigDecimal("50.00"), result);
    }

    @Test
    @DisplayName("Evaluate transaction supports account age condition")
    void testEvaluateTransaction_AccountAgeCondition() {
        SamplingRule rule = createRule(1L, "SMP_NEW_CUSTOMER", "New Customer",
                "{\"maxAccountAge\":30}", "30", "FX_EXCHANGE");
        ruleRepository.rules = List.of(rule);

        BigDecimal result = samplingRuleEngine.evaluateTransaction(
                1003L, "FX_EXCHANGE", new BigDecimal("300000"), "CNY", "CN", 12);

        assertEquals(new BigDecimal("30.00"), result);
    }

    @Test
    @DisplayName("Generate sampling tasks by active auto-extract rules")
    void testGenerateSamplingTasks() {
        SamplingRule highRiskCountry = createRule(1L, "SMP_HIGH_RISK", "High Risk Country",
                "{\"countries\":[\"IR\",\"KP\",\"MM\"]}", "100", "FX_PAYMENT");
        SamplingRule newCustomer = createRule(2L, "SMP_NEW_CUSTOMER", "New Customer",
                "{\"maxAccountAge\":30}", "100", "FX_EXCHANGE");
        ruleRepository.rules = List.of(highRiskCountry, newCustomer);

        List<Map<String, Object>> tasks = samplingRuleEngine.generateSamplingTasks(LocalDate.of(2026, 6, 1));

        assertEquals(4, tasks.size());
        assertEquals("PENDING", tasks.get(0).get("status"));
        assertFalse(((List<?>) tasks.get(0).get("matchedRules")).isEmpty());
    }

    private SamplingRule createRule(Long id, String code, String name, String condition, String rate, String module) {
        return new SamplingRule(
                id,
                code,
                name,
                condition,
                new BigDecimal(rate),
                module,
                LocalDate.of(2026, 1, 1),
                null,
                10,
                "ACTIVE",
                true
        );
    }

    /**
     * In-memory repository stub avoids Mockito's runtime agent in constrained JDKs.
     * 内存仓储桩避免受限 JDK 环境下 Mockito 运行时 agent 挂载失败。
     */
    private static class StubSamplingRuleRepository implements SamplingRuleRepository {

        private List<SamplingRule> rules = List.of();

        @Override
        public SamplingRule save(SamplingRule rule) {
            return rule;
        }

        @Override
        public Optional<SamplingRule> findById(Long id) {
            return rules.stream().filter(rule -> rule.getId().equals(id)).findFirst();
        }

        @Override
        public Optional<SamplingRule> findByRuleCode(String ruleCode) {
            return rules.stream().filter(rule -> rule.getRuleCode().equals(ruleCode)).findFirst();
        }

        @Override
        public List<SamplingRule> findAllActive() {
            return rules;
        }

        @Override
        public List<SamplingRule> findAll() {
            return rules;
        }

        @Override
        public void deleteById(Long id) {
            rules = rules.stream().filter(rule -> !rule.getId().equals(id)).toList();
        }
    }
}
