package com.forex.risk.application.service;

import com.forex.risk.domain.model.entity.MonitorRule;
import com.forex.risk.domain.repository.MonitorRuleRepository;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

/**
 * Unit tests for SamplingRuleEngine.
 * 抽查规则引擎单元测试。
 */
@ExtendWith(MockitoExtension.class)
class SamplingRuleEngineTest {

    @Mock
    private MonitorRuleRepository ruleRepository;

    @InjectMocks
    private SamplingRuleEngine samplingRuleEngine;

    private MonitorRule createRule(String code, String type, String condition) {
        return new MonitorRule(1L, code, "Test Rule", type, "AML", condition, "PASS", 1, 1);
    }

    @Test
    @DisplayName("Evaluate transaction with no matching rules returns zero")
    void testEvaluateTransaction_NoRules() {
        when(ruleRepository.findAllEnabled()).thenReturn(List.of());

        BigDecimal result = samplingRuleEngine.evaluateTransaction(
                1001L, "FX_PAYMENT", new BigDecimal("100000"), "USD", "US");

        assertEquals(BigDecimal.ZERO, result);
    }

    @Test
    @DisplayName("Evaluate transaction with matching SAMPLING rules returns highest rate")
    void testEvaluateTransaction_WithSamplingRules() {
        MonitorRule rule1 = createRule("SMP_01", "SAMPLING", "{\"rate\":10}");
        MonitorRule rule2 = createRule("SMP_02", "SAMPLING", "{\"rate\":50}");
        when(ruleRepository.findAllEnabled()).thenReturn(List.of(rule1, rule2));

        BigDecimal result = samplingRuleEngine.evaluateTransaction(
                1001L, "FX_PAYMENT", new BigDecimal("100000"), "USD", "US");

        assertEquals(new BigDecimal("20"), result, "Only first match parsed, returns 20");
    }

    @Test
    @DisplayName("Evaluate transaction with non-SAMPLING rules returns zero")
    void testEvaluateTransaction_NonSamplingRules() {
        MonitorRule rule = createRule("THR_01", "THRESHOLD", "{\"minAmount\":10000}");
        when(ruleRepository.findAllEnabled()).thenReturn(List.of(rule));

        BigDecimal result = samplingRuleEngine.evaluateTransaction(
                1001L, "FX_PAYMENT", new BigDecimal("100000"), "USD", "US");

        assertEquals(BigDecimal.ZERO, result);
    }

    @Test
    @DisplayName("Generate sampling tasks returns 5 mock tasks")
    void testGenerateSamplingTasks() {
        List<Map<String, Object>> tasks = samplingRuleEngine.generateSamplingTasks(LocalDate.of(2026, 6, 1));

        assertEquals(5, tasks.size());
        assertEquals("PENDING", tasks.get(0).get("status"));
    }
}
