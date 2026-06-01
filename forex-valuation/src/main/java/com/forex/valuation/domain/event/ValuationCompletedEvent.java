package com.forex.valuation.domain.event;

import com.forex.common.base.domain.BaseDomainEvent;
import lombok.Getter;

import java.math.BigDecimal;

@Getter
public class ValuationCompletedEvent extends BaseDomainEvent {

    private final Long tradeId;
    private final BigDecimal fairValue;

    public ValuationCompletedEvent(Long tradeId, BigDecimal fairValue) {
        super();
        this.tradeId = tradeId;
        this.fairValue = fairValue;
    }

    @Override
    public String eventName() {
        return "ValuationCompleted";
    }
}
