package com.forex.bookkeeping.application.command;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDate;

@Data
public class CreateEntryCmd {

    @NotNull
    private LocalDate voucherDate;

    @NotBlank
    private String bizType;

    private String bizNo;

    @NotBlank
    private String currency;

    @NotNull
    private BigDecimal amount;

    @NotBlank
    private String entryDirection;

    @NotBlank
    private String accountCode;

    private String oppositeAccountCode;

    @NotBlank
    private String summary;
}
