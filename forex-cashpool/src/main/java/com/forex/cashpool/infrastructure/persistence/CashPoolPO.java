package com.forex.cashpool.infrastructure.persistence;

import com.forex.common.mybatis.base.BasePO;

import com.baomidou.mybatisplus.annotation.TableName;

import lombok.Data;
import lombok.EqualsAndHashCode;

import java.math.BigDecimal;
import java.time.LocalDate;

/**
 * Cash pool persistent object.
 * 资金池持久化对象。
 */
@Data
@EqualsAndHashCode(callSuper = true)
@TableName("t_cash_pool")
public class CashPoolPO extends BasePO {

    private String poolId;
    private Long mainAccountId;
    private String poolName;
    private String poolCurrency;
    private BigDecimal totalLimit;
    private BigDecimal usedLimit;
    private BigDecimal availableLimit;
    private String poolStatus;
    private LocalDate effectiveDate;
}
