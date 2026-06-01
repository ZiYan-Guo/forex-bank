package com.forex.trading.application.command;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDate;

@Data
public class CreateTradeCmd {

    @NotNull
    private Long customerId;

    @NotBlank
    private String tradeType;

    @NotBlank
    private String dealType;

    private String buyCurrency;
    private String sellCurrency;

    @NotNull
    @DecimalMin("0.01")
    private BigDecimal buyAmount;

    private BigDecimal sellAmount;
    private BigDecimal tradeRate;
    private LocalDate valueDate;
    private LocalDate maturityDate;
    private LocalDate nearValueDate;
    private LocalDate farValueDate;
    private BigDecimal nearRate;
    private BigDecimal farRate;
    private BigDecimal swapPoints;
    private String optionType;
    private BigDecimal strikePrice;
    private BigDecimal premiumAmount;
    private String premiumCurrency;
    private LocalDate premiumDate;
    private LocalDate expiryDate;
    private String deliveryType;
    private String counterparty;
    private String nostroAccount;
    private String tradeChannel;
    private String remark;
}
