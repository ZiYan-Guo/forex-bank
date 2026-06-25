package com.forex.payment.adapter.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;

import lombok.Data;

import java.util.List;

@Data
@Schema(description = "批量提交支付请求")
public class BatchSubmitReq {

    @NotBlank(message = "渠道不能为空")
    @Schema(description = "支付渠道", example = "SWIFT")
    private String channel;

    @NotEmpty(message = "支付列表不能为空")
    @Valid
    @Schema(description = "支付列表")
    private List<CreatePaymentReq> payments;
}
