package com.forex.limit.domain.model.aggregate;

import com.forex.common.base.domain.BaseAggregate;
import com.forex.common.base.exception.BusinessException;
import com.forex.common.base.result.ResultCode;
import lombok.Getter;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Getter
public class LimitConfig extends BaseAggregate {

    private Long id;
    private String limitNo;
    private Long customerId;
    private String limitType;
    private String dimension;
    private String dimensionValue;
    private BigDecimal limitAmount;
    private String currency;
    private String limitPeriod;
    private LocalDate effectiveDate;
    private LocalDate expiryDate;
    private BigDecimal usedAmount;
    private BigDecimal availableAmount;
    private String limitStatus;
    private String approveStatus;
    private LocalDateTime createTime;

    private LimitConfig() {
        super();
    }

    public static LimitConfig create(Long customerId, String limitType, String dimension,
                                      String dimensionValue, BigDecimal limitAmount, String currency,
                                      String limitPeriod, LocalDate effectiveDate, LocalDate expiryDate) {
        LimitConfig config = new LimitConfig();
        config.customerId = customerId;
        config.limitType = limitType;
        config.dimension = dimension;
        config.dimensionValue = dimensionValue;
        config.limitAmount = limitAmount;
        config.currency = currency;
        config.limitPeriod = limitPeriod;
        config.effectiveDate = effectiveDate;
        config.expiryDate = expiryDate;
        config.usedAmount = BigDecimal.ZERO;
        config.availableAmount = limitAmount;
        config.limitStatus = "ACTIVE";
        config.approveStatus = "PENDING";
        config.createTime = LocalDateTime.now();
        config.validate();
        return config;
    }

    public void utilize(BigDecimal amount) {
        if (this.availableAmount.compareTo(amount) < 0) {
            throw new BusinessException(ResultCode.VALIDATE_FAIL,
                    "限额不足，当前可用额度: " + this.availableAmount + " " + this.currency);
        }
        this.usedAmount = this.usedAmount.add(amount);
        this.availableAmount = this.limitAmount.subtract(this.usedAmount);
        if (this.availableAmount.compareTo(this.limitAmount.multiply(new BigDecimal("0.2"))) <= 0) {
            this.limitStatus = "WARNING";
        }
        markUpdated();
    }

    public void release(BigDecimal amount) {
        this.usedAmount = this.usedAmount.subtract(amount);
        if (this.usedAmount.compareTo(BigDecimal.ZERO) < 0) {
            this.usedAmount = BigDecimal.ZERO;
        }
        this.availableAmount = this.limitAmount.subtract(this.usedAmount);
        this.limitStatus = "ACTIVE";
        markUpdated();
    }

    public void approve() {
        this.approveStatus = "APPROVED";
        this.limitStatus = "ACTIVE";
        markUpdated();
    }

    public void reject() {
        this.approveStatus = "REJECTED";
        this.limitStatus = "INACTIVE";
        markUpdated();
    }

    public void suspend() {
        this.limitStatus = "SUSPENDED";
        markUpdated();
    }

    public boolean isExceeded(BigDecimal requestedAmount) {
        return this.availableAmount.compareTo(requestedAmount) < 0;
    }

    public void assignLimitNo(String limitNo) {
        this.limitNo = limitNo;
    }

    @Override
    protected void validate() {
        if (customerId == null) {
            throw new BusinessException(ResultCode.VALIDATE_FAIL, "客户ID不能为空");
        }
        if (limitAmount == null || limitAmount.compareTo(BigDecimal.ZERO) <= 0) {
            throw new BusinessException(ResultCode.VALIDATE_FAIL, "限额金额必须大于0");
        }
        if (effectiveDate != null && expiryDate != null && effectiveDate.isAfter(expiryDate)) {
            throw new BusinessException(ResultCode.VALIDATE_FAIL, "生效日期不能晚于到期日期");
        }
    }
}
