package com.forex.exchange.application.service;

import com.forex.common.base.dto.PageResp;
import com.forex.exchange.application.command.CancelOrderCmd;
import com.forex.exchange.application.command.CreateOrderCmd;
import com.forex.exchange.domain.model.query.ExchangeOrderQuery;
import com.forex.exchange.domain.model.aggregate.ExchangeOrder;
import com.forex.exchange.domain.model.entity.ExchangeQuote;
import com.forex.exchange.domain.repository.ExchangeOrderRepository;
import com.forex.exchange.domain.repository.ExchangeQuoteRepository;
import com.forex.exchange.domain.service.ExchangeDomainService;
import com.forex.exchange.domain.service.QuoteDomainService;

import lombok.RequiredArgsConstructor;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import com.forex.common.base.exception.BusinessException;
import com.forex.common.base.result.ResultCode;

@Service
@RequiredArgsConstructor
/** Exchange application service. Coordinates order creation, rate locking, and confirm/cancel/reverse. 结售汇应用服务。 */
public class ExchangeAppService {

    private final ExchangeOrderRepository exchangeOrderRepository;
    private final ExchangeQuoteRepository exchangeQuoteRepository;
    private final ExchangeDomainService exchangeDomainService;
    private final QuoteDomainService quoteDomainService;

    /** Create spot/forward/pending exchange order. 创建即期/远期/挂单结售汇订单。 */
    public ExchangeOrder createSpotOrder(CreateOrderCmd cmd) {
        ExchangeOrder order = ExchangeOrder.create(
                cmd.getCustomerId(), cmd.getOrderType(), cmd.getDealType(),
                cmd.getBaseCurrency(), cmd.getQuoteCurrency(),
                cmd.getOrderAmount(), cmd.getChannel());
        order = exchangeDomainService.createOrder(order);
        applyOrderDefaults(order, cmd);
        return exchangeOrderRepository.findByOrderNo(order.getOrderNo()).orElse(order);
    }

    /** Create spot/forward/pending exchange order. 创建即期/远期/挂单结售汇订单。 */
    public ExchangeOrder createForwardOrder(CreateOrderCmd cmd) {
        ExchangeOrder order = ExchangeOrder.create(
                cmd.getCustomerId(), cmd.getOrderType(), cmd.getDealType(),
                cmd.getBaseCurrency(), cmd.getQuoteCurrency(),
                cmd.getOrderAmount(), cmd.getChannel());
        order = exchangeDomainService.createOrder(order);
        applyOrderDefaults(order, cmd);
        return exchangeOrderRepository.findByOrderNo(order.getOrderNo()).orElse(order);
    }

    /** Create spot/forward/pending exchange order. 创建即期/远期/挂单结售汇订单。 */
    public ExchangeOrder createPendingOrder(CreateOrderCmd cmd) {
        ExchangeOrder order = ExchangeOrder.create(
                cmd.getCustomerId(), cmd.getOrderType(), cmd.getDealType(),
                cmd.getBaseCurrency(), cmd.getQuoteCurrency(),
                cmd.getOrderAmount(), cmd.getChannel());
        order = exchangeDomainService.createOrder(order);
        applyOrderDefaults(order, cmd);
        return exchangeOrderRepository.findByOrderNo(order.getOrderNo()).orElse(order);
    }

    /** Apply default values (value date, account info) to order. 填充默认值到订单。 */
    private void applyOrderDefaults(ExchangeOrder order, CreateOrderCmd cmd) {
        order.assignDates(calculateValueDate("T+2"), cmd.getMaturityDate());
        order.assignAccountInfo(cmd.getCustomerAccountNo(), BigDecimal.ZERO,
                BigDecimal.ZERO, cmd.getSettlementType());
        if (cmd.getRateType() != null) {
            order.assignRate(null, null, cmd.getRateType());
        }
        if (cmd.getRemark() != null) {
            order.assignRemark(cmd.getRemark());
        }
    }

    /** Calculate T+2 value date skipping weekends. 计算T+2交割日(跳过周末)。 */
    private LocalDate calculateValueDate(String tenor) {
        if ("T+2".equals(tenor)) {
            LocalDate today = LocalDate.now();
            int daysAdded = 0;
            LocalDate result = today;
            while (daysAdded < 2) {
                result = result.plusDays(1);
                if (result.getDayOfWeek().getValue() >= 6) {
                    continue;
                }
                daysAdded++;
            }
            return result;
        }
        return LocalDate.now().plusDays(2);
    }

    public ExchangeOrder getOrderDetail(String orderNo) {
        return exchangeOrderRepository.findByOrderNo(orderNo)
                .orElseThrow(() -> new BusinessException(ResultCode.NOT_FOUND, "订单不存在"));
    }

    public PageResp<ExchangeOrder> pageQuery(ExchangeOrderQuery query) {
        return exchangeOrderRepository.pageQuery(query);
    }

    /** @Transactional. Lock rate with optimistic lock via version field. 锁汇，通过version乐观锁防并发。 */
    @Transactional
    public ExchangeOrder lockRate(String orderNo, BigDecimal confirmedRate) {
        ExchangeOrder order = exchangeOrderRepository.findByOrderNo(orderNo)
                .orElseThrow(() -> new BusinessException(ResultCode.NOT_FOUND, "订单不存在"));
        exchangeDomainService.lockRate(order, confirmedRate, 300);
        return exchangeOrderRepository.findByOrderNo(orderNo).orElse(order);
    }

    /** @Transactional. Confirm order using correct rate (askRate for BUY, bidRate for SELL). 确认订单，BUY用askRate，SELL用bidRate。 */
    @Transactional
    public ExchangeOrder confirmOrder(String orderNo) {
        ExchangeOrder order = exchangeOrderRepository.findByOrderNo(orderNo)
                .orElseThrow(() -> new BusinessException(ResultCode.NOT_FOUND, "订单不存在"));
        BigDecimal rate = "BUY".equals(order.getDealType())
                ? (order.getAskRate() != null ? order.getAskRate() : BigDecimal.ZERO)
                : (order.getBidRate() != null ? order.getBidRate() : BigDecimal.ZERO);
        exchangeDomainService.confirmOrder(order, rate);
        return exchangeOrderRepository.findByOrderNo(orderNo).orElse(order);
    }

    @Transactional
    public void cancelOrder(String orderNo, String reason) {
        ExchangeOrder order = exchangeOrderRepository.findByOrderNo(orderNo)
                .orElseThrow(() -> new BusinessException(ResultCode.NOT_FOUND, "订单不存在"));
        exchangeDomainService.cancelOrder(order, reason);
    }

    @Transactional
    public void reverseOrder(String orderNo) {
        ExchangeOrder order = exchangeOrderRepository.findByOrderNo(orderNo)
                .orElseThrow(() -> new BusinessException(ResultCode.NOT_FOUND, "订单不存在"));
        exchangeDomainService.reverseOrder(order);
    }

    public ExchangeQuote getQuote(Long customerId, String baseCcy, String quoteCcy) {
        return exchangeQuoteRepository.findLatestQuote(customerId, baseCcy, quoteCcy).orElse(null);
    }

    public BigDecimal calculateAmount(BigDecimal amount, String baseCcy, String quoteCcy, String dealType) {
        ExchangeQuote quote = getQuote(null, baseCcy, quoteCcy);
        if (quote == null) {
            return BigDecimal.ZERO;
        }
        BigDecimal rate = "BUY".equals(dealType) ? quote.getAskRate() : quote.getBidRate();
        if (rate == null) {
            return BigDecimal.ZERO;
        }
        return amount.multiply(rate).setScale(2, RoundingMode.HALF_UP);
    }
}
