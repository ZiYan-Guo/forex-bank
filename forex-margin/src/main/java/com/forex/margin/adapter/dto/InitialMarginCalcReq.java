package com.forex.margin.adapter.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

@Data
@Schema(description = "初始保证金标准法计量请求")
public class InitialMarginCalcReq {

    @Valid
    @NotEmpty(message = "交易明细不能为空")
    private List<TradeItem> trades = new ArrayList<>();

    @Data
    @Schema(description = "净额结算组合内单笔交易")
    public static class TradeItem {

        @NotNull(message = "资产类别不能为空")
        @Schema(description = "资产类别：CREDIT/COMMODITY/EQUITY/FX/INTEREST_RATE/OTHER")
        private String assetClass;

        @Schema(description = "期限年数，用于信用和利率期限分档")
        private BigDecimal tenorYears;

        @NotNull(message = "名义本金不能为空")
        private BigDecimal notionalAmount;

        @Schema(description = "交易盯市价值，用于 NGR 计算")
        private BigDecimal marketValue = BigDecimal.ZERO;

        @Schema(description = "更审慎的自定义保证金比例，百分数口径，例如 8 表示 8%")
        private BigDecimal conservativeRatePct;
    }
}
