package com.forex.settlement.domain.event;

import com.forex.common.base.domain.BaseDomainEvent;
import lombok.Getter;

import java.math.BigDecimal;

@Getter
public class LcIssuedEvent extends BaseDomainEvent {

    private final Long lcId;
    private final String lcNo;
    private final BigDecimal amount;

    public LcIssuedEvent(Long lcId, String lcNo, BigDecimal amount) {
        this.lcId = lcId;
        this.lcNo = lcNo;
        this.amount = amount;
    }

    @Override
    public String eventName() {
        return "LCIssued";
    }
}
