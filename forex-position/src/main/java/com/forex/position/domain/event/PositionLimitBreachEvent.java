package com.forex.position.domain.event;

import com.forex.common.base.domain.BaseDomainEvent;
import lombok.Getter;

import java.math.BigDecimal;

@Getter
public class PositionLimitBreachEvent extends BaseDomainEvent {

    private final Long positionId;
    private final BigDecimal limit;
    private final BigDecimal usage;

    public PositionLimitBreachEvent(Long positionId, BigDecimal limit, BigDecimal usage) {
        super();
        this.positionId = positionId;
        this.limit = limit;
        this.usage = usage;
    }

    @Override
    public String eventName() {
        return "PositionLimitBreach";
    }
}
