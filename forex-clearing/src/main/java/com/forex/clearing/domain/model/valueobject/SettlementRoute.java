package com.forex.clearing.domain.model.valueobject;

import com.forex.common.base.domain.BaseValueObject;
import lombok.Getter;

import java.math.BigDecimal;
import java.util.List;
import java.util.Objects;
import com.forex.common.base.exception.BusinessException;
import com.forex.common.base.result.ResultCode;

@Getter
public class SettlementRoute extends BaseValueObject {

    private final String routeId;
    private final String channelCode;
    private final BigDecimal totalCost;
    private final int estimatedHours;
    private final BigDecimal routeScore;
    private final String recommendation;
    private final List<String> intermediaryBanks;

    public SettlementRoute(String routeId, String channelCode, BigDecimal totalCost,
                           int estimatedHours, BigDecimal routeScore, String recommendation,
                           List<String> intermediaryBanks) {
        if (routeId == null || routeId.isBlank()) {
            throw new BusinessException(ResultCode.VALIDATE_FAIL, "路由ID不能为空");
        }
        this.routeId = routeId;
        this.channelCode = channelCode;
        this.totalCost = totalCost;
        this.estimatedHours = estimatedHours;
        this.routeScore = routeScore;
        this.recommendation = recommendation;
        this.intermediaryBanks = intermediaryBanks;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof SettlementRoute that)) return false;
        return Objects.equals(routeId, that.routeId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(routeId);
    }

    @Override
    public String toString() {
        return "SettlementRoute(routeId=" + routeId + ", channelCode=" + channelCode
                + ", recommendation=" + recommendation + ")";
    }
}
