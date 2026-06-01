package com.forex.customer.domain.model.entity;

import com.forex.common.base.domain.BaseEntity;

import lombok.Getter;

import java.math.BigDecimal;
import java.time.LocalDate;

@Getter
public class CreditLimit extends BaseEntity {

    private Long id;
    private Long customerId;
    private String limitType;
    private String currency;
    private BigDecimal totalLimit;
    private BigDecimal usedLimit;
    private BigDecimal availableLimit;
    private LocalDate effectiveDate;
    private LocalDate expireDate;
    private Integer status;

    public CreditLimit(Long id, Long customerId, String limitType, String currency,
                       BigDecimal totalLimit, BigDecimal usedLimit, BigDecimal availableLimit,
                       LocalDate effectiveDate, LocalDate expireDate, Integer status) {
        this.id = id;
        this.customerId = customerId;
        this.limitType = limitType;
        this.currency = currency;
        this.totalLimit = totalLimit;
        this.usedLimit = usedLimit;
        this.availableLimit = availableLimit;
        this.effectiveDate = effectiveDate;
        this.expireDate = expireDate;
        this.status = status;
    }

    public void deduct(BigDecimal amount) {
        if (amount == null || amount.compareTo(BigDecimal.ZERO) <= 0) {
            throw new IllegalArgumentException("扣减金额必须大于0");
        }
        if (!isAvailable()) {
            throw new IllegalStateException("信用额度不可用");
        }
        if (isExpired()) {
            throw new IllegalStateException("信用额度已过期");
        }
        if (this.availableLimit.compareTo(amount) < 0) {
            throw new IllegalStateException("可用额度不足");
        }
        this.usedLimit = this.usedLimit.add(amount);
        this.availableLimit = this.availableLimit.subtract(amount);
    }

    public void release(BigDecimal amount) {
        if (amount == null || amount.compareTo(BigDecimal.ZERO) <= 0) {
            throw new IllegalArgumentException("释放金额必须大于0");
        }
        if (this.usedLimit.compareTo(amount) < 0) {
            throw new IllegalStateException("释放金额超过已用额度");
        }
        this.usedLimit = this.usedLimit.subtract(amount);
        this.availableLimit = this.availableLimit.add(amount);
    }

    public boolean isAvailable() {
        return this.status != null && this.status == 1;
    }

    public boolean isExpired() {
        return this.expireDate != null && this.expireDate.isBefore(LocalDate.now());
    }

    public boolean hasSufficientLimit(BigDecimal amount) {
        return isAvailable() && !isExpired()
                && this.availableLimit != null
                && this.availableLimit.compareTo(amount) >= 0;
    }
}
