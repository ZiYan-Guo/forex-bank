package com.forex.margin.adapter.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.math.BigDecimal;

@Data
@Schema(description = "押品估值请求")
public class CollateralValuationReq {

    @NotNull(message = "押品类型不能为空")
    @Schema(description = "押品类型：CASH/BOND")
    private String collateralType;

    @Schema(description = "币种")
    private String currency;

    @NotNull(message = "估值金额不能为空")
    @Schema(description = "现金金额或债券估值金额")
    private BigDecimal marketValue;

    @Schema(description = "折人民币汇率，空值默认为1")
    private BigDecimal fxRate = BigDecimal.ONE;

    @Schema(description = "haircut 百分数，例如 2 表示 2%")
    private BigDecimal haircutPct = BigDecimal.ZERO;
}
