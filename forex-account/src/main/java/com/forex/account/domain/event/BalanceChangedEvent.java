package com.forex.account.domain.event;

import com.forex.common.base.domain.BaseDomainEvent;

import lombok.Getter;

import java.math.BigDecimal;

@Getter
public class BalanceChangedEvent extends BaseDomainEvent {

    private final Long accountId;
    private final String accountNo;
    private final BigDecimal amount;
    private final String txType;
    private final BigDecimal balanceAfter;

    public BalanceChangedEvent(Long accountId, String accountNo, BigDecimal amount,
                                String txType, BigDecimal balanceAfter) {
        super();
        this.accountId = accountId;
        this.accountNo = accountNo;
        this.amount = amount;
        this.txType = txType;
        this.balanceAfter = balanceAfter;
    }

    @Override
    public String eventName() {
        return "BalanceChanged";
    }
}
