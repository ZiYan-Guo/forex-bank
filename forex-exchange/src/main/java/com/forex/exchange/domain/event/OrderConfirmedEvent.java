package com.forex.exchange.domain.event;

import com.forex.common.base.domain.BaseDomainEvent;
import lombok.Getter;

import java.math.BigDecimal;

@Getter
public class OrderConfirmedEvent extends BaseDomainEvent {

    private final Long orderId;
    private final String orderNo;
    private final BigDecimal confirmedRate;
    private final BigDecimal settleAmount;

    public OrderConfirmedEvent(Long orderId, String orderNo, BigDecimal confirmedRate, BigDecimal settleAmount) {
        super();
        this.orderId = orderId;
        this.orderNo = orderNo;
        this.confirmedRate = confirmedRate;
        this.settleAmount = settleAmount;
    }

    @Override
    public String eventName() {
        return "ExchangeOrderConfirmed";
    }
}
