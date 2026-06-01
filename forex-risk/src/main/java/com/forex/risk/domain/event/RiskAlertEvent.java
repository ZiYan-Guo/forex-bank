package com.forex.risk.domain.event;

import com.forex.common.base.domain.BaseDomainEvent;
import lombok.Getter;

@Getter
public class RiskAlertEvent extends BaseDomainEvent {

    private final Long logId;
    private final String riskCategory;
    private final String riskLevel;

    public RiskAlertEvent(Long logId, String riskCategory, String riskLevel) {
        super();
        this.logId = logId;
        this.riskCategory = riskCategory;
        this.riskLevel = riskLevel;
    }

    @Override
    public String eventName() {
        return "RiskAlert";
    }
}
