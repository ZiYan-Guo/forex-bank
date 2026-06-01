package com.forex.settlement.adapter.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDate;

@Data
public class CreateLcReq {

    @NotNull(message = "客户ID不能为空")
    @Schema(description = "客户ID", example = "1")
    private Long customerId;

    @NotBlank(message = "信用证类型不能为空")
    @Schema(description = "信用证类型", example = "IRREVOCABLE")
    private String lcType;

    @Schema(description = "信用证方向", example = "IMPORT")
    private String lcDirection;

    @NotNull(message = "信用证金额不能为空")
    @DecimalMin(value = "0.01", message = "信用证金额必须大于0.01")
    @Schema(description = "信用证金额", example = "50000.00")
    private BigDecimal lcAmount;

    @NotBlank(message = "信用证币种不能为空")
    @Schema(description = "信用证币种", example = "USD")
    private String lcCurrency;

    @Schema(description = "申请人名称")
    private String applicantName;

    @Schema(description = "申请人地址")
    private String applicantAddress;

    @Schema(description = "受益人名称")
    private String beneficiaryName;

    @Schema(description = "受益人账号")
    private String beneficiaryAccount;

    @Schema(description = "受益人银行")
    private String beneficiaryBank;

    @Schema(description = "开证行")
    private String issuingBank;

    @Schema(description = "通知行")
    private String advisingBank;

    @Schema(description = "开证日期")
    private LocalDate issueDate;

    @Schema(description = "到期日")
    private LocalDate expiryDate;

    @Schema(description = "可用方式", example = "BY_PAYMENT")
    private String availableBy;

    @Schema(description = "货物描述")
    private String goodsDescription;

    @Schema(description = "所需单据")
    private String documentsRequired;

    @Schema(description = "保证金比例")
    private BigDecimal marginPct;

    @Schema(description = "手续费")
    private BigDecimal feeAmount;

    @Schema(description = "备注")
    private String remark;
}
