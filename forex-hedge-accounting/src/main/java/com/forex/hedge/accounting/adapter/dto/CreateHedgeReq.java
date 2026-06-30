package com.forex.hedge.accounting.adapter.dto;

import java.math.BigDecimal;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Data
@Schema(description = "CreateHedgeReq")
public class CreateHedgeReq {
    @Schema(description = "customerId") private Long customerId;
    @Schema(description = "hedgeType") private String hedgeType;
    @Schema(description = "hedgedItem") private String hedgedItem;
    @Schema(description = "hedgingInstrument") private String hedgingInstrument;
    @Schema(description = "hedgedAmount") private BigDecimal hedgedAmount;
    @Schema(description = "hedgedCurrency") private String hedgedCurrency;
    @Schema(description = "instrumentNotional") private BigDecimal instrumentNotional;
    @Schema(description = "ifrsStandard") private String ifrsStandard;
}
