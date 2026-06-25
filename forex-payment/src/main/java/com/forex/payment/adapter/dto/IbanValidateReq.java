package com.forex.payment.adapter.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;

import lombok.Data;

@Data
@Schema(description = "IBAN验证请求")
public class IbanValidateReq {

    @NotBlank(message = "IBAN不能为空")
    @Schema(description = "IBAN号码", example = "DE89370400440532013000")
    private String iban;
}
