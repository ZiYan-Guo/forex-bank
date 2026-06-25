package com.forex.exchange.domain.service;

import com.forex.common.base.exception.BusinessException;
import com.forex.exchange.domain.event.OrderCancelledEvent;
import com.forex.exchange.domain.event.OrderConfirmedEvent;
import com.forex.exchange.domain.model.aggregate.ExchangeOrder;
import com.forex.exchange.domain.repository.ExchangeOrderRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
@RequiredArgsConstructor
/** Exchange order domain service. Core business logic for forex exchange lifecycle. 结售汇领域服务。 */
@Transactional
public class ExchangeDomainService {

    private final ExchangeOrderRepository exchangeOrderRepository;
    private final ApplicationEventPublisher eventPublisher;

    /** Create and persist an exchange order. 创建并持久化结售汇订单。 */
    public ExchangeOrder createOrder(ExchangeOrder order) {
        ExchangeOrder saved = exchangeOrderRepository.save(order);
        log.info("Created exchange order: {}", saved.getOrderNo());
        return saved;
    }

    /** Confirm order with final rate. Validates rate not expired, calculates settle amount, publishes OrderConfirmedEvent. 确认成交，校验汇率未过期，发布确认事件。 */
    public void confirmOrder(ExchangeOrder order, BigDecimal rate) {
        if (order.isRateExpired()) {
            throw new BusinessException("汇率已过期，无法确认订单");
        }
        BigDecimal settleAmount = calculateSettleAmount(order.getOrderAmount(), rate, order.getDealType());
        order.confirm(rate);
        exchangeOrderRepository.save(order);
        eventPublisher.publishEvent(new OrderConfirmedEvent(order.getId(), order.getOrderNo(), rate, settleAmount));
        log.info("Confirmed exchange order: {}, rate: {}, settleAmount: {}", order.getOrderNo(), rate, settleAmount);
    }

    /** Lock exchange rate for the order. 锁定订单汇率。 */
    public void lockRate(ExchangeOrder order, BigDecimal rate, int seconds) {
        order.lockRate(rate, seconds);
        exchangeOrderRepository.save(order);
        log.info("Locked rate for order: {}, rate: {}, seconds: {}", order.getOrderNo(), rate, seconds);
    }

    /** Cancel a pending order. 取消待处理订单。 */
    public void cancelOrder(ExchangeOrder order, String reason) {
        order.cancel(reason);
        exchangeOrderRepository.save(order);
        eventPublisher.publishEvent(new OrderCancelledEvent(order.getId(), order.getOrderNo(), reason));
        log.info("Cancelled exchange order: {}, reason: {}", order.getOrderNo(), reason);
    }

    /** Reverse a confirmed order. 冲正已确认订单。 */
    public void reverseOrder(ExchangeOrder order) {
        order.markReversed();
        exchangeOrderRepository.save(order);
        log.info("Reversed exchange order: {}", order.getOrderNo());
    }

    /** Calculate settlement amount from amount and rate. 根据金额和汇率计算结算金额。 */
    public BigDecimal calculateSettleAmount(BigDecimal amount, BigDecimal rate, String dealType) {
        if (amount == null || rate == null) {
            return BigDecimal.ZERO;
        }
        return amount.multiply(rate);
    }
}
