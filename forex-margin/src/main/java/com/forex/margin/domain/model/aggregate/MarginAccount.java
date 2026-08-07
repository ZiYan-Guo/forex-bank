package com.forex.margin.domain.model.aggregate;

import com.forex.common.base.domain.BaseAggregate;
import com.forex.margin.domain.model.valueobject.WaterLevel;
import lombok.Getter;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import com.forex.common.base.exception.BusinessException;
import com.forex.common.base.result.ResultCode;

@Getter
/** Margin account aggregate root. 保证金账户聚合根。 */
public class MarginAccount extends BaseAggregate {

    private Long id;
    private String marginNo;
    private Long customerId;
    private Long tradeId;
    private String marginType;
    private String marginCurrency;
    private BigDecimal requiredAmount;
    private BigDecimal depositedAmount;
    private BigDecimal shortfallAmount;
    private BigDecimal marginRate;
    private LocalDateTime callDate;
    private LocalDateTime dueDate;
    private String status;
    private String collateralType;
    private String releaseReason;
    private String waterLevel;
    private BigDecimal collateralValue;

    private MarginAccount() {
        super();
    }

    public static MarginAccount create(Long customerId, Long tradeId, String marginType,
                                        String marginCurrency, BigDecimal requiredAmount,
                                        BigDecimal marginRate, String collateralType) {
        MarginAccount account = new MarginAccount();
        account.customerId = customerId;
        account.tradeId = tradeId;
        account.marginType = marginType;
        account.marginCurrency = marginCurrency;
        account.requiredAmount = requiredAmount;
        account.marginRate = marginRate;
        account.collateralType = collateralType;
        account.depositedAmount = BigDecimal.ZERO;
        account.shortfallAmount = requiredAmount;
        account.status = "PENDING";
        account.validate();
        return account;
    }

    public static BigDecimal calculateMarginRate(int tenorMonths) {
        if (tenorMonths <= 12) return new BigDecimal("0.07");
        if (tenorMonths <= 36) return new BigDecimal("0.12");
        return new BigDecimal("0.15");
    }

    public static BigDecimal calculateRequiredAmount(BigDecimal notional, int tenorMonths, BigDecimal volatilityPct) {
        BigDecimal rate = calculateMarginRate(tenorMonths);
        BigDecimal volAdj = BigDecimal.ONE.add(volatilityPct != null ? volatilityPct : BigDecimal.ZERO);
        return notional.multiply(rate).multiply(volAdj).setScale(2, java.math.RoundingMode.HALF_UP);
    }

    public void pay() {
        this.depositedAmount = this.requiredAmount;
        this.shortfallAmount = BigDecimal.ZERO;
        this.status = "PAID";
        markUpdated();
    }

    public void partialPay(BigDecimal amount) {
        if (amount == null || amount.compareTo(BigDecimal.ZERO) <= 0) {
            throw new BusinessException(ResultCode.VALIDATE_FAIL, "存入金额必须大于0");
        }
        this.depositedAmount = this.depositedAmount.add(amount);
        this.shortfallAmount = calculateShortfall();
        this.status = this.shortfallAmount.compareTo(BigDecimal.ZERO) > 0 ? "PARTIAL" : "PAID";
        markUpdated();
    }

    public void cancel(String reason) {
        if (!"PENDING".equals(this.status) && !"CALLED".equals(this.status)) {
            throw new BusinessException(ResultCode.VALIDATE_FAIL, "只有待缴或追缴状态的保证金才能取消");
        }
        this.status = "CANCELLED";
        this.releaseReason = reason;
        markUpdated();
    }

    public WaterLevel checkWaterLevel() {
        if (requiredAmount.compareTo(BigDecimal.ZERO) == 0) {
            return WaterLevel.evaluate(BigDecimal.ZERO, BigDecimal.ONE);
        }
        return WaterLevel.evaluate(depositedAmount, requiredAmount);
    }

    public BigDecimal getDepositRatio() {
        if (requiredAmount.compareTo(BigDecimal.ZERO) == 0) return BigDecimal.ZERO;
        return depositedAmount.divide(requiredAmount, 4, java.math.RoundingMode.HALF_UP)
                .multiply(BigDecimal.valueOf(100));
    }

    public static MarginAccount reconstitute(Long id, String marginNo, Long customerId,
                                              Long tradeId, String marginType,
                                              String marginCurrency, BigDecimal requiredAmount,
                                              BigDecimal depositedAmount, BigDecimal shortfallAmount,
                                              BigDecimal marginRate, LocalDateTime callDate,
                                              LocalDateTime dueDate, String status,
                                              String collateralType, String releaseReason,
                                              BigDecimal collateralValue, String waterLevel) {
        MarginAccount account = new MarginAccount();
        account.id = id;
        account.marginNo = marginNo;
        account.customerId = customerId;
        account.tradeId = tradeId;
        account.marginType = marginType;
        account.marginCurrency = marginCurrency;
        account.requiredAmount = requiredAmount;
        account.depositedAmount = depositedAmount;
        account.shortfallAmount = shortfallAmount;
        account.marginRate = marginRate;
        account.callDate = callDate;
        account.dueDate = dueDate;
        account.status = status;
        account.collateralType = collateralType;
        account.releaseReason = releaseReason;
        account.collateralValue = collateralValue != null ? collateralValue : BigDecimal.ZERO;
        account.waterLevel = waterLevel;
        return account;
    }

    /** Deposit margin. Transitions to SUFFICIENT when fully covered. 存入保证金。 */
    public void deposit(BigDecimal amount) {
        if (amount == null || amount.compareTo(BigDecimal.ZERO) <= 0) {
            throw new BusinessException(ResultCode.VALIDATE_FAIL, "存入保证金金额必须大于0");
        }
        this.depositedAmount = this.depositedAmount.add(amount);
        this.shortfallAmount = calculateShortfall();
        if (this.shortfallAmount.compareTo(BigDecimal.ZERO) <= 0) {
            this.status = "SUFFICIENT";
        }
        markUpdated();
    }

    /** Release margin. Only allowed when sufficient. Validates remaining shortfall. 释放保证金，需足额。 */
    public void release(BigDecimal amount, String reason) {
        if (amount == null || amount.compareTo(BigDecimal.ZERO) <= 0) {
            throw new BusinessException(ResultCode.VALIDATE_FAIL, "释放保证金金额必须大于0");
        }
        if (this.depositedAmount.compareTo(amount) < 0) {
            throw new BusinessException(ResultCode.VALIDATE_FAIL, "已存保证金不足，无法释放");
        }
        if (!"SUFFICIENT".equals(this.status) && !"PAID".equals(this.status)) {
            throw new BusinessException(ResultCode.VALIDATE_FAIL, "只有足额保证金才能释放");
        }
        this.depositedAmount = this.depositedAmount.subtract(amount);
        this.shortfallAmount = calculateShortfall();
        this.releaseReason = reason;
        if (this.shortfallAmount.compareTo(BigDecimal.ZERO) > 0) {
            this.status = "CALLED";
        } else {
            this.status = "RELEASED";
        }
        markUpdated();
    }

    /** Issue margin call for additional amount. 追缴保证金。 */
    public void call(BigDecimal additionalAmount) {
        if (additionalAmount == null || additionalAmount.compareTo(BigDecimal.ZERO) <= 0) {
            throw new BusinessException(ResultCode.VALIDATE_FAIL, "追加保证金金额必须大于0");
        }
        this.requiredAmount = this.requiredAmount.add(additionalAmount);
        this.shortfallAmount = calculateShortfall();
        this.callDate = LocalDateTime.now();
        this.status = "CALLED";
        markUpdated();
    }

    public BigDecimal calculateShortfall() {
        BigDecimal shortfall = this.requiredAmount.subtract(this.depositedAmount);
        return shortfall.compareTo(BigDecimal.ZERO) > 0 ? shortfall : BigDecimal.ZERO;
    }

    public void assignMarginNo(String marginNo) {
        this.marginNo = marginNo;
    }

    /**
     * Set water level after evaluation. 设置水位线。
     */
    public void setWaterLevel(String level) {
        this.waterLevel = level;
    }

    @Override
    protected void validate() {
        if (customerId == null) {
            throw new BusinessException(ResultCode.VALIDATE_FAIL, "客户ID不能为空");
        }
        if (marginType == null || marginType.isBlank()) {
            throw new BusinessException(ResultCode.VALIDATE_FAIL, "保证金类型不能为空");
        }
        if (marginCurrency == null || marginCurrency.isBlank()) {
            throw new BusinessException(ResultCode.VALIDATE_FAIL, "保证金币种不能为空");
        }
        if (requiredAmount == null || requiredAmount.compareTo(BigDecimal.ZERO) < 0) {
            throw new BusinessException(ResultCode.VALIDATE_FAIL, "保证金要求金额不能为负数");
        }
    }
}
