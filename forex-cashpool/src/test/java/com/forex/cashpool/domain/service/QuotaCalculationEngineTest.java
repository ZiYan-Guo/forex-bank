package com.forex.cashpool.domain.service;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.math.RoundingMode;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Unit tests for QuotaCalculationEngine.
 * 额度计算引擎单元测试。
 */
class QuotaCalculationEngineTest {

    private final QuotaCalculationEngine engine = new QuotaCalculationEngine();

    @Test
    @DisplayName("Calculate debt limit with valid net assets")
    void testCalculateDebtLimit_Valid() {
        BigDecimal result = engine.calculateDebtLimit(new BigDecimal("1000000.00"));
        assertEquals(new BigDecimal("3500000.00"), result);
    }

    @Test
    @DisplayName("Calculate debt limit with zero net assets returns zero")
    void testCalculateDebtLimit_Zero() {
        assertEquals(BigDecimal.ZERO, engine.calculateDebtLimit(BigDecimal.ZERO));
    }

    @Test
    @DisplayName("Calculate debt limit with null returns zero")
    void testCalculateDebtLimit_Null() {
        assertEquals(BigDecimal.ZERO, engine.calculateDebtLimit(null));
    }

    @Test
    @DisplayName("Calculate lending limit with valid net assets")
    void testCalculateLendingLimit_Valid() {
        BigDecimal result = engine.calculateLendingLimit(new BigDecimal("5000000.00"));
        assertEquals(new BigDecimal("4000000.00"), result);
    }

    @Test
    @DisplayName("Calculate lending limit with zero returns zero")
    void testCalculateLendingLimit_Zero() {
        assertEquals(BigDecimal.ZERO, engine.calculateLendingLimit(BigDecimal.ZERO));
    }

    @Test
    @DisplayName("Calculate usage percentage correctly")
    void testCalculateUsagePct() {
        BigDecimal pct = engine.calculateUsagePct(
                new BigDecimal("250000.00"), new BigDecimal("1000000.00"));
        assertEquals(new BigDecimal("25.0000"), pct.setScale(4, RoundingMode.HALF_UP));
    }

    @Test
    @DisplayName("Calculate usage percentage with zero total returns zero")
    void testCalculateUsagePct_ZeroTotal() {
        assertEquals(BigDecimal.ZERO, engine.calculateUsagePct(
                new BigDecimal("100.00"), BigDecimal.ZERO));
    }

    @Test
    @DisplayName("Calculate usage percentage with null total returns zero")
    void testCalculateUsagePct_NullTotal() {
        assertEquals(BigDecimal.ZERO, engine.calculateUsagePct(
                new BigDecimal("100.00"), null));
    }
}
