package com.forex.risk.adapter.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@Schema(description = "创建风险日志请求")
public class CreateRiskLogReq {

    @NotNull(message = "客户ID不能为空")
    @Schema(description = "客户ID", example = "1001")
    private Long customerId;

    @NotBlank(message = "业务类型不能为空")
    @Schema(description = "业务类型", example = "PAYMENT")
    private String bizType;

    @NotBlank(message = "业务编号不能为空")
    @Schema(description = "业务编号", example = "PAY20241201001")
    private String bizNo;

    @Schema(description = "交易金额")
    private BigDecimal transactionAmount;

    @Schema(description = "交易币种")
    private String transactionCurrency;

    @Schema(description = "交易时间")
    private LocalDateTime transactionTime;

    @NotBlank(message = "监控规则编码不能为空")
    @Schema(description = "监控规则编码", example = "LARGE_AMOUNT")
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

    @Schema(description = "处理备注")
    private String handleRemark;
}
