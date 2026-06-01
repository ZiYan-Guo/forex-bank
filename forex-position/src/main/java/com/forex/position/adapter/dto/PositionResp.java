package com.forex.position.adapter.dto;

import io.swagger.v3.oas.annotations.media.Schema;

import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDate;

@Data
@Schema(description = "头寸响应")
public class PositionResp {

    @Schema(description = "主键ID")
    private Long id;

    @Schema(description = "头寸编号")
    private String positionNo;

    @Schema(description = "货币对")
    private String currencyPair;

    @Schema(description = "头寸类型")
    private String positionType;

    @Schema(description = "头寸币种")
    private String positionCurrency;

    @Schema(description = "多头金额")
    private BigDecimal longAmount;

    @Schema(description = "空头金额")
    private BigDecimal shortAmount;

    @Schema(description = "净头寸")
    private BigDecimal netPosition;

    @Schema(description = "头寸限额")
    private BigDecimal positionLimit;

    @Schema(description = "限额使用比例")
    private BigDecimal limitUsagePct;

    @Schema(description = "头寸日")
    private LocalDate positionDate;

    @Schema(description = "交易员ID")
    private Long traderId;

    @Schema(description = "分行代码")
    private String branchCode;

    @Schema(description = "风险等级")
    private String riskLevel;

    @Schema(description = "对冲建议")
    private String hedgingAction;
}
