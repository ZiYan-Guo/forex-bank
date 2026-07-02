package com.forex.limit.application.command;

import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDate;

@Data
public class CreateLimitCmd {
    @NotNull private Long customerId;
    @NotNull private String limitType;
    @NotNull private String dimension;
    @NotNull private String dimensionValue;
    @NotNull private BigDecimal limitAmount;
    @NotNull private String currency;
    @NotNull private String limitPeriod;
    @NotNull private LocalDate effectiveDate;
    @NotNull private LocalDate expiryDate;
}
