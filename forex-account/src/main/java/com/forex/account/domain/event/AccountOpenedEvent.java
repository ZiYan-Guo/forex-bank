package com.forex.account.domain.event;

import com.forex.common.base.domain.BaseDomainEvent;

import lombok.Getter;

@Getter
public class AccountOpenedEvent extends BaseDomainEvent {

    private final Long accountId;
    private final String accountNo;
    private final Long customerId;
    private final String currency;

    public AccountOpenedEvent(Long accountId, String accountNo, Long customerId, String currency) {
        super();
        this.accountId = accountId;
        this.accountNo = accountNo;
        this.customerId = customerId;
        this.currency = currency;
    }

    @Override
    public String eventName() {
        return "AccountOpened";
    }
}
