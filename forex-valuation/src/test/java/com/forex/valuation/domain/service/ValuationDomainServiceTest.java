package com.forex.valuation.domain.service;

import com.forex.valuation.domain.event.ValuationCompletedEvent;
import com.forex.valuation.domain.model.aggregate.ValuationResult;
import com.forex.valuation.domain.model.valueobject.ValuationModelType;
import com.forex.valuation.domain.repository.ValuationResultRepository;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.context.ApplicationEventPublisher;

import java.math.BigDecimal;
import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.mockito.Mockito.lenient;

@ExtendWith(MockitoExtension.class)
class ValuationDomainServiceTest {

    @Mock private ValuationResultRepository valuationResultRepository;
    @Mock private ApplicationEventPublisher eventPublisher;
    @Mock private ValuationModelRegistry modelRegistry;
    @Mock private ValuationModel valuationModel;

    @InjectMocks
    private ValuationDomainService valuationDomainService;

    @BeforeEach
    void setUp() {
        lenient().when(modelRegistry.getModel(any())).thenReturn(valuationModel);
        lenient().when(valuationModel.calculateFairValue(any())).thenReturn(new BigDecimal("1050000.00"));
    }

    @Test
    @DisplayName("Calculate valuation returns result with fair value and P&L")
    void testCalculateValuation() {
        ValuationResult input = ValuationResult.create(1L, "TRD001", "FORWARD",
                LocalDate.now(), "USD/CNY", new BigDecimal("1000000.00"), "BS");

        ValuationResult result = valuationDomainService.calculateValuation(input);

        assertNotNull(result);
        assertEquals(new BigDecimal("1050000.00"), result.getFairValue());
        assertEquals(new BigDecimal("50000.00"), result.getPnl());
        assertEquals(new BigDecimal("50000.00"), result.getCumulativePnl());
    }

    @Test
    @DisplayName("Save result publishes ValuationCompletedEvent")
    void testSaveResult() {
        ValuationResult input = ValuationResult.create(2L, "TRD002", "SPOT",
                LocalDate.now(), "EUR/USD", new BigDecimal("500000.00"), "DCF");
        input.recalculate(new BigDecimal("510000.00"), new BigDecimal("10000.00"));
        when(valuationResultRepository.save(any())).thenReturn(input);

        ValuationResult result = valuationDomainService.saveResult(input);

        assertNotNull(result);
        verify(valuationResultRepository).save(input);
        verify(eventPublisher).publishEvent(any(ValuationCompletedEvent.class));
    }

    @Test
    @DisplayName("Calculate valuation with GK method")
    void testCalculateValuation_GK() {
        ValuationResult input = ValuationResult.create(3L, "TRD003", "SPOT",
                LocalDate.now(), "USD/JPY", new BigDecimal("2000000.00"), "GK");

        ValuationResult result = valuationDomainService.calculateValuation(input);

        assertNotNull(result);
        assertTrue(result.getFairValue().compareTo(BigDecimal.ZERO) > 0);
    }

    @Test
    @DisplayName("Calculate valuation with DCF method")
    void testCalculateValuation_DCF() {
        ValuationResult input = ValuationResult.create(4L, "TRD004", "FORWARD",
                LocalDate.now(), "GBP/USD", new BigDecimal("3000000.00"), "DCF");

        ValuationResult result = valuationDomainService.calculateValuation(input);

        assertNotNull(result);
        assertTrue(result.getFairValue().compareTo(BigDecimal.ZERO) > 0);
    }
}
