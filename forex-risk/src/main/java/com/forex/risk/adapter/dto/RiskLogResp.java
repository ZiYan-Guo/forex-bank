package com.forex.risk.adapter.dto;

import io.swagger.v3.oas.annotations.media.Schema;

import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@Schema(description = "风险日志响应")
public class RiskLogResp {

    @Schema(description = "主键ID")
    private Long id;

    @Schema(description = "日志编号")
    private String logNo;

    @Schema(description = "客户ID")
    private Long customerId;

    @Schema(description = "业务类型")
    private String bizType;

    @Schema(description = "业务编号")
    private String bizNo;

    @Schema(description = "交易金额")
    private BigDecimal transactionAmount;

    @Schema(description = "交易币种")
    private String transactionCurrency;

    @Schema(description = "交易时间")
    private LocalDateTime transactionTime;

    @Schema(description = "监控规则编码")
    private String monitorRuleCode;

    @Schema(description = "监控规则名称")
    private String monitorRuleName;

    @Schema(description = "风险类别")
    private String riskCategory;

    @Schema(description = "风险级别")
    private String riskLevel;

    @Schema(description = "风险评分")
    private BigDecimal riskScore;

    @Schema(description = "检查结果")
    private String checkResult;

    @Schema(description = "操作员ID")
    private Long operatorId;

    @Schema(description = "处理时间")
    private LocalDateTime handleTime;

    @Schema(description = "处理备注")
    private String handleRemark;
}
