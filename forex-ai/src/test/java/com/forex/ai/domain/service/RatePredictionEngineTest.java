package com.forex.ai.domain.service;

import com.forex.ai.domain.model.aggregate.RatePrediction;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class RatePredictionEngineTest {

    private final RatePredictionEngine engine = new RatePredictionEngine();

    @Test
    @DisplayName("Predict returns prediction with bounds and confidence")
    void testPredict() {
        List<BigDecimal> rates = List.of(
                new BigDecimal("7.2400"), new BigDecimal("7.2410"),
                new BigDecimal("7.2380"), new BigDecimal("7.2450"),
                new BigDecimal("7.2500"));

        RatePrediction result = engine.predict("USD/CNY", "DAILY", rates);

        assertNotNull(result);
        assertEquals("USD/CNY", result.getCurrencyPair());
        assertTrue(result.getPredictedRate().compareTo(BigDecimal.ZERO) > 0);
        assertNotNull(result.getLowerBound());
        assertNotNull(result.getUpperBound());
        assertTrue(result.getConfidence().compareTo(BigDecimal.ZERO) >= 0);
    }

    @Test
    @DisplayName("Predict throws for less than 2 data points")
    void testPredict_InsufficientData() {
        assertThrows(IllegalArgumentException.class,
                () -> engine.predict("EUR/USD", "DAILY", List.of(new BigDecimal("1.10"))));
    }

    @Test
    @DisplayName("Batch predict returns empty list")
    void testBatchPredict() {
        List<RatePrediction> results = engine.batchPredict("USD/CAD", "DAILY", 3);
        assertNotNull(results);
        assertTrue(results.isEmpty());
    }
}
