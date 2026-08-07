package com.forex.margin.adapter.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;

import java.math.BigDecimal;

@Data
@AllArgsConstructor
@Schema(description = "押品估值结果")
public class CollateralValuationResp {

    private String collateralType;
    private String currency;
    private BigDecimal marketValue;
    private BigDecimal fxRate;
    private BigDecimal haircutPct;

    @Schema(description = "折算后基础价值")
    private BigDecimal convertedValue;

    @Schema(description = "haircut 后押品价值")
    private BigDecimal collateralValue;
}
