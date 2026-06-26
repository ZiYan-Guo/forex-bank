package com.forex.rate.domain.service;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;

import static org.junit.jupiter.api.Assertions.*;
import com.forex.common.base.exception.BusinessException;

class RateCalculationDomainServiceTest {

    private final RateCalculationDomainService service = new RateCalculationDomainService();

    @Test
    @DisplayName("Calculate cross rate divides quote by base")
    void testCalculateCrossRate() {
        BigDecimal result = service.calculateCrossRate(
                new BigDecimal("7.2400"), new BigDecimal("0.9200"));
        assertEquals(0, result.compareTo(new BigDecimal("0.12707182")));
    }

    @Test
    @DisplayName("Calculate cross rate throws for zero base rate")
    void testCalculateCrossRate_ZeroBase() {
        assertThrows(BusinessException.class,
                () -> service.calculateCrossRate(BigDecimal.ZERO, new BigDecimal("0.92")));
    }

    @Test
    @DisplayName("Apply spread adds adjust to base rate")
    void testApplySpread() {
        BigDecimal result = service.applySpread(new BigDecimal("7.2400"), new BigDecimal("0.0010"));
        assertEquals(0, result.compareTo(new BigDecimal("7.2410")));
    }

    @Test
    @DisplayName("Apply spread with negative adjust")
    void testApplySpread_Negative() {
        BigDecimal result = service.applySpread(new BigDecimal("7.2400"), new BigDecimal("-0.0020"));
        assertEquals(0, result.compareTo(new BigDecimal("7.2380")));
    }

    @Test
    @DisplayName("Calculate amount multiplies amount by rate")
    void testCalculateAmount() {
        BigDecimal result = service.calculateAmount(new BigDecimal("1000.00"), new BigDecimal("7.2456"));
        assertEquals(0, result.compareTo(new BigDecimal("7245.60")));
    }

    @Test
    @DisplayName("Calculate amount throws for zero amount")
    void testCalculateAmount_ZeroAmount() {
        assertThrows(BusinessException.class,
                () -> service.calculateAmount(BigDecimal.ZERO, new BigDecimal("7.24")));
    }

    @Test
    @DisplayName("Calculate amount throws for zero rate")
    void testCalculateAmount_ZeroRate() {
        assertThrows(BusinessException.class,
                () -> service.calculateAmount(new BigDecimal("1000.00"), BigDecimal.ZERO));
    }
}
