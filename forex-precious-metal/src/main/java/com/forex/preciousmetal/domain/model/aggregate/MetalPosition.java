package com.forex.preciousmetal.domain.model.aggregate;

import com.forex.common.base.domain.BaseAggregate;
import com.forex.common.base.exception.BusinessException;
import com.forex.common.base.result.ResultCode;
import lombok.Getter;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Getter
public class MetalPosition extends BaseAggregate {

    private Long id;
    private String positionNo;
    private Long customerId;
    private String metalType;
    private BigDecimal totalWeight;
    private BigDecimal availableWeight;
    private BigDecimal lockedWeight;
    private String weightUnit;
    private BigDecimal averageCost;
    private String currency;
    private BigDecimal marketPrice;
    private BigDecimal unrealizedPnl;
    private String storageLocation;
    private LocalDate lastValuationDate;
    private LocalDateTime updateTime;

    private MetalPosition() {
        super();
    }

    public static MetalPosition create(Long customerId, String metalType, BigDecimal weight,
                                        String weightUnit, BigDecimal cost, String currency,
                                        String storageLocation) {
        MetalPosition position = new MetalPosition();
        position.customerId = customerId;
        position.metalType = metalType;
        position.totalWeight = weight;
        position.availableWeight = weight;
        position.lockedWeight = BigDecimal.ZERO;
        position.weightUnit = weightUnit;
        position.averageCost = cost;
        position.currency = currency;
        position.marketPrice = cost;
        position.unrealizedPnl = BigDecimal.ZERO;
        position.storageLocation = storageLocation;
        position.lastValuationDate = LocalDate.now();
        position.updateTime = LocalDateTime.now();
        position.validate();
        return position;
    }

    public void addWeight(BigDecimal weight, BigDecimal cost) {
        BigDecimal totalCost = this.totalWeight.multiply(this.averageCost)
                .add(weight.multiply(cost));
        this.totalWeight = this.totalWeight.add(weight);
        this.averageCost = totalCost.divide(this.totalWeight, 6, BigDecimal.ROUND_HALF_UP);
        this.availableWeight = this.availableWeight.add(weight);
        recalculatePnl();
        markUpdated();
    }

    public void lock(BigDecimal weight) {
        if (this.availableWeight.compareTo(weight) < 0) {
            throw new BusinessException(ResultCode.VALIDATE_FAIL, "可用库存不足");
        }
        this.availableWeight = this.availableWeight.subtract(weight);
        this.lockedWeight = this.lockedWeight.add(weight);
        markUpdated();
    }

    public void unlock(BigDecimal weight) {
        if (this.lockedWeight.compareTo(weight) < 0) {
            throw new BusinessException(ResultCode.VALIDATE_FAIL, "锁定库存不足");
        }
        this.lockedWeight = this.lockedWeight.subtract(weight);
        this.availableWeight = this.availableWeight.add(weight);
        markUpdated();
    }

    public void deliver(BigDecimal weight) {
        if (this.lockedWeight.compareTo(weight) < 0) {
            throw new BusinessException(ResultCode.VALIDATE_FAIL, "已锁定库存不足");
        }
        this.lockedWeight = this.lockedWeight.subtract(weight);
        this.totalWeight = this.totalWeight.subtract(weight);
        recalculatePnl();
        markUpdated();
    }

    public void markToMarket(BigDecimal newPrice) {
        this.marketPrice = newPrice;
        this.lastValuationDate = LocalDate.now();
        recalculatePnl();
        markUpdated();
    }

    private void recalculatePnl() {
        this.unrealizedPnl = this.totalWeight.multiply(
                this.marketPrice.subtract(this.averageCost));
    }

    public void assignPositionNo(String positionNo) {
        this.positionNo = positionNo;
    }

    @Override
    protected void validate() {
        if (customerId == null) {
            throw new BusinessException(ResultCode.VALIDATE_FAIL, "客户ID不能为空");
        }
        if (metalType == null || metalType.isBlank()) {
            throw new BusinessException(ResultCode.VALIDATE_FAIL, "贵金属类型不能为空");
        }
    }
}
