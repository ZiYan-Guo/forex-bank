package com.forex.rate.application.command;

import java.math.BigDecimal;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class RateSaveCmd {

    private String currencyPair;

    @NotNull
    private String baseCurrency;

    private String quoteCurrency;

    @NotNull
    @DecimalMin("0")
    private BigDecimal bidRate;

    private BigDecimal askRate;

    private String rateSource;
}
