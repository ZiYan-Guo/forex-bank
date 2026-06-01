package com.forex.payment.adapter.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;

import lombok.Data;

@Data
public class SendPaymentReq {

    @NotBlank(message = "支付编号不能为空")
    @Schema(description = "支付编号", example = "PMTOUT202501011200001234")
    private String paymentNo;

    @Schema(description = "SWIFT参考号")
    private String swiftRef;

    @Schema(description = "CIPS参考号")
    private String cipsRef;
}
