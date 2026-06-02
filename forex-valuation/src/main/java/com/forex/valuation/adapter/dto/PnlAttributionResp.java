package com.forex.valuation.adapter.dto;

import io.swagger.v3.oas.annotations.media.Schema;

import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDate;

@Data
@Schema(description = "损益归因响应")
public class PnlAttributionResp {

    @Schema(description = "主键ID")
    private Long id;

    @Schema(description = "归因编号")
    private String attribNo;

    @Schema(description = "交易ID")
    private Long tradeId;

    @Schema(description = "交易编号")
    private String tradeNo;

    @Schema(description = "归因日期")
    private LocalDate attribDate;

    @Schema(description = "总损益")
    private BigDecimal totalPnl;

    @Schema(description = "Delta损益")
    private BigDecimal deltaPnl;

    @Schema(description = "Theta损益")
    private BigDecimal thetaPnl;

    @Schema(description = "Gamma损益")
    private BigDecimal gammaPnl;

    @Schema(description = "Vega损益")
    private BigDecimal vegaPnl;

    @Schema(description = "息差损益")
    private BigDecimal carryPnl;

    @Schema(description = "交易操作损益")
    private BigDecimal tradePnl;

    @Schema(description = "归因维度类型")
    private String tariffType;

    @Schema(description = "归因维度值")
    private String tariffValue;
}
