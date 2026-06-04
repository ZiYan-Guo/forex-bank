package com.forex.ai.adapter.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.math.BigDecimal;
import java.util.List;

@Data
@Schema(description = "交易推荐响应")
public class TradingRecommendResp {

    @Schema(description = "推荐策略列表")
    private List<String> strategies;

    @Schema(description = "推荐比例")
    private BigDecimal recommendedRatio;

    @Schema(description = "推荐理由")
    private String reasoning;
}
