package com.forex.bookkeeping.domain.event;

import com.forex.common.base.domain.BaseDomainEvent;
import lombok.Getter;

import java.math.BigDecimal;

@Getter
public class EntryPostedEvent extends BaseDomainEvent {

    private final Long entryId;
    private final String voucherNo;
    private final BigDecimal amount;

    public EntryPostedEvent(Long entryId, String voucherNo, BigDecimal amount) {
        super();
        this.entryId = entryId;
        this.voucherNo = voucherNo;
        this.amount = amount;
    }

    @Override
    public String eventName() {
        return "JournalEntryPosted";
    }
}
