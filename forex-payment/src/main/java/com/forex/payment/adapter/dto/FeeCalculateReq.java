package com.forex.payment.adapter.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import lombok.Data;

import java.math.BigDecimal;

@Data
@Schema(description = "渠道费用计算请求")
public class FeeCalculateReq {

    @NotBlank(message = "渠道不能为空")
    @Schema(description = "支付渠道", example = "SWIFT")
    private String channel;

    @NotBlank(message = "费用承担方不能为空")
    @Schema(description = "费用承担方", example = "OUR")
    private String chargeBearer;

    @NotNull(message = "金额不能为空")
    @Schema(description = "支付金额", example = "10000.00")
    private BigDecimal amount;
}
