package com.forex.clearing.application.service;

import com.forex.clearing.domain.model.valueobject.SettlementRoute;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;

import static org.junit.jupiter.api.Assertions.*;

class RouteOptimizationServiceTest {

    private final RouteOptimizationService service = new RouteOptimizationService();

    @Test
    @DisplayName("Optimize route returns RECOMMENDED route for large amount")
    void testOptimizeRoute_LargeAmount() {
        SettlementRoute route = service.optimizeRoute("USD", "CNY",
                new BigDecimal("500000"), "US");

        assertNotNull(route);
        assertTrue(route.getRouteScore().compareTo(new BigDecimal("0")) > 0);
    }

    @Test
    @DisplayName("CIPS scores highest due to same-day settlement")
    void testOptimizeRoute_PrefersCips() {
        SettlementRoute route = service.optimizeRoute("EUR", "USD",
                new BigDecimal("50000"), "DE");

        assertNotNull(route);
        assertEquals("CIPS", route.getChannelCode());
    }

    @Test
    @DisplayName("Large amount gets RECOMMENDED status")
    void testOptimizeRoute_Recommended() {
        SettlementRoute route = service.optimizeRoute("JPY", "USD",
                new BigDecimal("200000"), "JP");

        assertEquals("RECOMMENDED", route.getRecommendation());
    }
}
