package com.forex.payment.domain.event;

import com.forex.common.base.domain.BaseDomainEvent;
import lombok.Getter;

@Getter
public class PaymentSentEvent extends BaseDomainEvent {

    private final Long paymentId;
    private final String paymentNo;
    private final String swiftRef;

    public PaymentSentEvent(Long paymentId, String paymentNo, String swiftRef) {
        this.paymentId = paymentId;
        this.paymentNo = paymentNo;
        this.swiftRef = swiftRef;
    }

    @Override
    public String eventName() {
        return "CrossBorderPaymentSent";
    }
}
