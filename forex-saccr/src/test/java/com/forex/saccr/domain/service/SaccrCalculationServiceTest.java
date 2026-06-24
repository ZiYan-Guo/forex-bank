package com.forex.saccr.domain.service;

import com.forex.saccr.domain.model.aggregate.SaccrResult;
import com.forex.saccr.domain.model.aggregate.SimmResult;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.*;

class SaccrCalculationServiceTest {

    private final SaccrCalculationService service = new SaccrCalculationService();

    @Test
    @DisplayName("SA-CCR exposure = alpha × (RC + PFE)")
    void testCalculate() {
        SaccrResult input = SaccrResult.create("CALC001", 1L, "TRD001", "CP001", LocalDate.now());
        // Input default RC and PFE are both ZERO, result = 1.4 × 0 = 0
        SaccrResult result = service.calculate(input);
        assertNotNull(result);
        assertEquals(0, result.getExposure().compareTo(BigDecimal.ZERO));
    }
}

class SimmCalculationServiceTest {

    private final SimmCalculationService service = new SimmCalculationService();

    @Test
    @DisplayName("SIMM total = delta + vega + curvature")
    void testCalculate() {
        SimmResult input = SimmResult.create("SIMM001", 1L, "TRD001",
                LocalDate.now(), new BigDecimal("10000000.00"));
        SimmResult result = service.calculate(input);

        assertNotNull(result);
        BigDecimal expected = new BigDecimal("500000.00")
                .add(new BigDecimal("200000.00"))
                .add(new BigDecimal("100000.00"));
        assertEquals(0, result.getTotalMargin().compareTo(expected));
    }

    @Test
    @DisplayName("SIMM with null notional returns zeros")
    void testCalculate_ZeroNotional() {
        SimmResult input = SimmResult.create("SIMM002", 2L, "TRD002",
                LocalDate.now(), BigDecimal.ZERO);
        SimmResult result = service.calculate(input);

        assertEquals(0, result.getTotalMargin().compareTo(BigDecimal.ZERO));
    }
}
