package com.forex.payment.adapter.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Data
@Schema(description = "CreateTemplateReq")
public class CreateTemplateReq {
    @Schema(description = "templateName") private String templateName;
    @Schema(description = "scenarioType") private String scenarioType;
}
