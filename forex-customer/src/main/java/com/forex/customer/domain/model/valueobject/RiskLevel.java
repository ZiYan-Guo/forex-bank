package com.forex.customer.domain.model.valueobject;

import com.forex.common.base.domain.BaseValueObject;

import lombok.Getter;

import java.util.Objects;

@Getter
public class RiskLevel extends BaseValueObject {

    public static final int LOW = 1;
    public static final int MEDIUM = 2;
    public static final int HIGH = 3;
    public static final int PROHIBITED = 9;

    private final Integer level;

    private RiskLevel(Integer level) {
        if (level == null) {
            throw new IllegalArgumentException("风险等级不能为空");
        }
        if (level != LOW && level != MEDIUM && level != HIGH && level != PROHIBITED) {
            throw new IllegalArgumentException("无效的风险等级: " + level);
        }
        this.level = level;
    }

    public static RiskLevel of(Integer level) {
        return new RiskLevel(level);
    }

    public static RiskLevel low() {
        return new RiskLevel(LOW);
    }

    public static RiskLevel medium() {
        return new RiskLevel(MEDIUM);
    }

    public static RiskLevel high() {
        return new RiskLevel(HIGH);
    }

    public static RiskLevel prohibited() {
        return new RiskLevel(PROHIBITED);
    }

    public RiskLevel upgrade() {
        if (this.level == PROHIBITED || this.level == HIGH) {
            return this;
        }
        return new RiskLevel(this.level + 1);
    }

    public RiskLevel downgrade() {
        if (this.level == LOW) {
            return this;
        }
        if (this.level == PROHIBITED) {
            return new RiskLevel(HIGH);
        }
        return new RiskLevel(this.level - 1);
    }

    public boolean isLow() {
        return level == LOW;
    }

    public boolean isMedium() {
        return level == MEDIUM;
    }

    public boolean isHigh() {
        return level == HIGH;
    }

    public boolean isProhibited() {
        return level == PROHIBITED;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof RiskLevel that)) return false;
        return Objects.equals(level, that.level);
    }

    @Override
    public int hashCode() {
        return Objects.hash(level);
    }

    @Override
    public String toString() {
        return "RiskLevel(level=" + level + ")";
    }
}
