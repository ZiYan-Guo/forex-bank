package com.forex.settlement.adapter.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDate;

@Data
public class GuaranteeReq {

    @Schema(description = "客户ID", example = "1")
    private Long customerId;

    @NotBlank(message = "保函类型不能为空")
    @Schema(description = "保函类型", example = "BID")
    private String guaranteeType;

    @NotNull(message = "保函金额不能为空")
    @Schema(description = "保函金额", example = "100000.00")
    private BigDecimal guaranteeAmount;

    @NotBlank(message = "保函币种不能为空")
    @Schema(description = "保函币种", example = "USD")
    private String guaranteeCurrency;

    @Schema(description = "受益人名称")
    private String beneficiaryName;

    @Schema(description = "生效日期")
    private LocalDate effectiveDate;

    @Schema(description = "到期日期")
    private LocalDate expiryDate;

    @Schema(description = "保函格式")
    private String guaranteeFormat;

    @Schema(description = "佣金费率")
    private BigDecimal commissionRate;

    @Schema(description = "备注")
    private String remark;
}
