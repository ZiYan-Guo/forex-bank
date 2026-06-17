package com.forex.hedge.accounting.infrastructure.persistence;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;

import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDate;

/**
 * Hedge effectiveness test persistent object.
 * 套期有效性测试持久化对象。
 */
@Data
@TableName("t_hedge_effectiveness_test")
public class HedgeEffectivenessTestPO {

    @TableId(type = IdType.ASSIGN_ID)
    private Long id;

    private String relationId;
    private LocalDate testDate;
    private String testType;
    private String testMethod;
    private BigDecimal testResult;
    private String resultStatus;
    private String remarks;
}
