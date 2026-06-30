package com.forex.hedge.accounting.adapter.dto;

import java.math.BigDecimal;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Data
@Schema(description = "GenerateHedgeEntriesReq")
public class GenerateHedgeEntriesReq {
    @Schema(description = "relationId") private String relationId;
    @Schema(description = "fairValueChange") private BigDecimal fairValueChange;
}
