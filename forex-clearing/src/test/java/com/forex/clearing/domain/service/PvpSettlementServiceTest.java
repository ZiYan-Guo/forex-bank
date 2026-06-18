package com.forex.clearing.domain.service;

import com.forex.clearing.domain.model.aggregate.ClearingInstruction;
import com.forex.clearing.domain.model.aggregate.PvpSettlementPair;
import com.forex.clearing.domain.repository.ClearingInstructionRepository;
import com.forex.clearing.domain.repository.PvpSettlementRepository;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.context.ApplicationEventPublisher;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class PvpSettlementServiceTest {

    @Mock private PvpSettlementRepository pairRepository;
    @Mock private ClearingInstructionRepository instructionRepository;
    @Mock private ApplicationEventPublisher eventPublisher;

    @InjectMocks
    private PvpSettlementService pvpSettlementService;

    @Test
    @DisplayName("Create PVP pair saves and returns pair with PVP prefix")
    void testCreatePvpPair() {
        ClearingInstruction payLeg = mock(ClearingInstruction.class);
        ClearingInstruction recLeg = mock(ClearingInstruction.class);
        when(payLeg.getInstructionNo()).thenReturn("INST2026060001");
        when(payLeg.getPayCurrency()).thenReturn("USD");
        when(payLeg.getPayAmount()).thenReturn(new BigDecimal("100000.00"));
        when(payLeg.getValueDate()).thenReturn(LocalDate.now());
        when(recLeg.getInstructionNo()).thenReturn("INST2026060002");
        when(recLeg.getReceiveCurrency()).thenReturn("CNY");
        when(recLeg.getReceiveAmount()).thenReturn(new BigDecimal("724000.00"));
        when(instructionRepository.findById(1L)).thenReturn(Optional.of(payLeg));
        when(instructionRepository.findById(2L)).thenReturn(Optional.of(recLeg));
        when(pairRepository.save(any())).thenAnswer(i -> i.getArgument(0));

        PvpSettlementPair result = pvpSettlementService.createPvpPair(1L, 2L);

        assertNotNull(result);
        assertTrue(result.getPairId().startsWith("PVP"));
        verify(pairRepository).save(any());
    }

    @Test
    @DisplayName("Execute PVP settlement completes both legs")
    void testExecutePvpSettlement() {
        PvpSettlementPair pair = mock(PvpSettlementPair.class);
        when(pair.getPayInstructionId()).thenReturn(1L);
        when(pair.getReceiveInstructionId()).thenReturn(2L);
        when(pairRepository.findByPairId("PVP001")).thenReturn(Optional.of(pair));

        ClearingInstruction payLeg = mock(ClearingInstruction.class);
        ClearingInstruction recLeg = mock(ClearingInstruction.class);
        when(payLeg.getNostroBalanceBefore()).thenReturn(new BigDecimal("500000.00"));
        when(recLeg.getNostroBalanceBefore()).thenReturn(new BigDecimal("300000.00"));
        when(instructionRepository.findById(1L)).thenReturn(Optional.of(payLeg));
        when(instructionRepository.findById(2L)).thenReturn(Optional.of(recLeg));

        pvpSettlementService.executePvpSettlement("PVP001");

        verify(pair).attempt();
        verify(payLeg).settle(any());
        verify(recLeg).settle(any());
        verify(pair).settle();
        verify(pairRepository).save(pair);
        verify(instructionRepository, times(2)).save(any());
    }

    @Test
    @DisplayName("Execute PVP settlement marks pair as failed on exception")
    void testExecutePvpSettlement_Failure() {
        PvpSettlementPair pair = mock(PvpSettlementPair.class);
        when(pair.getPayInstructionId()).thenReturn(1L);
        when(pairRepository.findByPairId("PVP001")).thenReturn(Optional.of(pair));
        when(instructionRepository.findById(1L)).thenThrow(new RuntimeException("DB error"));

        pvpSettlementService.executePvpSettlement("PVP001");

        verify(pair).fail(anyString());
        verify(pairRepository).save(pair);
    }
}
