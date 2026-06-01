package com.forex.customer.adapter.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;

import lombok.Data;

import java.math.BigDecimal;

@Data
public class CreditCheckReq {

    @NotNull(message = "客户ID不能为空")
    @Schema(description = "客户ID", example = "1")
    private Long customerId;

    @NotNull(message = "额度类型不能为空")
    @Schema(description = "额度类型", example = "FOREX_LIMIT")
    private String limitType;

    @NotNull(message = "币种不能为空")
    @Schema(description = "币种", example = "USD")
    private String currency;

    @NotNull(message = "金额不能为空")
    @Schema(description = "金额", example = "10000.00")
    private BigDecimal amount;
}
