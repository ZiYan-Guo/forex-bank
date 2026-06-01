package com.forex.settlement.domain.event;

import com.forex.common.base.domain.BaseDomainEvent;
import lombok.Getter;

@Getter
public class LcDocCheckedEvent extends BaseDomainEvent {

    private final Long lcId;
    private final String lcNo;
    private final boolean discrepancy;

    public LcDocCheckedEvent(Long lcId, String lcNo, boolean discrepancy) {
        this.lcId = lcId;
        this.lcNo = lcNo;
        this.discrepancy = discrepancy;
    }

    @Override
    public String eventName() {
        return "LcDocChecked";
    }
}
