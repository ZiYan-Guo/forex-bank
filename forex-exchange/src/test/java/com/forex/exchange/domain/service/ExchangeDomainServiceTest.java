package com.forex.exchange.domain.service;

import com.forex.exchange.domain.event.OrderCancelledEvent;
import com.forex.exchange.domain.event.OrderConfirmedEvent;
import com.forex.exchange.domain.model.aggregate.ExchangeOrder;
import com.forex.exchange.domain.repository.ExchangeOrderRepository;

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

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ExchangeDomainServiceTest {

    @Mock private ExchangeOrderRepository exchangeOrderRepository;
    @Mock private ApplicationEventPublisher eventPublisher;
    @Captor private ArgumentCaptor<OrderConfirmedEvent> confirmedCaptor;
    @Captor private ArgumentCaptor<OrderCancelledEvent> cancelledCaptor;

    private ExchangeDomainService exchangeDomainService;

    @BeforeEach
    void setUp() {
        exchangeDomainService = new ExchangeDomainService(exchangeOrderRepository, eventPublisher);
    }

    private ExchangeOrder createPendingOrder() {
        return ExchangeOrder.create(1001L, "SPOT", "BUY",
                "USD", "CNY", new BigDecimal("10000.00"), "ONLINE");
    }

    @Test
    @DisplayName("Create order saves and returns the order")
    void testCreateOrder() {
        ExchangeOrder order = createPendingOrder();
        when(exchangeOrderRepository.save(any())).thenReturn(order);

        ExchangeOrder result = exchangeDomainService.createOrder(order);

        assertNotNull(result);
        assertEquals("PENDING", result.getOrderStatus());
        verify(exchangeOrderRepository).save(order);
    }

    @Test
    @DisplayName("Confirm order publishes OrderConfirmedEvent")
    void testConfirmOrder() {
        ExchangeOrder order = createPendingOrder();
        BigDecimal rate = new BigDecimal("7.2400");
        when(exchangeOrderRepository.save(any())).thenReturn(order);

        exchangeDomainService.confirmOrder(order, rate);

        assertEquals("CONFIRMED", order.getOrderStatus());
        verify(eventPublisher).publishEvent(confirmedCaptor.capture());
        assertEquals(rate, confirmedCaptor.getValue().getConfirmedRate());
    }

    @Test
    @DisplayName("Confirm order with expired rate throws exception")
    void testConfirmOrder_RateExpired() {
        ExchangeOrder order = createPendingOrder();
        order.lockRate(new BigDecimal("7.2400"), 1);
        try { Thread.sleep(1500); } catch (InterruptedException e) { }

        assertThrows(RuntimeException.class,
                () -> exchangeDomainService.confirmOrder(order, new BigDecimal("7.2400")));
    }

    @Test
    @DisplayName("Lock rate sets RATE_LOCKED status")
    void testLockRate() {
        ExchangeOrder order = createPendingOrder();
        when(exchangeOrderRepository.save(any())).thenReturn(order);

        exchangeDomainService.lockRate(order, new BigDecimal("7.2400"), 60);

        assertEquals("RATE_LOCKED", order.getOrderStatus());
        assertNotNull(order.getLockRateTime());
        assertNotNull(order.getLockRateExpireTime());
    }

    @Test
    @DisplayName("Cancel order publishes OrderCancelledEvent")
    void testCancelOrder() {
        ExchangeOrder order = createPendingOrder();
        when(exchangeOrderRepository.save(any())).thenReturn(order);

        exchangeDomainService.cancelOrder(order, "Customer withdraw");

        assertEquals("CANCELLED", order.getOrderStatus());
        verify(eventPublisher).publishEvent(cancelledCaptor.capture());
        assertEquals("Customer withdraw", cancelledCaptor.getValue().getReason());
    }

    @Test
    @DisplayName("Reverse order sets REVERSED status")
    void testReverseOrder() {
        ExchangeOrder order = createPendingOrder();
        order.confirm(new BigDecimal("7.2400"));
        when(exchangeOrderRepository.save(any())).thenReturn(order);

        exchangeDomainService.reverseOrder(order);

        assertEquals("REVERSED", order.getOrderStatus());
    }

    @Test
    @DisplayName("Calculate settle amount multiplies amount by rate")
    void testCalculateSettleAmount() {
        BigDecimal result = exchangeDomainService.calculateSettleAmount(
                new BigDecimal("10000.00"), new BigDecimal("7.2400"), "BUY");
        assertEquals(0, result.compareTo(new BigDecimal("72400.000")));
    }

    @Test
    @DisplayName("Calculate settle amount returns zero for null inputs")
    void testCalculateSettleAmount_Null() {
        assertEquals(BigDecimal.ZERO,
                exchangeDomainService.calculateSettleAmount(null, new BigDecimal("7.24"), "BUY"));
    }
}
