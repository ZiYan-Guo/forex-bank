package com.forex.clearing.adapter.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Data
@Schema(description = "ReconcileAutoMatchReq")
public class ReconcileAutoMatchReq {

    @Schema(description = "date")
    private String date;

    @Schema(description = "threshold")
    private String threshold;

}
