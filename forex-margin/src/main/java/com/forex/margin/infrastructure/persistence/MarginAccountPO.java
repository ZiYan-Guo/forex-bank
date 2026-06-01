package com.forex.margin.infrastructure.persistence;

import com.forex.common.mybatis.base.BasePO;

import com.baomidou.mybatisplus.annotation.TableName;

import lombok.Data;
import lombok.EqualsAndHashCode;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@EqualsAndHashCode(callSuper = true)
@TableName("t_margin_account")
public class MarginAccountPO extends BasePO {

    private String marginNo;
    private Long customerId;
    private Long tradeId;
    private String marginType;
    private String marginCurrency;
    private BigDecimal requiredAmount;
    private BigDecimal depositedAmount;
    private BigDecimal shortfallAmount;
    private BigDecimal marginRate;
    private LocalDateTime callDate;
    private LocalDateTime dueDate;
    private String status;
    private String collateralType;
    private String releaseReason;
}
