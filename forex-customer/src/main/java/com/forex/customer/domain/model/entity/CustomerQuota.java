package com.forex.customer.domain.model.entity;

import com.forex.common.base.domain.BaseEntity;

import lombok.Getter;

import java.math.BigDecimal;
import com.forex.common.base.exception.BusinessException;
import com.forex.common.base.result.ResultCode;

@Getter
public class CustomerQuota extends BaseEntity {

    private Long id;
    private Long customerId;
    private String quotaYear;
    private String quotaType;
    private BigDecimal quotaAmount;
    private BigDecimal usedAmount;
    private String currency;

    public CustomerQuota(Long id, Long customerId, String quotaYear, String quotaType,
                         BigDecimal quotaAmount, BigDecimal usedAmount, String currency) {
        this.id = id;
        this.customerId = customerId;
        this.quotaYear = quotaYear;
        this.quotaType = quotaType;
        this.quotaAmount = quotaAmount;
        this.usedAmount = usedAmount;
        this.currency = currency;
    }

    public void deduct(BigDecimal amount) {
        if (amount == null || amount.compareTo(BigDecimal.ZERO) <= 0) {
            throw new BusinessException(ResultCode.VALIDATE_FAIL, "扣减金额必须大于0");
        }
        if (isExceeded(amount)) {
            throw new IllegalStateException("配额不足");
        }
        this.usedAmount = this.usedAmount.add(amount);
    }

    public void release(BigDecimal amount) {
        if (amount == null || amount.compareTo(BigDecimal.ZERO) <= 0) {
            throw new BusinessException(ResultCode.VALIDATE_FAIL, "释放金额必须大于0");
        }
        if (this.usedAmount.compareTo(amount) < 0) {
            throw new IllegalStateException("释放金额超过已用配额");
        }
        this.usedAmount = this.usedAmount.subtract(amount);
    }

    public BigDecimal getAvailable() {
        return this.quotaAmount.subtract(this.usedAmount);
    }

    public boolean isExceeded(BigDecimal amount) {
        return getAvailable().compareTo(amount) < 0;
    }
}
