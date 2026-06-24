package com.forex.exchange.application.service;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

class EnterpriseQuoteServiceTest {

    private final EnterpriseQuoteService service = new EnterpriseQuoteService();

    @Test
    @DisplayName("Enterprise quote returns 3 tiers and recommends PREMIUM")
    void testGetEnterpriseQuote() {
        Map<String, Object> result = service.getEnterpriseQuote(1001L, "USD/CNY",
                new BigDecimal("500000"), "BUY");

        assertEquals(1001L, result.get("customerId"));
        assertEquals("PREMIUM", result.get("recommendedTier"));
        @SuppressWarnings("unchecked")
        List<Map<String, Object>> tiers = (List<Map<String, Object>>) result.get("availableTiers");
        assertEquals(3, tiers.size());
        assertEquals("STANDARD", tiers.get(0).get("tier"));
        assertEquals("PREMIUM", tiers.get(1).get("tier"));
        assertEquals("VIP", tiers.get(2).get("tier"));
    }

    @Test
    @DisplayName("Tiers have progressively better rates")
    void testTierRateProgression() {
        Map<String, Object> result = service.getEnterpriseQuote(2001L, "EUR/USD",
                new BigDecimal("1000000"), "SELL");

        @SuppressWarnings("unchecked")
        List<Map<String, Object>> tiers = (List<Map<String, Object>>) result.get("availableTiers");
        double standardRate = (Double) tiers.get(0).get("rate");
        double premiumRate = (Double) tiers.get(1).get("rate");
        double vipRate = (Double) tiers.get(2).get("rate");
        assertTrue(standardRate > premiumRate);
        assertTrue(premiumRate > vipRate);
    }
}
