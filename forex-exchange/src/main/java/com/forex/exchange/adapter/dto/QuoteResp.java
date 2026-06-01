package com.forex.exchange.adapter.dto;

import io.swagger.v3.oas.annotations.media.Schema;

import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@Schema(description = "报价响应")
public class QuoteResp {

    @Schema(description = "买入价")
    private BigDecimal bidRate;

    @Schema(description = "卖出价")
    private BigDecimal askRate;

    @Schema(description = "中间价")
    private BigDecimal midRate;

    @Schema(description = "报价时间")
    private LocalDateTime quoteTime;

    @Schema(description = "过期时间")
    private LocalDateTime expireTime;
}
