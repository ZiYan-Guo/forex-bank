package com.forex.reporting.adapter.dto;

import io.swagger.v3.oas.annotations.media.Schema;

import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
@Schema(description = "国际收支申报响应")
public class BopReportResp {

    @Schema(description = "主键ID")
    private Long id;

    @Schema(description = "申报编号")
    private String reportNo;

    @Schema(description = "申报类型")
    private String reportType;

    @Schema(description = "客户ID")
    private Long customerId;

    @Schema(description = "客户名称")
    private String customerName;

    @Schema(description = "交易编号")
    private String transactionNo;

    @Schema(description = "交易类型")
    private String transactionType;

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
    private LocalDate settlementDate;

    @Schema(description = "BOP编码")
    private String bopCode;

    @Schema(description = "BOP名称")
    private String bopName;

    @Schema(description = "交易附言编码")
    private String purposeCode;

    @Schema(description = "交易附言")
    private String purposeRemark;

    @Schema(description = "对手方国家")
    private String counterpartyCountry;

    @Schema(description = "对手方名称")
    private String counterpartyName;

    @Schema(description = "申报状态")
    private String reportStatus;

    @Schema(description = "提交时间")
    private LocalDateTime submitTime;

    @Schema(description = "监管回执号")
    private String regulatoryRef;

    @Schema(description = "错误信息")
    private String errorMsg;
}
