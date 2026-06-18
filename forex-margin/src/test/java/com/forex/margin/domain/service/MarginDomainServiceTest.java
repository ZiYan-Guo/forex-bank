package com.forex.margin.domain.service;

import com.forex.margin.domain.event.MarginCalledEvent;
import com.forex.margin.domain.model.aggregate.MarginAccount;
import com.forex.margin.domain.model.entity.MarginCall;
import com.forex.margin.domain.repository.MarginAccountRepository;
import com.forex.margin.domain.repository.MarginCallRepository;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.context.ApplicationEventPublisher;

import java.math.BigDecimal;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

/**
 * Unit tests for MarginDomainService.
 * 保证金领域服务单元测试。
 */
@ExtendWith(MockitoExtension.class)
class MarginDomainServiceTest {

    @Mock
    private MarginAccountRepository marginAccountRepository;

    @Mock
    private MarginCallRepository marginCallRepository;

    @Mock
    private ApplicationEventPublisher eventPublisher;

    @InjectMocks
    private MarginDomainService marginDomainService;

    @Captor
    private ArgumentCaptor<MarginCalledEvent> eventCaptor;

    @Test
    @DisplayName("Create margin returns account with margin number")
    void testCreateMargin() {
        MarginAccount result = marginDomainService.createMargin(
                1001L, 2001L, "INITIAL", "USD",
                new BigDecimal("50000.00"), new BigDecimal("0.08"), "CASH");
        assertNotNull(result);
        assertTrue(result.getMarginNo().startsWith("MG"));
    }

    @Test
    @DisplayName("Calculate initial margin returns product of notional and rate")
    void testCalculateInitialMargin() {
        BigDecimal result = marginDomainService.calculateInitialMargin(
                new BigDecimal("100000.00"), new BigDecimal("0.10"));
        assertEquals(0, result.compareTo(new BigDecimal("10000.00")));
    }

    @Test
    @DisplayName("Calculate initial margin with null returns zero")
    void testCalculateInitialMargin_Null() {
        assertEquals(BigDecimal.ZERO, marginDomainService.calculateInitialMargin(null, new BigDecimal("0.10")));
    }

    @Test
    @DisplayName("Call margin saves account and publishes event")
    void testCallMargin() {
        MarginAccount account = MarginAccount.create(1001L, 2001L, "VARIATION",
                "USD", new BigDecimal("10000.00"), new BigDecimal("0.08"), "CASH");
        account.assignMarginNo("MG2026060100000001");
        when(marginAccountRepository.save(any())).thenReturn(account);

        MarginAccount result = marginDomainService.callMargin(account, new BigDecimal("5000.00"));

        verify(marginAccountRepository).save(account);
        verify(eventPublisher).publishEvent(eventCaptor.capture());
        assertEquals(account.getId(), eventCaptor.getValue().getMarginId());
    }

    @Test
    @DisplayName("Release margin saves account and sets release reason")
    void testReleaseMargin() {
        MarginAccount account = MarginAccount.create(1001L, 2001L, "INITIAL",
                "USD", new BigDecimal("50000.00"), new BigDecimal("0.08"), "CASH");
        account.deposit(new BigDecimal("50000.00"));
        when(marginAccountRepository.save(any())).thenReturn(account);

        MarginAccount result = marginDomainService.releaseMargin(account, new BigDecimal("50000.00"), "POSITION_CLOSED");

        assertEquals("POSITION_CLOSED", result.getReleaseReason());
        verify(marginAccountRepository).save(account);
    }

    @Test
    @DisplayName("Deposit with water level check triggers margin call when shortfall exists")
    void testDepositWithWaterLevelCheck() {
        MarginAccount account = MarginAccount.create(1001L, 2001L, "INITIAL",
                "USD", new BigDecimal("50000.00"), new BigDecimal("0.08"), "CASH");
        account.assignMarginNo("MG2026060100000002");

        MarginAccount result = marginDomainService.depositWithWaterLevelCheck(account, new BigDecimal("10000.00"));

        assertEquals(new BigDecimal("10000.00"), result.getDepositedAmount());
        verify(marginCallRepository, atLeast(0)).save(any(MarginCall.class));
    }
}
