package com.forex.margin.adapter.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;

import java.math.BigDecimal;

@Data
@AllArgsConstructor
@Schema(description = "初始保证金标准法计量结果")
public class InitialMarginCalcResp {

    @Schema(description = "初始保证金总额")
    private BigDecimal grossInitialMargin;

    @Schema(description = "净毛比率 NGR")
    private BigDecimal ngr;

    @Schema(description = "标准化初始保证金净额")
    private BigDecimal standardizedInitialMargin;

    @Schema(description = "交易笔数")
    private int tradeCount;
}
