package com.forex.rate.adapter.dto;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Data
@Schema(description = "货币转换响应")
public class ConversionResp {

    @Schema(description = "源货币", example = "USD")
    private String fromCurrency;

    @Schema(description = "目标货币", example = "CNY")
    private String toCurrency;

    @Schema(description = "原金额", example = "1000.00")
    private BigDecimal originalAmount;

    @Schema(description = "转换后金额", example = "7240.00")
    private BigDecimal convertedAmount;

    @Schema(description = "汇率", example = "7.2400")
    private BigDecimal exchangeRate;

    @Schema(description = "汇率时间")
    private LocalDateTime rateTime;
}
