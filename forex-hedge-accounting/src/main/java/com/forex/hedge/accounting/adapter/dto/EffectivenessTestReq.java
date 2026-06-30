package com.forex.hedge.accounting.adapter.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Data
@Schema(description = "EffectivenessTestReq")
public class EffectivenessTestReq {
    @Schema(description = "relationId") private String relationId;
}
