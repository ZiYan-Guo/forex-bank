package com.forex.exchange.adapter.dto;

import io.swagger.v3.oas.annotations.media.Schema;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDate;

@Data
@Schema(description = "创建订单请求")
public class CreateOrderReq {

    @Schema(description = "客户ID", example = "1")
    @NotNull(message = "客户ID不能为空")
    private Long customerId;

    @Schema(description = "订单类型: SPOT/FORWARD/PENDING_ORDER", example = "SPOT")
    @NotBlank(message = "订单类型不能为空")
    private String orderType;

    @Schema(description = "交易方向: BUY/SELL", example = "BUY")
    @NotBlank(message = "交易方向不能为空")
    private String dealType;

    @Schema(description = "基础货币", example = "USD")
    private String baseCurrency;

    @Schema(description = "报价货币", example = "CNY")
    private String quoteCurrency;

    @Schema(description = "订单金额", example = "10000.00")
    @NotNull(message = "订单金额不能为空")
    @DecimalMin(value = "0.01", message = "订单金额必须大于0.01")
    private BigDecimal orderAmount;

    @Schema(description = "汇率类型")
    private String rateType;

    @Schema(description = "到期日(远期订单必填)")
    private LocalDate maturityDate;

    @Schema(description = "结算方式")
    private String settlementType;

    @Schema(description = "渠道")
    private String channel;

    @Schema(description = "客户账户号")
    private String customerAccountNo;

    @Schema(description = "备注")
    private String remark;
}
