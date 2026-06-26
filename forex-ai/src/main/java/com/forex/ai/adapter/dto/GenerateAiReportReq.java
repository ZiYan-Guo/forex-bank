package com.forex.ai.adapter.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Data
@Schema(description = "GenerateAiReportReq")
public class GenerateAiReportReq {

    @Schema(description = "type")
    private String type;

    @Schema(description = "customerId")
    private String customerId;

}
