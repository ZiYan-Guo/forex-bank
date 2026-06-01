package com.forex.position.infrastructure.persistence;

import com.forex.common.mybatis.base.BasePO;

import com.baomidou.mybatisplus.annotation.TableName;

import lombok.Data;
import lombok.EqualsAndHashCode;

import java.math.BigDecimal;
import java.time.LocalDate;

@Data
@EqualsAndHashCode(callSuper = true)
@TableName("t_position")
public class PositionPO extends BasePO {

    private String positionNo;
    private String currencyPair;
    private String positionType;
    private String positionCurrency;
    private BigDecimal longAmount;
    private BigDecimal shortAmount;
    private BigDecimal netPosition;
    private BigDecimal positionLimit;
    private BigDecimal limitUsagePct;
    private LocalDate positionDate;
    private Long traderId;
    private String branchCode;
    private String riskLevel;
    private String hedgingAction;
}
