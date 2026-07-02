package com.forex.preciousmetal.application.command;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.math.BigDecimal;

@Data
public class SettleOrderCmd {
    @NotBlank private String orderNo;
    @NotNull private String settleCurrency;
    @NotNull private BigDecimal settleAmount;
}
