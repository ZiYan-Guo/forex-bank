package com.forex.account.adapter.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import lombok.Data;

@Data
public class OpenAccountReq {

    @NotNull(message = "客户ID不能为空")
    @Schema(description = "客户ID", example = "1")
    private Long customerId;

    @NotBlank(message = "账户类型不能为空")
    @Schema(description = "账户类型", example = "SAVINGS")
    private String accountType;

    @NotBlank(message = "币种不能为空")
    @Schema(description = "币种", example = "USD")
    private String currency;

    @NotBlank(message = "账户名称不能为空")
    @Schema(description = "账户名称", example = "美元储蓄账户")
    private String accountName;

    @Schema(description = "开户网点", example = "总行营业部")
    private String openBranch;
}
