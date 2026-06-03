package com.forex.clearing.infrastructure.persistence;

import com.forex.common.mybatis.base.BasePO;

import com.baomidou.mybatisplus.annotation.TableName;

import lombok.Data;
import lombok.EqualsAndHashCode;

import java.math.BigDecimal;
import java.time.LocalDate;

@Data
@EqualsAndHashCode(callSuper = true)
@TableName("t_settlement_batch")
public class SettlementBatchPO extends BasePO {

    private String batchNo;
    private LocalDate batchDate;
    private String clearingChannel;
    private Integer totalCount;
    private BigDecimal totalAmount;
    private BigDecimal netAmount;
    private String batchStatus;
}
