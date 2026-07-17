package com.forex.rate.application.query;

import java.time.LocalDate;

import com.forex.common.base.dto.PageReq;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = true)
@Schema(description = "汇率历史分页查询 / Exchange rate history page query")
public class RateQuery extends PageReq {

    @Schema(description = "货币对 / Currency pair", example = "USD_CNY")
    private String currencyPair;

    @Schema(description = "基础货币 / Base currency", example = "USD")
    private String baseCurrency;

    @Schema(description = "报价货币 / Quote currency", example = "CNY")
    private String quoteCurrency;

    @Schema(description = "汇率来源 / Rate source", example = "CFETS")
    private String rateSource;

    @Schema(description = "状态: 1=有效, 0=失效 / Status: 1=active, 0=inactive")
    private Integer status;

    @Schema(description = "指定汇率日期 / Exact rate date")
    private LocalDate rateDate;

    @Schema(description = "开始日期 / Start date")
    private LocalDate startDate;

    @Schema(description = "结束日期 / End date")
    private LocalDate endDate;
}
