package com.forex.account.application.query;

import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
public class TransactionDTO {

    private Long id;
    private String transactionNo;
    private Long accountId;
    private String accountNo;
    private String transactionType;
    private BigDecimal amount;
    private String currency;
    private BigDecimal balanceBefore;
    private BigDecimal balanceAfter;
    private String relatedBizNo;
    private String relatedBizType;
    private LocalDateTime transactionTime;
    private String summary;
}
