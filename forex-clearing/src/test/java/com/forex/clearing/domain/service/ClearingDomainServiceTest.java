package com.forex.clearing.domain.service;

import com.forex.clearing.domain.event.InstructionSentEvent;
import com.forex.clearing.domain.model.aggregate.ClearingInstruction;
import com.forex.clearing.domain.repository.ClearingInstructionRepository;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.context.ApplicationEventPublisher;

import java.math.BigDecimal;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ClearingDomainServiceTest {

    @Mock private ClearingInstructionRepository clearingInstructionRepository;
    @Mock private ApplicationEventPublisher eventPublisher;

    private ClearingDomainService clearingDomainService;

    @BeforeEach
    void setUp() {
        clearingDomainService = new ClearingDomainService(clearingInstructionRepository, eventPublisher);
    }

    @Test
    @DisplayName("Create instruction saves and returns")
    void testCreateInstruction() {
        ClearingInstruction inst = mock(ClearingInstruction.class);
        when(clearingInstructionRepository.save(any())).thenReturn(inst);

        ClearingInstruction result = clearingDomainService.createInstruction(inst);
        assertNotNull(result);
        verify(clearingInstructionRepository).save(inst);
    }

    @Test
    @DisplayName("Send instruction publishes event")
    void testSendInstruction() {
        ClearingInstruction inst = mock(ClearingInstruction.class);
        when(inst.getId()).thenReturn(1L);
        when(inst.getInstructionNo()).thenReturn("SW001");
        when(inst.getClearingChannel()).thenReturn("SWIFT");
        when(clearingInstructionRepository.save(any())).thenReturn(inst);

        clearingDomainService.sendInstruction(inst);

        verify(inst).send();
        verify(eventPublisher).publishEvent(any(InstructionSentEvent.class));
    }

    @Test
    @DisplayName("Acknowledge throws for blank SWIFT ref")
    void testAcknowledgeInstruction_BlankRef() {
        ClearingInstruction inst = mock(ClearingInstruction.class);
        assertThrows(RuntimeException.class,
                () -> clearingDomainService.acknowledgeInstruction(inst, ""));
    }

    @Test
    @DisplayName("Settle instruction saves with calculated nostro balance")
    void testSettleInstruction() {
        ClearingInstruction inst = mock(ClearingInstruction.class);
        when(inst.getNostroBalanceBefore()).thenReturn(new BigDecimal("1000000"));
        when(inst.getPayAmount()).thenReturn(new BigDecimal("500000"));
        when(inst.getReceiveAmount()).thenReturn(new BigDecimal("200000"));
        when(clearingInstructionRepository.save(any())).thenReturn(inst);

        clearingDomainService.settleInstruction(inst);

        verify(inst).settle(any(BigDecimal.class));
    }
}
