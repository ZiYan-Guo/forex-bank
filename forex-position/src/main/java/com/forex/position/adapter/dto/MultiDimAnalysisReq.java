package com.forex.position.adapter.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.util.List;

@Data
@Schema(description = "MultiDimAnalysisReq")
public class MultiDimAnalysisReq {

    @Schema(description = "date")
    private String date;

    @Schema(description = "dimensions")
    private List<String> dimensions;

    @Schema(description = "currencies")
    private List<String> currencies;
}
