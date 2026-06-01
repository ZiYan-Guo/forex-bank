package com.forex.clearing.adapter.dto;

import io.swagger.v3.oas.annotations.media.Schema;

import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
public class ClearingResp {

    @Schema(description = "指令ID")
    private Long id;

    @Schema(description = "清算指令号")
    private String instructionNo;

    @Schema(description = "业务类型")
    private String bizType;

    @Schema(description = "业务编号")
    private String bizNo;

    @Schema(description = "清算渠道")
    private String clearingChannel;

    @Schema(description = "我行账户")
    private String nostroAccount;

    @Schema(description = "对手账户")
    private String counterPartyAccount;

    @Schema(description = "支付币种")
    private String payCurrency;

    @Schema(description = "支付金额")
    private BigDecimal payAmount;

    @Schema(description = "收款币种")
    private String receiveCurrency;

    @Schema(description = "收款金额")
    private BigDecimal receiveAmount;

    @Schema(description = "起息日")
    private LocalDate valueDate;

    @Schema(description = "结算日")
    private LocalDate settlementDate;

    @Schema(description = "结算类型")
    private String settlementType;

    @Schema(description = "指令状态")
    private String instructionStatus;

    @Schema(description = "SWIFT参考号")
    private String swiftRef;

    @Schema(description = "CIPS参考号")
    private String cipsRef;

    @Schema(description = "清算前账户余额")
    private BigDecimal nostroBalanceBefore;

    @Schema(description = "清算后账户余额")
    private BigDecimal nostroBalanceAfter;

    @Schema(description = "发送时间")
    private LocalDateTime sendTime;

    @Schema(description = "确认时间")
    private LocalDateTime ackTime;

    @Schema(description = "结算时间")
    private LocalDateTime settleTime;

    @Schema(description = "操作员ID")
    private Long operatorId;

    @Schema(description = "备注")
    private String remark;

    @Schema(description = "创建时间")
    private LocalDateTime createTime;
}
