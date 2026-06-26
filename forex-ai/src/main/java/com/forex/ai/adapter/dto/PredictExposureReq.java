package com.forex.ai.adapter.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Data
@Schema(description = "PredictExposureReq")
public class PredictExposureReq {

    @Schema(description = "customerId")
    private String customerId;

}
