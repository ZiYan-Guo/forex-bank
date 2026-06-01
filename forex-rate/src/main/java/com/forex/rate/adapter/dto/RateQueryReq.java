package com.forex.rate.adapter.dto;

import java.math.BigDecimal;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Data
@Schema(description = "汇率查询请求")
public class RateQueryReq {

    @Schema(description = "货币对", example = "USD_CNY")
    private String currencyPair;

    @Schema(description = "金额", example = "1000.00")
    private BigDecimal amount;

    @Schema(description = "目标货币", example = "CNY")
    private String targetCurrency;
}
