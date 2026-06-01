package com.forex.margin.adapter.dto;

import io.swagger.v3.oas.annotations.media.Schema;

import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@Schema(description = "保证金响应")
public class MarginResp {

    @Schema(description = "主键ID")
    private Long id;

    @Schema(description = "保证金编号")
    private String marginNo;

    @Schema(description = "客户ID")
    private Long customerId;

    @Schema(description = "交易ID")
    private Long tradeId;

    @Schema(description = "保证金类型")
    private String marginType;

    @Schema(description = "保证金币种")
    private String marginCurrency;

    @Schema(description = "应交保证金")
    private BigDecimal requiredAmount;

    @Schema(description = "已存保证金")
    private BigDecimal depositedAmount;

    @Schema(description = "差额")
    private BigDecimal shortfallAmount;

    @Schema(description = "保证金率")
    private BigDecimal marginRate;

    @Schema(description = "催缴时间")
    private LocalDateTime callDate;

    @Schema(description = "到期时间")
    private LocalDateTime dueDate;

    @Schema(description = "状态")
    private String status;

    @Schema(description = "抵押品类型")
    private String collateralType;

    @Schema(description = "释放原因")
    private String releaseReason;
}
