package com.forex.risk.adapter.dto;

import io.swagger.v3.oas.annotations.media.Schema;

import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@Schema(description = "风险报告响应")
public class RiskReportResp {

    @Schema(description = "主键ID")
    private Long id;

    @Schema(description = "报告编号")
    private String reportNo;

    @Schema(description = "报告类型")
    private String reportType;

    @Schema(description = "报告期间")
    private String reportPeriod;

    @Schema(description = "客户ID")
    private Long customerId;

    @Schema(description = "总交易笔数")
    private Integer totalTransactions;

    @Schema(description = "总金额")
    private BigDecimal totalAmount;

    @Schema(description = "报告内容")
    private String reportContent;

    @Schema(description = "报告状态")
    private String reportStatus;

    @Schema(description = "提交时间")
    private LocalDateTime submitTime;

    @Schema(description = "提交人ID")
    private Long submitterId;

    @Schema(description = "监管回执号")
    private String regulatoryRef;
}
