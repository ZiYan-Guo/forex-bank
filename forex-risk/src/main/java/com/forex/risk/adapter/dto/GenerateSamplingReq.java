package com.forex.risk.adapter.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Data
@Schema(description = "GenerateSamplingReq")
public class GenerateSamplingReq {
    @Schema(description = "date") private String date;
}
