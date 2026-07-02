package com.forex.preciousmetal.application.command;

import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDate;

@Data
public class CreateOrderCmd {
    @NotNull private Long customerId;
    @NotNull private String metalType;
    @NotNull private String tradeType;
    @NotNull private String direction;
    @NotNull private BigDecimal weight;
    @NotNull private String weightUnit;
    @NotNull private BigDecimal purity;
    @NotNull private BigDecimal unitPrice;
    @NotNull private String currency;
    @NotNull private String deliveryType;
    @NotNull private LocalDate valueDate;
    private String deliveryLocation;
}
