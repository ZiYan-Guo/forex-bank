package com.forex.margin.domain.model.valueobject;

import com.forex.common.base.domain.BaseValueObject;
import lombok.Getter;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Objects;

@Getter
public class WaterLevel extends BaseValueObject {

    public static final String NEEDS_ACTION = "NEEDS_ACTION";

    private final String level;
    private final BigDecimal ratio;
    private final String description;

    private WaterLevel(String level, BigDecimal ratio, String description) {
        this.level = level;
        this.ratio = ratio;
        this.description = description;
    }

    public static WaterLevel evaluate(BigDecimal deposited, BigDecimal required) {
        if (required.compareTo(BigDecimal.ZERO) == 0) {
            return new WaterLevel("SAFE", BigDecimal.ZERO, "无要求金额");
        }
        BigDecimal ratio = deposited.divide(required, 4, RoundingMode.HALF_UP)
                .multiply(BigDecimal.valueOf(100));
        if (ratio.compareTo(BigDecimal.valueOf(100)) >= 0) {
            return new WaterLevel("SAFE", ratio, "保证金充足");
        } else if (ratio.compareTo(BigDecimal.valueOf(50)) >= 0) {
            return new WaterLevel("MARGIN_CALL", ratio, "保证金不足，需追缴");
        } else {
            return new WaterLevel("FORCE_LIQUIDATION", ratio, "保证金严重不足，强制平仓");
        }
    }

    public boolean needsAction() {
        return !"SAFE".equals(level);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof WaterLevel that)) return false;
        return Objects.equals(level, that.level);
    }

    @Override
    public int hashCode() {
        return Objects.hash(level);
    }
}
