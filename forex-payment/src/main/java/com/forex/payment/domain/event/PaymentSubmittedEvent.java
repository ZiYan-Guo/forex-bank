package com.forex.payment.domain.event;

import com.forex.common.base.domain.BaseDomainEvent;
import lombok.Getter;

import java.math.BigDecimal;

@Getter
public class PaymentSubmittedEvent extends BaseDomainEvent {

    private final Long paymentId;
    private final String paymentNo;
    private final BigDecimal amount;
    private final String currency;

    public PaymentSubmittedEvent(Long paymentId, String paymentNo, BigDecimal amount, String currency) {
        this.paymentId = paymentId;
        this.paymentNo = paymentNo;
        this.amount = amount;
        this.currency = currency;
    }

    @Override
    public String eventName() {
        return "CrossBorderPaymentSubmitted";
    }
}
