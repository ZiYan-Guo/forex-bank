package com.forex.trading.application.service;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

class OptionStrategyServiceTest {

    private final OptionStrategyService service = new OptionStrategyService();

    @Test
    @DisplayName("CONSERVATIVE preference recommends Range Forward")
    void testRecommendStrategies_Conservative() {
        Map<String, Object> result = service.recommendStrategies(1001L, "CONSERVATIVE",
                new BigDecimal("500000"), "USD/CNY");

        assertEquals("区间宝 Range Forward", result.get("recommendedStrategy"));
        @SuppressWarnings("unchecked")
        List<Map<String, Object>> strategies = (List<Map<String, Object>>) result.get("strategies");
        assertEquals(3, strategies.size());
    }

    @Test
    @DisplayName("Non-conservative preference recommends Risk Reversal")
    void testRecommendStrategies_Aggressive() {
        Map<String, Object> result = service.recommendStrategies(2001L, "AGGRESSIVE",
                new BigDecimal("1000000"), "EUR/USD");

        assertEquals("风险逆转 Risk Reversal", result.get("recommendedStrategy"));
    }

    @Test
    @DisplayName("Default recommends Risk Reversal for null preference")
    void testRecommendStrategies_NullPreference() {
        Map<String, Object> result = service.recommendStrategies(3001L, null,
                new BigDecimal("200000"), "GBP/USD");

        assertEquals("风险逆转 Risk Reversal", result.get("recommendedStrategy"));
    }

    @Test
    @DisplayName("Strategy list includes three strategies")
    void testStrategyListContents() {
        Map<String, Object> result = service.recommendStrategies(1001L, "BALANCED",
                new BigDecimal("750000"), "USD/JPY");

        @SuppressWarnings("unchecked")
        List<Map<String, Object>> strategies = (List<Map<String, Object>>) result.get("strategies");
        assertEquals("区间宝 Range Forward", strategies.get(0).get("name"));
        assertEquals("风险逆转 Risk Reversal", strategies.get(1).get("name"));
        assertEquals("领口期权 Collar", strategies.get(2).get("name"));
    }
}
