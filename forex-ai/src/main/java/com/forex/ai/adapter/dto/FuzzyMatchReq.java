package com.forex.ai.adapter.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Data
@Schema(description = "FuzzyMatchReq")
public class FuzzyMatchReq {

    @Schema(description = "name")
    private String name;

    @Schema(description = "idType")
    private String idType;

}
