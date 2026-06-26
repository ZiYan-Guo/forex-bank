package com.forex.clearing.adapter.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Data
@Schema(description = "UpdateTrackerStatusReq")
public class UpdateTrackerStatusReq {

    @Schema(description = "status")
    private String status;

}
