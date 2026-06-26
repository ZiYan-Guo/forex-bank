package com.forex.clearing.adapter.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Data
@Schema(description = "ReconcileConvertReq")
public class ReconcileConvertReq {

    @Schema(description = "raw")
    private String raw;

}
