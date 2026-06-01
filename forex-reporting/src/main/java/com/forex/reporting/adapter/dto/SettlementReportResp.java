package com.forex.reporting.adapter.dto;

import io.swagger.v3.oas.annotations.media.Schema;

import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
@Schema(description = "结售汇统计申报响应")
public class SettlementReportResp {

    @Schema(description = "主键ID")
    private Long id;

    @Schema(description = "申报编号")
    private String reportNo;

    @Schema(description = "客户ID")
    private Long customerId;

    @Schema(description = "结售汇订单号")
    private String exchangeOrderNo;

    @Schema(description = "结售汇类型")
    private String exchangeType;

    @Schema(description = "交易方向")
    private String dealType;

    @Schema(description = "交易金额")
    private BigDecimal transactionAmount;

    @Schema(description = "交易币种")
    private String transactionCurrency;

    @Schema(description = "人民币金额")
    private BigDecimal cnyAmount;

    @Schema(description = "汇率")
    private BigDecimal exchangeRate;

    @Schema(description = "交易日期")
    private LocalDate transactionDate;

    @Schema(description = "结算日期")
    private LocalDate settleDate;

    @Schema(description = "结算编码")
    private String settlementCode;

    @Schema(description = "申报状态")
    private String reportStatus;

    @Schema(description = "提交时间")
    private LocalDateTime submitTime;

    @Schema(description = "监管回执号")
    private String regulatoryRef;
}
