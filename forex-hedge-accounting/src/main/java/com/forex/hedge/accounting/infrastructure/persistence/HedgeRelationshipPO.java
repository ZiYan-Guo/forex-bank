package com.forex.hedge.accounting.infrastructure.persistence;

import com.forex.common.mybatis.base.BasePO;

import com.baomidou.mybatisplus.annotation.TableName;

import lombok.Data;
import lombok.EqualsAndHashCode;

import java.math.BigDecimal;
import java.time.LocalDate;

/**
 * Hedge relationship persistent object.
 * 套期关系持久化对象。
 */
@Data
@EqualsAndHashCode(callSuper = true)
@TableName("t_hedge_relationship")
public class HedgeRelationshipPO extends BasePO {

    private String relationId;
    private Long customerId;
    private String hedgeType;
    private String hedgedItem;
    private String hedgingInstrument;
    private BigDecimal hedgedAmount;
    private String hedgedCurrency;
    private BigDecimal instrumentNotional;
    private LocalDate designationDate;
    private LocalDate deDesignationDate;
    private String relationshipStatus;
    private BigDecimal effectivenessRatio;
    private String ifrsStandard;
}
