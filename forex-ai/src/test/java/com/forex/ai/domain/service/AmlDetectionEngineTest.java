package com.forex.ai.domain.service;

import com.forex.ai.domain.model.aggregate.RiskAiAssessment;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class AmlDetectionEngineTest {

    private final AmlDetectionEngine engine = new AmlDetectionEngine();

    @Test
    @DisplayName("Small amount scores zero")
    void testCalculateAmlScore_SmallAmount() {
        BigDecimal score = engine.calculateAmlScore(new BigDecimal("10000.00"), 0, "US");
        assertEquals(0, score.compareTo(BigDecimal.ZERO));
    }

    @Test
    @DisplayName("Amount over 50k adds 0.30")
    void testCalculateAmlScore_Over50k() {
        BigDecimal score = engine.calculateAmlScore(new BigDecimal("60000.00"), 0, "US");
        assertEquals(0, score.compareTo(new BigDecimal("0.30")));
    }

    @Test
    @DisplayName("Amount over 100k adds 0.55")
    void testCalculateAmlScore_Over100k() {
        BigDecimal score = engine.calculateAmlScore(new BigDecimal("120000.00"), 0, "US");
        assertEquals(0, score.compareTo(new BigDecimal("0.55")));
    }

    @Test
    @DisplayName("Amount over 500k and high-risk country caps at 1.00")
    void testCalculateAmlScore_MaxRisk() {
        BigDecimal score = engine.calculateAmlScore(new BigDecimal("600000.00"), 5, "IR");
        assertEquals(0, score.compareTo(new BigDecimal("1.00")));
    }

    @Test
    @DisplayName("High frequency adds score")
    void testCalculateAmlScore_HighFrequency() {
        BigDecimal score = engine.calculateAmlScore(new BigDecimal("5000.00"), 5, "US");
        assertEquals(0, score.compareTo(new BigDecimal("0.20")));
    }

    @Test
    @DisplayName("Evaluate transaction returns LOW for small amount")
    void testEvaluateTransaction_Low() {
        RiskAiAssessment result = engine.evaluateTransaction(1L, "TXN001",
                new BigDecimal("1000.00"), "USD", "OUTWARD");
        assertEquals("LOW", result.getRiskLevel());
    }

    @Test
    @DisplayName("Evaluate transaction returns HIGH for large high-risk")
    void testEvaluateTransaction_High() {
        RiskAiAssessment result = engine.evaluateTransaction(2L, "TXN002",
                new BigDecimal("200000.00"), "USD", "INWARD");
        assertTrue("HIGH".equals(result.getRiskLevel()) || "PROHIBITED".equals(result.getRiskLevel()));
    }

    @Test
    @DisplayName("Analyze pattern handles empty list")
    void testAnalyzePattern_Empty() {
        String result = engine.analyzePattern(List.of());
        assertTrue(result.contains("无法进行模式分析"));
    }
}
