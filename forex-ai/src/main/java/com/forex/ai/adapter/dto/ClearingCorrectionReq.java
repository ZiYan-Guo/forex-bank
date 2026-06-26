package com.forex.ai.adapter.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Data
@Schema(description = "ClearingCorrectionReq")
public class ClearingCorrectionReq {

    @Schema(description = "clearingId")
    private String clearingId;

}
