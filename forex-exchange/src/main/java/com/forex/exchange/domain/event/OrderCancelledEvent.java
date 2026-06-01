package com.forex.exchange.domain.event;

import com.forex.common.base.domain.BaseDomainEvent;
import lombok.Getter;

@Getter
public class OrderCancelledEvent extends BaseDomainEvent {

    private final Long orderId;
    private final String orderNo;
    private final String reason;

    public OrderCancelledEvent(Long orderId, String orderNo, String reason) {
        super();
        this.orderId = orderId;
        this.orderNo = orderNo;
        this.reason = reason;
    }

    @Override
    public String eventName() {
        return "ExchangeOrderCancelled";
    }
}
