package com.forex.trading.domain.service;

import com.forex.trading.domain.event.TradeExecutedEvent;
import com.forex.trading.domain.event.TradeSettledEvent;
import com.forex.trading.domain.model.aggregate.FxTrade;
import com.forex.trading.domain.model.valueobject.SwapPoints;
import com.forex.trading.domain.repository.FxTradeRepository;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.context.ApplicationEventPublisher;

import java.math.BigDecimal;
import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class TradeDomainServiceTest {

    @Mock private FxTradeRepository fxTradeRepository;
    @Mock private ApplicationEventPublisher eventPublisher;
    @Captor private ArgumentCaptor<TradeExecutedEvent> executedCaptor;
    @Captor private ArgumentCaptor<TradeSettledEvent> settledCaptor;

    private TradeDomainService tradeDomainService;

    @BeforeEach
    void setUp() {
        tradeDomainService = new TradeDomainService(fxTradeRepository, eventPublisher);
    }

    private FxTrade createPendingTrade() {
        return FxTrade.create("TRD2026060100001", 1001L, "SPOT", "BUY",
                "USD", "CNY", new BigDecimal("1000000.00"), new BigDecimal("7240000.00"),
                new BigDecimal("7.2400"), LocalDate.now(), "ONLINE", 1001L);
    }

    @Test
    @DisplayName("Create trade saves and returns trade")
    void testCreateTrade() {
        FxTrade trade = createPendingTrade();
        when(fxTradeRepository.save(any())).thenReturn(trade);

        FxTrade result = tradeDomainService.createTrade(trade);

        assertNotNull(result);
        assertEquals("PENDING", result.getTradeStatus());
        verify(fxTradeRepository).save(trade);
    }

    @Test
    @DisplayName("Confirm trade transitions PENDING→CONFIRMED")
    void testConfirmTrade() {
        FxTrade trade = createPendingTrade();
        when(fxTradeRepository.save(any())).thenReturn(trade);

        tradeDomainService.confirmTrade(trade);

        assertEquals("CONFIRMED", trade.getTradeStatus());
        assertNotNull(trade.getConfirmTime());
    }

    @Test
    @DisplayName("Execute trade publishes TradeExecutedEvent")
    void testExecuteTrade() {
        FxTrade trade = createPendingTrade();
        trade.confirm();
        when(fxTradeRepository.save(any())).thenReturn(trade);

        tradeDomainService.executeTrade(trade);

        assertEquals("EXECUTED", trade.getTradeStatus());
        verify(eventPublisher).publishEvent(executedCaptor.capture());
        assertEquals("USD", executedCaptor.getValue().getBuyCcy());
        assertEquals("CNY", executedCaptor.getValue().getSellCcy());
    }

    @Test
    @DisplayName("Settle trade publishes TradeSettledEvent")
    void testSettleTrade() {
        FxTrade trade = createPendingTrade();
        trade.confirm();
        trade.execute();
        when(fxTradeRepository.save(any())).thenReturn(trade);

        tradeDomainService.settleTrade(trade);

        assertEquals("SETTLED", trade.getTradeStatus());
        verify(eventPublisher).publishEvent(settledCaptor.capture());
        assertEquals("TRD2026060100001", settledCaptor.getValue().getTradeNo());
    }

    @Test
    @DisplayName("Roll over trade transitions SETTLED→ROLLED_OVER")
    void testRollOverTrade() {
        FxTrade trade = createPendingTrade();
        trade.confirm();
        trade.execute();
        trade.settle();
        LocalDate newDate = LocalDate.now().plusDays(30);
        BigDecimal newRate = new BigDecimal("7.3000");
        when(fxTradeRepository.save(any())).thenReturn(trade);

        tradeDomainService.rollOverTrade(trade, newDate, newRate);

        assertEquals("ROLLED_OVER", trade.getTradeStatus());
        assertEquals(newDate, trade.getValueDate());
        assertEquals(newRate, trade.getTradeRate());
    }

    @Test
    @DisplayName("Close out trade transitions EXECUTED→CLOSED_OUT")
    void testCloseOutTrade() {
        FxTrade trade = createPendingTrade();
        trade.confirm();
        trade.execute();
        when(fxTradeRepository.save(any())).thenReturn(trade);

        tradeDomainService.closeOutTrade(trade);

        assertEquals("CLOSED_OUT", trade.getTradeStatus());
    }

    @Test
    @DisplayName("Cancel trade transitions PENDING→CANCELLED")
    void testCancelTrade() {
        FxTrade trade = createPendingTrade();
        when(fxTradeRepository.save(any())).thenReturn(trade);

        tradeDomainService.cancelTrade(trade, "Customer request");

        assertEquals("CANCELLED", trade.getTradeStatus());
    }

    @Test
    @DisplayName("Calculate forward rate applies swap points")
    void testCalculateForwardRate() {
        BigDecimal spotRate = new BigDecimal("7.2400");
        SwapPoints points = SwapPoints.of(new BigDecimal("0.0050"));

        BigDecimal fwdRate = tradeDomainService.calculateForwardRate(spotRate, points);

        assertEquals(0, fwdRate.compareTo(new BigDecimal("7.2450")));
    }

    @Test
    @DisplayName("Calculate swap amount returns buy-sell difference")
    void testCalculateSwapAmount() {
        FxTrade trade = createPendingTrade();
        BigDecimal diff = tradeDomainService.calculateSwapAmount(trade);
        assertEquals(0, diff.compareTo(new BigDecimal("-6240000.00")));
    }

    @Test
    @DisplayName("Calculate swap amount returns zero when amounts missing")
    void testCalculateSwapAmount_NoAmounts() {
        FxTrade trade = FxTrade.create("TRD002", 1001L, "SPOT", "BUY",
                "USD", "CNY", null, null,
                new BigDecimal("7.24"), LocalDate.now(), "ONLINE", 1001L);
        assertEquals(BigDecimal.ZERO, tradeDomainService.calculateSwapAmount(trade));
    }
}
