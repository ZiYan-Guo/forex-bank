package com.forex.payment.adapter.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;

import lombok.Data;

@Data
@Schema(description = "SWIFT/BIC代码验证请求")
public class SwiftValidateReq {

    @NotBlank(message = "SWIFT代码不能为空")
    @Schema(description = "SWIFT/BIC代码", example = "ICBKCNBJBJM")
    private String swiftCode;
}
