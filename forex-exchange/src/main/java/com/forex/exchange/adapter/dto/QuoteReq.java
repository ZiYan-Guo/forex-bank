package com.forex.exchange.adapter.dto;

import io.swagger.v3.oas.annotations.media.Schema;

import lombok.Data;

@Data
@Schema(description = "报价请求")
public class QuoteReq {

    @Schema(description = "客户ID", example = "1")
    private Long customerId;

    @Schema(description = "基础货币", example = "USD")
    private String baseCurrency;

    @Schema(description = "报价货币", example = "CNY")
    private String quoteCurrency;
}
