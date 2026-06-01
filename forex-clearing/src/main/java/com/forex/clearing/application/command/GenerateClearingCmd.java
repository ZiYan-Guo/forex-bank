package com.forex.clearing.application.command;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDate;

@Data
public class GenerateClearingCmd {

    @NotBlank
    private String bizType;

    @NotBlank
    private String bizNo;

    @NotBlank
    private String clearingChannel;

    private String nostroAccount;

    @NotNull
    private BigDecimal payAmount;

    @NotBlank
    private String payCurrency;

    private String receiveCurrency;

    private BigDecimal receiveAmount;

    private LocalDate valueDate;

    private LocalDate settlementDate;
}
