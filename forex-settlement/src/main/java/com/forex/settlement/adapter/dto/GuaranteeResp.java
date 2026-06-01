package com.forex.settlement.adapter.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
public class GuaranteeResp {

    @Schema(description = "ID")
    private Long id;

    @Schema(description = "保函编号")
    private String guaranteeNo;

    @Schema(description = "客户ID")
    private Long customerId;

    @Schema(description = "保函类型")
    private String guaranteeType;

    @Schema(description = "保函金额")
    private BigDecimal guaranteeAmount;

    @Schema(description = "保函币种")
    private String guaranteeCurrency;

    @Schema(description = "受益人信息")
    private String beneficiaryInfo;

    @Schema(description = "开立日期")
    private LocalDate issueDate;

    @Schema(description = "生效日期")
    private LocalDate effectiveDate;

    @Schema(description = "到期日期")
    private LocalDate expiryDate;

    @Schema(description = "索赔到期日")
    private LocalDate claimExpiryDate;

    @Schema(description = "反担保编号")
    private String counterGuaranteeNo;

    @Schema(description = "保函格式")
    private String guaranteeFormat;

    @Schema(description = "保函状态")
    private String guaranteeStatus;

    @Schema(description = "手续费")
    private BigDecimal feeAmount;

    @Schema(description = "佣金费率")
    private BigDecimal commissionRate;

    @Schema(description = "操作员ID")
    private Long operatorId;

    @Schema(description = "SWIFT参考号")
    private String swiftRef;

    @Schema(description = "备注")
    private String remark;

    @Schema(description = "创建时间")
    private LocalDateTime createTime;
}
