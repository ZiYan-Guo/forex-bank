package com.forex.ai.adapter.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.math.BigDecimal;

@Data
@Schema(description = "风险预警响应")
public class RiskAlertResp {

    @Schema(description = "预警ID")
    private String alertId;

    @Schema(description = "风险类型")
    private String riskType;

    @Schema(description = "风险评分")
    private BigDecimal score;

    @Schema(description = "风险级别")
    private String level;

    @Schema(description = "风险描述")
    private String description;
}
