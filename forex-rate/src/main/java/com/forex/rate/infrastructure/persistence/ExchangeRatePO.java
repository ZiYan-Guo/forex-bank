package com.forex.rate.infrastructure.persistence;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

import com.baomidou.mybatisplus.annotation.TableName;
import com.forex.common.mybatis.base.BasePO;

import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = true)
@TableName("t_exchange_rate")
public class ExchangeRatePO extends BasePO {

    private String currencyPair;
    private String baseCurrency;
    private String quoteCurrency;
    private BigDecimal bidRate;
    private BigDecimal askRate;
    private BigDecimal midRate;
    private BigDecimal spread;
    private String rateSource;
    private LocalDate rateDate;
    private LocalDateTime rateTime;
    private Integer status;
}
