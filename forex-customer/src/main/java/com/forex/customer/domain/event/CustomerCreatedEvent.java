package com.forex.customer.domain.event;

import com.forex.common.base.domain.BaseDomainEvent;

import lombok.Getter;

@Getter
public class CustomerCreatedEvent extends BaseDomainEvent {

    private final Long customerId;
    private final String customerNo;
    private final String customerName;

    public CustomerCreatedEvent(Long customerId, String customerNo, String customerName) {
        this.customerId = customerId;
        this.customerNo = customerNo;
        this.customerName = customerName;
    }

    @Override
    public String eventName() {
        return "CustomerCreated";
    }
}
