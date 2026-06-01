package com.forex.exchange.adapter.dto;

import io.swagger.v3.oas.annotations.media.Schema;

import lombok.Data;

import java.math.BigDecimal;

@Data
@Schema(description = "锁汇请求")
public class RateLockReq {

    @Schema(description = "订单号", example = "EX202505280001")
    private String orderNo;

    @Schema(description = "确认汇率", example = "7.1234")
    private BigDecimal confirmedRate;
}
