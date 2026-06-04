package com.forex.ai.adapter.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@Schema(description = "汇率预测响应")
public class RatePredictionResp {

    @Schema(description = "货币对")
    private String currencyPair;

    @Schema(description = "预测类型")
    private String predType;

    @Schema(description = "当前汇率")
    private BigDecimal currentRate;

    @Schema(description = "预测汇率")
    private BigDecimal predictedRate;

    @Schema(description = "下限")
    private BigDecimal lowerBound;

    @Schema(description = "上限")
    private BigDecimal upperBound;

    @Schema(description = "置信度")
    private BigDecimal confidence;

    @Schema(description = "趋势")
    private String trend;

    @Schema(description = "预测时间")
    private LocalDateTime predictionTime;
}
