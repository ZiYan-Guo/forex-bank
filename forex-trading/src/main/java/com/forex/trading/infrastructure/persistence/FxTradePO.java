package com.forex.trading.infrastructure.persistence;

import com.forex.common.mybatis.base.BasePO;

import com.baomidou.mybatisplus.annotation.TableName;

import lombok.Data;
import lombok.EqualsAndHashCode;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
@EqualsAndHashCode(callSuper = true)
@TableName("t_fx_trade")
public class FxTradePO extends BasePO {

    private String tradeNo;
    private Long customerId;
    private String tradeType;
    private String dealType;
    private String buyCurrency;
    private String sellCurrency;
    private BigDecimal buyAmount;
    private BigDecimal sellAmount;
    private BigDecimal tradeRate;
    private LocalDate valueDate;
    private LocalDate maturityDate;
    private LocalDate nearValueDate;
    private LocalDate farValueDate;
    private BigDecimal nearRate;
    private BigDecimal farRate;
    private BigDecimal swapPoints;
    private String optionType;
    private BigDecimal strikePrice;
    private BigDecimal premiumAmount;
    private String premiumCurrency;
    private LocalDate premiumDate;
    private LocalDate expiryDate;
    private String deliveryType;
    private String tradeStatus;
    private String settlementStatus;
    private String nostroAccount;
    private String counterparty;
    private String tradeChannel;
    private Long operatorId;
    private LocalDateTime confirmTime;
    private LocalDateTime executeTime;
    private LocalDateTime settleTime;
    private String remark;
}
