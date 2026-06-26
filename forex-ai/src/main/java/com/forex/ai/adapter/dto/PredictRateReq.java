package com.forex.ai.adapter.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Data
@Schema(description = "PredictRateReq")
public class PredictRateReq {

    @Schema(description = "currencyPair")
    private String currencyPair;

}
