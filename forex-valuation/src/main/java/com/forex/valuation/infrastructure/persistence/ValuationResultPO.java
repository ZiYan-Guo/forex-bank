package com.forex.valuation.infrastructure.persistence;

import com.forex.common.mybatis.base.BasePO;

import com.baomidou.mybatisplus.annotation.TableName;

import lombok.Data;
import lombok.EqualsAndHashCode;

import java.math.BigDecimal;
import java.time.LocalDate;

@Data
@EqualsAndHashCode(callSuper = true)
@TableName("t_valuation_result")
public class ValuationResultPO extends BasePO {

    private Long tradeId;
    private String tradeNo;
    private String tradeType;
    private LocalDate valuationDate;
    private String currencyPair;
    private BigDecimal notionalAmount;
    private BigDecimal fairValue;
    private BigDecimal pnl;
    private BigDecimal cumulativePnl;
    private String valuationMethod;
    private String modelParams;
    private String marketDataSnapshot;
}
