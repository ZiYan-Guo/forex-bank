package com.forex.valuation.infrastructure.persistence;

import com.forex.common.mybatis.base.BasePO;

import com.baomidou.mybatisplus.annotation.TableName;

import lombok.Data;
import lombok.EqualsAndHashCode;

import java.math.BigDecimal;
import java.time.LocalDate;

@Data
@EqualsAndHashCode(callSuper = true)
@TableName("t_pnl_attribution")
public class PnlAttributionPO extends BasePO {

    private String attribNo;
    private Long tradeId;
    private String tradeNo;
    private LocalDate attribDate;
    private BigDecimal totalPnl;
    private BigDecimal deltaPnl;
    private BigDecimal thetaPnl;
    private BigDecimal gammaPnl;
    private BigDecimal vegaPnl;
    private BigDecimal carryPnl;
    private BigDecimal tradePnl;
    private String tariffType;
    private String tariffValue;
}
