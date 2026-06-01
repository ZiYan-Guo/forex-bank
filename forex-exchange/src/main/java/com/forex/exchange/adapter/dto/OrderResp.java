package com.forex.exchange.adapter.dto;

import io.swagger.v3.oas.annotations.media.Schema;

import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
@Schema(description = "订单响应")
public class OrderResp {

    @Schema(description = "主键ID")
    private Long id;

    @Schema(description = "订单号")
    private String orderNo;

    @Schema(description = "客户ID")
    private Long customerId;

    @Schema(description = "订单类型")
    private String orderType;

    @Schema(description = "交易方向")
    private String dealType;

    @Schema(description = "基础货币")
    private String baseCurrency;

    @Schema(description = "报价货币")
    private String quoteCurrency;

    @Schema(description = "订单金额")
    private BigDecimal orderAmount;

    @Schema(description = "结算金额")
    private BigDecimal settleAmount;

    @Schema(description = "买入价")
    private BigDecimal bidRate;

    @Schema(description = "卖出价")
    private BigDecimal askRate;

    @Schema(description = "确认汇率")
    private BigDecimal confirmedRate;

    @Schema(description = "汇率类型")
    private String rateType;

    @Schema(description = "锁汇时间")
    private LocalDateTime lockRateTime;

    @Schema(description = "锁汇过期时间")
    private LocalDateTime lockRateExpireTime;

    @Schema(description = "起息日")
    private LocalDate valueDate;

    @Schema(description = "到期日")
    private LocalDate maturityDate;

    @Schema(description = "订单状态")
    private String orderStatus;

    @Schema(description = "客户账户号")
    private String customerAccountNo;

    @Schema(description = "银行账户号")
    private String bankAccountNo;

    @Schema(description = "手续费")
    private BigDecimal feeAmount;

    @Schema(description = "佣金")
    private BigDecimal commissionAmount;

    @Schema(description = "结算方式")
    private String settlementType;

    @Schema(description = "渠道")
    private String channel;

    @Schema(description = "操作员ID")
    private Long operatorId;

    @Schema(description = "备注")
    private String remark;

    @Schema(description = "取消原因")
    private String cancelReason;
}
