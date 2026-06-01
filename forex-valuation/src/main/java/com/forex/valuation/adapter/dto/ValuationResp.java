package com.forex.valuation.adapter.dto;

import io.swagger.v3.oas.annotations.media.Schema;

import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDate;

@Data
@Schema(description = "估值响应")
public class ValuationResp {

    @Schema(description = "主键ID")
    private Long id;

    @Schema(description = "交易ID")
    private Long tradeId;

    @Schema(description = "交易号")
    private String tradeNo;

    @Schema(description = "交易类型")
    private String tradeType;

    @Schema(description = "估值日")
    private LocalDate valuationDate;

    @Schema(description = "货币对")
    private String currencyPair;

    @Schema(description = "名义本金")
    private BigDecimal notionalAmount;

    @Schema(description = "公允价值")
    private BigDecimal fairValue;

    @Schema(description = "损益")
    private BigDecimal pnl;

    @Schema(description = "累计损益")
    private BigDecimal cumulativePnl;

    @Schema(description = "估值方法")
    private String valuationMethod;

    @Schema(description = "模型参数")
    private String modelParams;

    @Schema(description = "市场数据快照")
    private String marketDataSnapshot;
}
