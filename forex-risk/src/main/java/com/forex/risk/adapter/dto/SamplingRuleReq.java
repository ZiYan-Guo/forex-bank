package com.forex.risk.adapter.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.math.BigDecimal;

@Data
@Schema(description = "Sampling rule save request / 抽查规则保存请求")
public class SamplingRuleReq {

    @Schema(description = "Rule code / 规则编码")
    private String ruleCode;

    @Schema(description = "Rule name / 规则名称")
    private String ruleName;

    @Schema(description = "Condition JSON / 规则条件JSON")
    private String conditionJson;

    @Schema(description = "Sampling rate percentage / 抽查比例")
    private BigDecimal samplingRate;

    @Schema(description = "Target business module / 目标业务模块")
    private String targetModule;

    @Schema(description = "Effective date, yyyy-MM-dd / 生效日期")
    private String effectiveDate;

    @Schema(description = "Expiry date, yyyy-MM-dd / 失效日期")
    private String expireDate;

    @Schema(description = "Priority / 优先级")
    private Integer priority;

    @Schema(description = "Status: ACTIVE/INACTIVE / 状态")
    private String status;

    @Schema(description = "Whether to auto-extract samples / 是否自动提取样本")
    private Boolean isAutoExtract;
}
