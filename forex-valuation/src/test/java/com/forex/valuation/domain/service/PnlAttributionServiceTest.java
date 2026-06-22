package com.forex.valuation.domain.service;

import com.forex.valuation.domain.model.aggregate.PnlAttribution;
import com.forex.valuation.domain.model.valueobject.ValuationInput;
import com.forex.valuation.domain.model.valueobject.ValuationModelType;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class PnlAttributionServiceTest {

    @Mock private ValuationModelRegistry modelRegistry;
    @Mock private ValuationModel valuationModel;

    @InjectMocks
    private PnlAttributionService pnlAttributionService;

    @BeforeEach
    void setUp() {
        when(modelRegistry.getModel(any())).thenReturn(valuationModel);
        when(valuationModel.calculateFairValue(any())).thenReturn(new BigDecimal("1000000.00"));
        when(valuationModel.calculateDelta(any())).thenReturn(new BigDecimal("0.85"));
        when(valuationModel.calculateGamma(any())).thenReturn(new BigDecimal("0.05"));
        when(valuationModel.calculateVega(any())).thenReturn(new BigDecimal("0.02"));
        when(valuationModel.calculateTheta(any())).thenReturn(new BigDecimal("-500.00"));
    }

    @Test
    @DisplayName("Calculate attribution returns PnlAttribution with all components")
    void testCalculateAttribution() {
        ValuationInput today = ValuationInput.builder()
                .tradeId(1L).tradeType("FORWARD").callPut("CALL")
                .notionalAmount(new BigDecimal("1000000.00"))
                .strikePrice(new BigDecimal("7.2000"))
                .spotRate(new BigDecimal("7.2500"))
                .domesticRate(new BigDecimal("0.03"))
                .foreignRate(new BigDecimal("0.02"))
                .volatility(new BigDecimal("0.15"))
                .timeToMaturity(0.5)
                .valuationDate(LocalDate.now())
                .build();

        ValuationInput yesterday = ValuationInput.builder()
                .tradeId(1L).tradeType("FORWARD").callPut("CALL")
                .notionalAmount(new BigDecimal("1000000.00"))
                .strikePrice(new BigDecimal("7.2000"))
                .spotRate(new BigDecimal("7.2400"))
                .domesticRate(new BigDecimal("0.03"))
                .foreignRate(new BigDecimal("0.02"))
                .volatility(new BigDecimal("0.14"))
                .timeToMaturity(0.5)
                .valuationDate(LocalDate.now().minusDays(1))
                .build();

        PnlAttribution result = pnlAttributionService.calculateAttribution(
                today, yesterday, ValuationModelType.GARMAN_KOHLHAGEN);

        assertNotNull(result);
        assertEquals(1L, result.getTradeId());
        assertNotNull(result.getDeltaPnl());
        assertNotNull(result.getThetaPnl());
        assertNotNull(result.getGammaPnl());
        assertNotNull(result.getVegaPnl());
        assertNotNull(result.getCarryPnl());
        assertNotNull(result.getTotalPnl());
    }

    @Test
    @DisplayName("Total P&L equals sum of all components")
    void testCalculateAttribution_TotalEqualsSum() {
        ValuationInput input = ValuationInput.builder()
                .tradeId(2L).tradeType("SPOT").callPut("CALL")
                .notionalAmount(new BigDecimal("500000.00"))
                .spotRate(new BigDecimal("1.1000"))
                .volatility(new BigDecimal("0.12"))
                .valuationDate(LocalDate.now())
                .build();

        PnlAttribution result = pnlAttributionService.calculateAttribution(
                input, input, ValuationModelType.BLACK_SCHOLES);

        assertNotNull(result.getTotalPnl());
        BigDecimal sum = result.getDeltaPnl().add(result.getThetaPnl())
                .add(result.getGammaPnl()).add(result.getVegaPnl())
                .add(result.getCarryPnl()).add(result.getTradePnl());
        assertEquals(0, result.getTotalPnl().compareTo(sum));
    }
}
