package com.forex.payment.domain.model.entity;

import com.forex.common.base.domain.BaseEntity;
import lombok.Getter;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Getter
public class PaymentReconciliation extends BaseEntity {

    public static final String STATUS_MATCHED = "MATCHED";
    public static final String STATUS_UNMATCHED = "UNMATCHED";
    public static final String STATUS_DIFFERENCE = "DIFFERENCE";

    private static final long serialVersionUID = 1L;

    private Long id;
    private Long paymentId;
    private String nostroAccount;
    private String currency;
    private String transactionRef;
    private LocalDate statementDate;
    private BigDecimal nostroAmount;
    private String nostroDirection;
    private BigDecimal systemAmount;
    private String systemDirection;
    private String reconciliationStatus;
    private LocalDateTime matchTime;
    private BigDecimal difference;

    public PaymentReconciliation(Long id, Long paymentId, String nostroAccount, String currency,
                                  String transactionRef, LocalDate statementDate,
                                  BigDecimal nostroAmount, String nostroDirection,
                                  BigDecimal systemAmount, String systemDirection,
                                  String reconciliationStatus, LocalDateTime matchTime,
                                  BigDecimal difference) {
        this.id = id;
        this.paymentId = paymentId;
        this.nostroAccount = nostroAccount;
        this.currency = currency;
        this.transactionRef = transactionRef;
        this.statementDate = statementDate;
        this.nostroAmount = nostroAmount;
        this.nostroDirection = nostroDirection;
        this.systemAmount = systemAmount;
        this.systemDirection = systemDirection;
        this.reconciliationStatus = reconciliationStatus;
        this.matchTime = matchTime;
        this.difference = difference;
    }

    public boolean isMatched() {
        return STATUS_MATCHED.equals(this.reconciliationStatus);
    }
}
