package com.forex.exchange.application.command;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDate;

@Data
public class CreateOrderCmd {

    @NotNull(message = "客户ID不能为空")
    private Long customerId;

    @NotBlank(message = "订单类型不能为空")
    private String orderType;

    @NotBlank(message = "交易方向不能为空")
    private String dealType;

    private String baseCurrency;
    private String quoteCurrency;

    @NotNull(message = "订单金额不能为空")
    @DecimalMin(value = "0.01", message = "订单金额必须大于0.01")
    private BigDecimal orderAmount;

    private String rateType;
    private LocalDate maturityDate;
    private String settlementType;
    private String channel;
    private String customerAccountNo;
    private String remark;
}
