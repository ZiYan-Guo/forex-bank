package com.forex.settlement.application.command;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDate;

@Data
public class CreateGuaranteeCmd {

    private Long customerId;

    @NotBlank
    private String guaranteeType;

    @NotNull
    private BigDecimal guaranteeAmount;

    @NotBlank
    private String guaranteeCurrency;

    private String beneficiaryName;
    private LocalDate effectiveDate;
    private LocalDate expiryDate;
    private String guaranteeFormat;
    private BigDecimal commissionRate;
    private String remark;
}
