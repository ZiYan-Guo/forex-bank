package com.forex.exchange.infrastructure.persistence;

import com.forex.common.mybatis.base.BasePO;

import com.baomidou.mybatisplus.annotation.TableName;

import lombok.Data;
import lombok.EqualsAndHashCode;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
@EqualsAndHashCode(callSuper = true)
@TableName("t_exchange_order")
public class ExchangeOrderPO extends BasePO {

    private String orderNo;
    private Long customerId;
    private String orderType;
    private String dealType;
    private String baseCurrency;
    private String quoteCurrency;
    private BigDecimal orderAmount;
    private BigDecimal settleAmount;
    private BigDecimal bidRate;
    private BigDecimal askRate;
    private BigDecimal confirmedRate;
    private String rateType;
    private LocalDateTime lockRateTime;
    private LocalDateTime lockRateExpireTime;
    private LocalDate valueDate;
    private LocalDate maturityDate;
    private String orderStatus;
    private String customerAccountNo;
    private String bankAccountNo;
    private BigDecimal feeAmount;
    private BigDecimal commissionAmount;
    private String settlementType;
    private String channel;
    private Long operatorId;
    private String remark;
    private String cancelReason;
}
