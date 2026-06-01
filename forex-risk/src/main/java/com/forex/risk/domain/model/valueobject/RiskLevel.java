package com.forex.risk.domain.model.valueobject;

import com.forex.common.base.domain.BaseValueObject;
import lombok.Getter;

import java.util.Objects;

@Getter
public class RiskLevel extends BaseValueObject {
    private final String code;
    private final String name;
    private final int severity;

    public static final RiskLevel LOW = new RiskLevel("LOW", "低风险", 0);
    public static final RiskLevel MEDIUM = new RiskLevel("MEDIUM", "中风险", 1);
    public static final RiskLevel HIGH = new RiskLevel("HIGH", "高风险", 2);
    public static final RiskLevel WARNING = new RiskLevel("WARNING", "预警", 3);
    public static final RiskLevel BREACH = new RiskLevel("BREACH", "超限", 4);

    public RiskLevel(String code, String name, int severity) {
        this.code = code;
        this.name = name;
        this.severity = severity;
    }

    public boolean isHigherThan(RiskLevel other) {
        return this.severity > other.severity;
    }

    public boolean needsAction() {
        return severity >= 2;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof RiskLevel that)) return false;
        return Objects.equals(code, that.code);
    }

    @Override
    public int hashCode() {
        return Objects.hash(code);
    }
}
