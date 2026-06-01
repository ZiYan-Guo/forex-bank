package com.forex.exchange.infrastructure.persistence;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;

import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@TableName("t_exchange_quote")
public class ExchangeQuotePO {

    @TableId(type = IdType.ASSIGN_ID)
    private Long id;
    private Long customerId;
    private String baseCurrency;
    private String quoteCurrency;
    private BigDecimal bidRate;
    private BigDecimal askRate;
    private BigDecimal midRate;
    private LocalDateTime quoteTime;
    private LocalDateTime expireTime;
    private Integer quoteStatus;
}
