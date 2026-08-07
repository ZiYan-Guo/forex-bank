package com.forex.margin.adapter.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.math.BigDecimal;

@Data
@Schema(description = "押品台账汇总")
public class CollateralLedgerSummaryResp {

    private BigDecimal vmReceivedBalance = BigDecimal.ZERO;
    private BigDecimal vmPostedBalance = BigDecimal.ZERO;
    private BigDecimal imPledgeeBalance = BigDecimal.ZERO;
    private BigDecimal imPledgorBalance = BigDecimal.ZERO;
    private BigDecimal cashCollateralBalance = BigDecimal.ZERO;
    private BigDecimal bondCollateralBalance = BigDecimal.ZERO;
    private BigDecimal inTransitAmount = BigDecimal.ZERO;
    private BigDecimal totalCollateralValue = BigDecimal.ZERO;
    private BigDecimal totalShortfallAmount = BigDecimal.ZERO;
}
