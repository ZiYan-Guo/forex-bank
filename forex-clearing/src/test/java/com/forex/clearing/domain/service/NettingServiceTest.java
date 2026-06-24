package com.forex.clearing.domain.service;

import com.forex.clearing.domain.model.aggregate.ClearingInstruction;
import com.forex.clearing.domain.model.valueobject.NettingPosition;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class NettingServiceTest {

    private final NettingService service = new NettingService();

    @Test
    @DisplayName("Single instruction creates positions for pay and receive currencies")
    void testCalculateBilateralNetting_Single() {
        ClearingInstruction inst = mock(ClearingInstruction.class);
        when(inst.getPayCurrency()).thenReturn("USD");
        when(inst.getPayAmount()).thenReturn(new BigDecimal("100000"));
        when(inst.getReceiveCurrency()).thenReturn("CNY");
        when(inst.getReceiveAmount()).thenReturn(new BigDecimal("724000"));

        List<NettingPosition> positions = service.calculateBilateralNetting(List.of(inst));
        assertEquals(2, positions.size());
    }

    @Test
    @DisplayName("Multiple instructions aggregate by currency")
    void testCalculateBilateralNetting_Aggregate() {
        ClearingInstruction inst1 = mock(ClearingInstruction.class);
        when(inst1.getPayCurrency()).thenReturn("USD");
        when(inst1.getPayAmount()).thenReturn(new BigDecimal("100000"));
        when(inst1.getReceiveCurrency()).thenReturn("CNY");
        when(inst1.getReceiveAmount()).thenReturn(new BigDecimal("724000"));

        ClearingInstruction inst2 = mock(ClearingInstruction.class);
        when(inst2.getPayCurrency()).thenReturn("USD");
        when(inst2.getPayAmount()).thenReturn(new BigDecimal("50000"));
        when(inst2.getReceiveCurrency()).thenReturn("EUR");
        when(inst2.getReceiveAmount()).thenReturn(new BigDecimal("45000"));

        List<NettingPosition> positions = service.calculateBilateralNetting(List.of(inst1, inst2));
        // USD should be aggregated: pay 100000+50000 = 150000
        NettingPosition usd = positions.stream()
                .filter(p -> "USD".equals(p.getCurrency())).findFirst().orElse(null);
        assertNotNull(usd);
        assertEquals(0, usd.getTotalPay().compareTo(new BigDecimal("150000")));
    }

    @Test
    @DisplayName("Empty instructions returns empty list")
    void testCalculateBilateralNetting_Empty() {
        List<NettingPosition> positions = service.calculateBilateralNetting(List.of());
        assertTrue(positions.isEmpty());
    }
}
