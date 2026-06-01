package com.forex.payment.application.command;

import jakarta.validation.constraints.NotBlank;

import lombok.Data;

@Data
public class SendPaymentCmd {

    @NotBlank
    private String paymentNo;

    private String swiftRef;

    private String cipsRef;
}
