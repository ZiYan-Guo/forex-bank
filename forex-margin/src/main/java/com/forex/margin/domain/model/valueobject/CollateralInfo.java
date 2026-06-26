package com.forex.margin.domain.model.valueobject;

import com.forex.common.base.domain.BaseValueObject;
import lombok.Getter;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Objects;
import com.forex.common.base.exception.BusinessException;
import com.forex.common.base.result.ResultCode;

@Getter
public class CollateralInfo extends BaseValueObject {

    private final String collateralType;
    private final BigDecimal haircutPct;
    private final BigDecimal marketValue;
    private final BigDecimal collateralValue;

    private CollateralInfo(String collateralType, BigDecimal marketValue, BigDecimal haircutPct) {
        if (collateralType == null || collateralType.isBlank()) {
            throw new BusinessException(ResultCode.VALIDATE_FAIL, "担保品类型不能为空");
        }
        if (marketValue == null || marketValue.compareTo(BigDecimal.ZERO) < 0) {
            throw new BusinessException(ResultCode.VALIDATE_FAIL, "市场价值不能为负数");
        }
        if (haircutPct == null || haircutPct.compareTo(BigDecimal.ZERO) < 0) {
            throw new BusinessException(ResultCode.VALIDATE_FAIL, "折扣率不能为负数");
        }
        this.collateralType = collateralType;
        this.marketValue = marketValue;
        this.haircutPct = haircutPct;
        this.collateralValue = calcCollateralValue();
    }

    public static CollateralInfo of(String type, BigDecimal marketValue, BigDecimal haircutPct) {
        return new CollateralInfo(type, marketValue, haircutPct);
    }

    public BigDecimal getCollateralValue() {
        return this.collateralValue;
    }

    private BigDecimal calcCollateralValue() {
        BigDecimal divisor = new BigDecimal("100");
        BigDecimal haircutRatio = BigDecimal.ONE.subtract(haircutPct.divide(divisor, 6, RoundingMode.HALF_UP));
        return marketValue.multiply(haircutRatio).setScale(2, RoundingMode.HALF_UP);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof CollateralInfo that)) return false;
        return Objects.equals(collateralType, that.collateralType)
                && Objects.equals(haircutPct, that.haircutPct)
                && Objects.equals(marketValue, that.marketValue)
                && Objects.equals(collateralValue, that.collateralValue);
    }

    @Override
    public int hashCode() {
        return Objects.hash(collateralType, haircutPct, marketValue, collateralValue);
    }
}
