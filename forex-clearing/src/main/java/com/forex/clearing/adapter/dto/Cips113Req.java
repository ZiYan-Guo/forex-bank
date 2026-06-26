package com.forex.clearing.adapter.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.math.BigDecimal;

@Data
@Schema(description = "CIPS.113报文生成请求")
public class Cips113Req {

    @Schema(description = "原始报文ID")
    private String originalMsgId;

    @Schema(description = "退回原因")
    private String returnReason;

    @Schema(description = "退回金额")
    private BigDecimal returnAmount;
}
