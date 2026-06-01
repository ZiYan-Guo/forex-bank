package com.forex.margin.application.command;

import jakarta.validation.constraints.NotNull;

import lombok.Data;

import java.math.BigDecimal;

@Data
public class CreateMarginCmd {

    @NotNull(message = "客户ID不能为空")
    private Long customerId;

    @NotNull(message = "交易ID不能为空")
    private Long tradeId;

    @NotNull(message = "名义本金不能为空")
    private BigDecimal notionalAmount;

    @NotNull(message = "保证金率不能为空")
    private BigDecimal marginRate;

    private String currency;
}
