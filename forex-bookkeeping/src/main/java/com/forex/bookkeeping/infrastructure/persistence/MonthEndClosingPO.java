package com.forex.bookkeeping.infrastructure.persistence;

import com.forex.common.mybatis.base.BasePO;

import com.baomidou.mybatisplus.annotation.TableName;

import lombok.Data;
import lombok.EqualsAndHashCode;

import java.math.BigDecimal;
import java.time.LocalDate;

/**
 * Month-end closing persistent object.
 * 月末结账持久化对象。
 */
@Data
@EqualsAndHashCode(callSuper = true)
@TableName("t_month_end_closing")
public class MonthEndClosingPO extends BasePO {

    private String closingId;
    private String fiscalPeriod;
    private LocalDate closingDate;
    private String closingStatus;
    private String checklistJson;
    private String auditTrail;
    private BigDecimal totalDebits;
    private BigDecimal totalCredits;
    private Long operatorId;
}
