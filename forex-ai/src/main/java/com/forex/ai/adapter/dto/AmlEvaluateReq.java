package com.forex.ai.adapter.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Data
@Schema(description = "AmlEvaluateReq")
public class AmlEvaluateReq {

    @Schema(description = "customerId")
    private String customerId;

    @Schema(description = "transactionData")
    private String transactionData;

}
