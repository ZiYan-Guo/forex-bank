package com.forex.customer.infrastructure.persistence;

import com.forex.common.mybatis.base.BasePO;

import com.baomidou.mybatisplus.annotation.TableName;

import lombok.Data;
import lombok.EqualsAndHashCode;

import java.math.BigDecimal;
import java.time.LocalDate;

@Data
@EqualsAndHashCode(callSuper = true)
@TableName("t_customer_credit_limit")
public class CreditLimitPO extends BasePO {

    private Long customerId;
    private String limitType;
    private String currency;
    private BigDecimal totalLimit;
    private BigDecimal usedLimit;
    private BigDecimal availableLimit;
    private LocalDate effectiveDate;
    private LocalDate expireDate;
    private Integer status;
}
