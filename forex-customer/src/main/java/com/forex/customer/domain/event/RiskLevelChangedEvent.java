package com.forex.customer.domain.event;

import com.forex.common.base.domain.BaseDomainEvent;

import lombok.Getter;

@Getter
public class RiskLevelChangedEvent extends BaseDomainEvent {

    private final Long customerId;
    private final Integer oldLevel;
    private final Integer newLevel;
    private final String reason;

    public RiskLevelChangedEvent(Long customerId, Integer oldLevel, Integer newLevel, String reason) {
        this.customerId = customerId;
        this.oldLevel = oldLevel;
        this.newLevel = newLevel;
        this.reason = reason;
    }

    @Override
    public String eventName() {
        return "RiskLevelChanged";
    }
}
