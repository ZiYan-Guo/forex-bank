package com.forex.rate.domain.event;

import java.math.BigDecimal;

import com.forex.common.base.domain.BaseDomainEvent;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class RateUpdatedEvent extends BaseDomainEvent {

    private static final long serialVersionUID = 1L;

    private final String currencyPair;
    private final BigDecimal bidRate;
    private final BigDecimal askRate;

    @Override
    public String eventName() {
        return "RateUpdated";
    }
}
