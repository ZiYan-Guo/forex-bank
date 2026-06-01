package com.forex.trading.adapter.dto;

import io.swagger.v3.oas.annotations.media.Schema;

import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
public class TradeResp {

    @Schema(description = "交易ID")
    private Long id;

    @Schema(description = "交易编号")
    private String tradeNo;

    @Schema(description = "客户ID")
    private Long customerId;

    @Schema(description = "交易类型")
    private String tradeType;

    @Schema(description = "交易方向")
    private String dealType;

    @Schema(description = "买入币种")
    private String buyCurrency;

    @Schema(description = "卖出币种")
    private String sellCurrency;

    @Schema(description = "买入金额")
    private BigDecimal buyAmount;

    @Schema(description = "卖出金额")
    private BigDecimal sellAmount;

    @Schema(description = "交易汇率")
    private BigDecimal tradeRate;

    @Schema(description = "起息日")
    private LocalDate valueDate;

    @Schema(description = "到期日")
    private LocalDate maturityDate;

    @Schema(description = "近期起息日")
    private LocalDate nearValueDate;

    @Schema(description = "远期起息日")
    private LocalDate farValueDate;

    @Schema(description = "近期汇率")
    private BigDecimal nearRate;

    @Schema(description = "远期汇率")
    private BigDecimal farRate;

    @Schema(description = "掉期点")
    private BigDecimal swapPoints;

    @Schema(description = "期权类型")
    private String optionType;

    @Schema(description = "行权价格")
    private BigDecimal strikePrice;

    @Schema(description = "权利金金额")
    private BigDecimal premiumAmount;

    @Schema(description = "权利金币种")
    private String premiumCurrency;

    @Schema(description = "权利金支付日")
    private LocalDate premiumDate;

    @Schema(description = "期权到期日")
    private LocalDate expiryDate;

    @Schema(description = "交割方式")
    private String deliveryType;

    @Schema(description = "交易状态")
    private String tradeStatus;

    @Schema(description = "结算状态")
    private String settlementStatus;

    @Schema(description = "往账账户")
    private String nostroAccount;

    @Schema(description = "交易对手")
    private String counterparty;

    @Schema(description = "交易渠道")
    private String tradeChannel;

    @Schema(description = "操作员ID")
    private Long operatorId;

    @Schema(description = "确认时间")
    private LocalDateTime confirmTime;

    @Schema(description = "执行时间")
    private LocalDateTime executeTime;

    @Schema(description = "结算时间")
    private LocalDateTime settleTime;

    @Schema(description = "备注")
    private String remark;
}
