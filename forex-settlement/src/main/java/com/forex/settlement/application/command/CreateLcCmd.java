package com.forex.settlement.application.command;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDate;

@Data
public class CreateLcCmd {

    @NotNull
    private Long customerId;

    @NotBlank
    private String lcType;

    private String lcDirection;

    @NotNull
    @DecimalMin("0.01")
    private BigDecimal lcAmount;

    @NotBlank
    private String lcCurrency;

    private String applicantName;
    private String applicantAddress;
    private String beneficiaryName;
    private String beneficiaryAccount;
    private String beneficiaryBank;
    private String issuingBank;
    private String advisingBank;
    private LocalDate issueDate;
    private LocalDate expiryDate;
    private String availableBy;
    private String goodsDescription;
    private String documentsRequired;
    private BigDecimal marginPct;
    private BigDecimal feeAmount;
    private String remark;
}
