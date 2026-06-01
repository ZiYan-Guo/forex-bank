package com.forex.customer.infrastructure.persistence;

import com.forex.common.mybatis.base.BasePO;

import com.baomidou.mybatisplus.annotation.TableName;

import lombok.Data;
import lombok.EqualsAndHashCode;

import java.math.BigDecimal;

@Data
@EqualsAndHashCode(callSuper = true)
@TableName("t_customer_quota")
public class CustomerQuotaPO extends BasePO {

    private Long customerId;
    private String quotaYear;
    private String quotaType;
    private BigDecimal quotaAmount;
    private BigDecimal usedAmount;
    private String currency;
}
