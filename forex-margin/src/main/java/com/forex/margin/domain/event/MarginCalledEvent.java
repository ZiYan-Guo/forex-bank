package com.forex.margin.domain.event;

import com.forex.common.base.domain.BaseDomainEvent;
import lombok.Getter;

import java.math.BigDecimal;

@Getter
public class MarginCalledEvent extends BaseDomainEvent {

    private final Long marginId;
    private final BigDecimal callAmount;

    public MarginCalledEvent(Long marginId, BigDecimal callAmount) {
        super();
        this.marginId = marginId;
        this.callAmount = callAmount;
    }

    @Override
    public String eventName() {
        return "MarginCalled";
    }
}
