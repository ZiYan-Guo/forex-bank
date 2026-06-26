package com.forex.ai.adapter.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Data
@Schema(description = "HedgingRecommendReq")
public class HedgingRecommendReq {

    @Schema(description = "customerId")
    private String customerId;

    @Schema(description = "tradeId")
    private String tradeId;

}
