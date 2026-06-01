package com.forex.payment.adapter.dto;

import io.swagger.v3.oas.annotations.media.Schema;

import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
public class PaymentResp {

    @Schema(description = "支付ID")
    private Long id;

    @Schema(description = "支付编号")
    private String paymentNo;

    @Schema(description = "客户ID")
    private Long customerId;

    @Schema(description = "支付方向: OUTWARD/INWARD")
    private String paymentDirection;

    @Schema(description = "支付类型: TT/DD/CIPS")
    private String paymentType;

    @Schema(description = "支付金额")
    private BigDecimal payAmount;

    @Schema(description = "支付币种")
    private String payCurrency;

    @Schema(description = "结算金额")
    private BigDecimal settlementAmount;

    @Schema(description = "汇率")
    private BigDecimal exchangeRate;

    @Schema(description = "汇款方信息")
    private String senderInfo;

    @Schema(description = "收款方信息")
    private String beneficiaryInfo;

    @Schema(description = "中间行信息")
    private String intermediaryBankInfo;

    @Schema(description = "付款行代码")
    private String payingBankCode;

    @Schema(description = "收款行代码")
    private String receivingBankCode;

    @Schema(description = "报文类型")
    private String messageType;

    @Schema(description = "SWIFT参考号")
    private String swiftRef;

    @Schema(description = "CIPS参考号")
    private String cipsRef;

    @Schema(description = "GPI追踪ID")
    private String gpiTrackingId;

    @Schema(description = "GPI状态")
    private String gpiStatus;

    @Schema(description = "支付用途")
    private String paymentPurpose;

    @Schema(description = "银行用途代码")
    private String bankPurposeCode;

    @Schema(description = "费用承担方")
    private String chargeBearer;

    @Schema(description = "手续费")
    private BigDecimal feeAmount;

    @Schema(description = "电报费")
    private BigDecimal telegraphicFee;

    @Schema(description = "佣金")
    private BigDecimal commissionAmount;

    @Schema(description = "支付状态")
    private String paymentStatus;

    @Schema(description = "提交时间")
    private LocalDateTime submitTime;

    @Schema(description = "起息日")
    private LocalDate valueDate;

    @Schema(description = "结算日")
    private LocalDate settlementDate;

    @Schema(description = "操作员ID")
    private Long operatorId;

    @Schema(description = "审批员ID")
    private Long approverId;

    @Schema(description = "备注")
    private String remark;

    @Schema(description = "创建时间")
    private LocalDateTime createTime;
}
