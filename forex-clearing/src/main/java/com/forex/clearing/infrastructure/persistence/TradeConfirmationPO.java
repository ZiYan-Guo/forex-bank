package com.forex.clearing.infrastructure.persistence;

import com.forex.common.mybatis.base.BasePO;

import com.baomidou.mybatisplus.annotation.TableName;

import lombok.Data;
import lombok.EqualsAndHashCode;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
@EqualsAndHashCode(callSuper = true)
@TableName("t_confirmation_match")
public class TradeConfirmationPO extends BasePO {

    private String confirmId;
    private String tradeNo;
    private String tradeType;
    private String confirmFlag;
    private String currencyPair;
    private String direction;
    private BigDecimal amount;
    private BigDecimal rate;
    private LocalDate valueDate;
    private String counterparty;
    private String matchStatus;
    private String externalRef;
    private String discrepancyDetail;
    private Integer retryCount;
    private LocalDateTime nextRetryAt;
    private String resolutionAction;
    private String resolutionComment;
}
