package com.forex.trading.adapter.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDate;

@Data
public class CreateTradeReq {

    @NotNull(message = "客户ID不能为空")
    @Schema(description = "客户ID", example = "1")
    private Long customerId;

    @NotBlank(message = "交易类型不能为空")
    @Schema(description = "交易类型 (SPOT/FORWARD/SWAP/OPTION)", example = "SPOT")
    private String tradeType;

    @NotBlank(message = "交易方向不能为空")
    @Schema(description = "交易方向 (BUY/SELL)", example = "BUY")
    private String dealType;

    @Schema(description = "买入币种", example = "USD")
    private String buyCurrency;

    @Schema(description = "卖出币种", example = "CNY")
    private String sellCurrency;

    @NotNull(message = "买入金额不能为空")
    @DecimalMin(value = "0.01", message = "买入金额必须大于0.01")
    @Schema(description = "买入金额", example = "10000.00")
    private BigDecimal buyAmount;

    @Schema(description = "卖出金额", example = "71000.00")
    private BigDecimal sellAmount;

    @Schema(description = "交易汇率", example = "7.1000")
    private BigDecimal tradeRate;

    @Schema(description = "起息日", example = "2025-06-01")
    private LocalDate valueDate;

    @Schema(description = "到期日", example = "2025-07-01")
    private LocalDate maturityDate;

    @Schema(description = "近期起息日(掉期)", example = "2025-06-01")
    private LocalDate nearValueDate;

    @Schema(description = "远期起息日(掉期)", example = "2025-07-01")
    private LocalDate farValueDate;

    @Schema(description = "近期汇率(掉期)", example = "7.1000")
    private BigDecimal nearRate;

    @Schema(description = "远期汇率(掉期)", example = "7.1500")
    private BigDecimal farRate;

    @Schema(description = "掉期点", example = "0.0500")
    private BigDecimal swapPoints;

    @Schema(description = "期权类型 (CALL/PUT)", example = "CALL")
    private String optionType;

    @Schema(description = "行权价格", example = "7.2000")
    private BigDecimal strikePrice;

    @Schema(description = "权利金金额", example = "500.00")
    private BigDecimal premiumAmount;

    @Schema(description = "权利金币种", example = "USD")
    private String premiumCurrency;

    @Schema(description = "权利金支付日", example = "2025-06-01")
    private LocalDate premiumDate;

    @Schema(description = "期权到期日", example = "2025-07-01")
    private LocalDate expiryDate;

    @Schema(description = "交割方式", example = "PHYSICAL")
    private String deliveryType;

    @Schema(description = "交易对手", example = "BANK_A")
    private String counterparty;

    @Schema(description = "往账账户", example = "NOSTRO001")
    private String nostroAccount;

    @Schema(description = "交易渠道", example = "ONLINE")
    private String tradeChannel;

    @Schema(description = "备注")
    private String remark;
}
