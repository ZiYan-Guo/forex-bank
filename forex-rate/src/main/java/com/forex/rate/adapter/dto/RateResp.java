package com.forex.rate.adapter.dto;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Data
@Schema(description = "汇率响应")
public class RateResp {

    @Schema(description = "货币对", example = "USD_CNY")
    private String currencyPair;

    @Schema(description = "买入价", example = "7.2345")
    private BigDecimal bidRate;

    @Schema(description = "卖出价", example = "7.2456")
    private BigDecimal askRate;

    @Schema(description = "中间价", example = "7.2400")
    private BigDecimal midRate;

    @Schema(description = "汇率时间")
    private LocalDateTime rateTime;

    @Schema(description = "汇率来源", example = "Reuters")
    private String rateSource;
}
