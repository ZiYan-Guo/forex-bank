package com.forex.exchange.adapter.dto;

import io.swagger.v3.oas.annotations.media.Schema;

import lombok.Data;

import java.math.BigDecimal;

@Data
@Schema(description = "金额计算请求")
public class AmountCalcReq {

    @Schema(description = "金额", example = "10000.00")
    private BigDecimal amount;

    @Schema(description = "基础货币", example = "USD")
    private String baseCurrency;

    @Schema(description = "报价货币", example = "CNY")
    private String quoteCurrency;

    @Schema(description = "交易方向: BUY/SELL", example = "BUY")
    private String dealType;
}
