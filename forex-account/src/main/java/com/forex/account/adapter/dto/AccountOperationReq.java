package com.forex.account.adapter.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotNull;

import lombok.Data;

import java.math.BigDecimal;

@Data
public class AccountOperationReq {

    @NotNull(message = "账户ID不能为空")
    @Schema(description = "账户ID", example = "1")
    private Long accountId;

    @NotNull(message = "金额不能为空")
    @DecimalMin(value = "0.01", message = "金额必须大于0")
    @Schema(description = "操作金额", example = "100.00")
    private BigDecimal amount;

    @Schema(description = "关联业务单号", example = "BIZ20240528001")
    private String relatedBizNo;

    @Schema(description = "摘要", example = "柜台存款")
    private String summary;
}
