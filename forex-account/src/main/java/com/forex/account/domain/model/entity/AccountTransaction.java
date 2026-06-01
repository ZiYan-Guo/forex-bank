package com.forex.account.domain.model.entity;

import com.forex.common.base.domain.BaseEntity;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class AccountTransaction extends BaseEntity {

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

    public static AccountTransaction record(Long accountId, String accountNo,
                                             String transactionType, BigDecimal amount,
                                             String currency, BigDecimal balanceBefore,
                                             BigDecimal balanceAfter, String relatedBizNo,
                                             String relatedBizType, String summary) {
        AccountTransaction tx = new AccountTransaction();
        tx.transactionNo = "TXN" + UUID.randomUUID().toString().replace("-", "").substring(0, 24);
        tx.accountId = accountId;
        tx.accountNo = accountNo;
        tx.transactionType = transactionType;
        tx.amount = amount;
        tx.currency = currency;
        tx.balanceBefore = balanceBefore;
        tx.balanceAfter = balanceAfter;
        tx.relatedBizNo = relatedBizNo;
        tx.relatedBizType = relatedBizType;
        tx.transactionTime = LocalDateTime.now();
        tx.summary = summary;
        return tx;
    }
}
