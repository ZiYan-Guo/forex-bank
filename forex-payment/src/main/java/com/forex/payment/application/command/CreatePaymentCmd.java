package com.forex.payment.application.command;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDate;

@Data
public class CreatePaymentCmd {

    @NotNull
    private Long customerId;

    @NotBlank
    private String paymentType;

    @NotNull
    @DecimalMin("0.01")
    private BigDecimal payAmount;

    @NotBlank
    private String payCurrency;

    private String beneficiaryName;
    private String beneficiaryAccount;
    private String beneficiaryBank;
    private String beneficiarySwift;
    private String beneficiaryAddress;
    private String beneficiaryCountry;

    private String senderName;
    private String senderAccount;
    private String senderAddress;

    private String intermediaryBank;
    private String payingBankCode;
    private String receivingBankCode;

    private String paymentPurpose;
    private String bankPurposeCode;
    private String chargeBearer;

    private LocalDate valueDate;

    private String remark;
}
