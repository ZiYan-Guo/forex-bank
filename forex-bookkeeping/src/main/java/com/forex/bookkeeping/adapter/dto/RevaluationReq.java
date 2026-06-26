package com.forex.bookkeeping.adapter.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.math.BigDecimal;
import java.util.List;

@Data
@Schema(description = "重估请求")
public class RevaluationReq {

    @Schema(description = "币种余额列表")
    private List<CurrencyBalance> currencies;

    @Data
    @Schema(description = "币种余额")
    public static class CurrencyBalance {

        @Schema(description = "币种代码")
        private String currency;

        @Schema(description = "旧汇率")
        private BigDecimal oldRate;

        @Schema(description = "新汇率")
        private BigDecimal newRate;

        @Schema(description = "余额")
        private BigDecimal balance;
    }
}
