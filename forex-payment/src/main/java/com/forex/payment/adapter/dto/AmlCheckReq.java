package com.forex.payment.adapter.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import lombok.Data;

@Data
public class AmlCheckReq {

    @NotBlank(message = "支付编号不能为空")
    @Schema(description = "支付编号", example = "PMTOUT202501011200001234")
    private String paymentNo;

    @NotNull(message = "检查结果不能为空")
    @Schema(description = "是否通过", example = "true")
    private Boolean passed;

    @Schema(description = "原因")
    private String reason;
}
