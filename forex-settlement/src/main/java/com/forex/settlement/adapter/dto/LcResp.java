package com.forex.settlement.adapter.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
public class LcResp {

    @Schema(description = "ID")
    private Long id;

    @Schema(description = "信用证编号")
    private String lcNo;

    @Schema(description = "客户ID")
    private Long customerId;

    @Schema(description = "信用证类型")
    private String lcType;

    @Schema(description = "信用证方向")
    private String lcDirection;

    @Schema(description = "信用证金额")
    private BigDecimal lcAmount;

    @Schema(description = "信用证币种")
    private String lcCurrency;

    @Schema(description = "溢短装比例")
    private BigDecimal tolerancePct;

    @Schema(description = "申请人信息")
    private String applicantInfo;

    @Schema(description = "受益人信息")
    private String beneficiaryInfo;

    @Schema(description = "开证行信息")
    private String issuingBankInfo;

    @Schema(description = "通知行信息")
    private String advisingBankInfo;

    @Schema(description = "保兑行信息")
    private String confirmingBankInfo;

    @Schema(description = "开证日期")
    private LocalDate issueDate;

    @Schema(description = "到期日")
    private LocalDate expiryDate;

    @Schema(description = "到期地点")
    private String expiryPlace;

    @Schema(description = "最迟装运日")
    private LocalDate latestShipDate;

    @Schema(description = "交单期")
    private Integer presentationPeriod;

    @Schema(description = "可用银行")
    private String availableWith;

    @Schema(description = "可用方式")
    private String availableBy;

    @Schema(description = "汇票期限")
    private String draftTenor;

    @Schema(description = "分批装运")
    private String partialShipment;

    @Schema(description = "转运")
    private String transshipment;

    @Schema(description = "装运港")
    private String portOfLoading;

    @Schema(description = "卸货港")
    private String portOfDischarge;

    @Schema(description = "货物描述")
    private String goodsDescription;

    @Schema(description = "所需单据")
    private String documentsRequired;

    @Schema(description = "附加条件")
    private String additionalConditions;

    @Schema(description = "保兑指示")
    private String confirmationInstruction;

    @Schema(description = "费用承担方")
    private String chargeBearer;

    @Schema(description = "信用证状态")
    private String lcStatus;

    @Schema(description = "SWIFT参考号")
    private String swiftRef;

    @Schema(description = "保证金比例")
    private BigDecimal marginPct;

    @Schema(description = "保证金金额")
    private BigDecimal marginAmount;

    @Schema(description = "手续费")
    private BigDecimal feeAmount;

    @Schema(description = "操作员ID")
    private Long operatorId;

    @Schema(description = "备注")
    private String remark;

    @Schema(description = "创建时间")
    private LocalDateTime createTime;
}
