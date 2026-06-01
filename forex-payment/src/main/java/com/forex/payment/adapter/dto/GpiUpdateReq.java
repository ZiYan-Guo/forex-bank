package com.forex.payment.adapter.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;

import lombok.Data;

@Data
public class GpiUpdateReq {

    @NotBlank(message = "支付编号不能为空")
    @Schema(description = "支付编号", example = "PMTOUT202501011200001234")
    private String paymentNo;

    @NotBlank(message = "GPI状态不能为空")
    @Schema(description = "GPI状态", example = "CREDITED")
    private String gpiStatus;

    @Schema(description = "GPI追踪ID")
    private String trackingId;
}
