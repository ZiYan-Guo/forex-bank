package com.forex.ai.adapter.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Data
@Schema(description = "AlertConfigReq")
public class AlertConfigReq {

    @Schema(description = "currencyPair")
    private String currencyPair;

    @Schema(description = "threshold")
    private String threshold;

}
