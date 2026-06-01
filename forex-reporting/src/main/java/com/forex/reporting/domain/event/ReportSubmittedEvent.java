package com.forex.reporting.domain.event;

import com.forex.common.base.domain.BaseDomainEvent;
import lombok.Getter;

@Getter
public class ReportSubmittedEvent extends BaseDomainEvent {

    private final Long reportId;
    private final String reportNo;
    private final String reportType;

    public ReportSubmittedEvent(Long reportId, String reportNo, String reportType) {
        super();
        this.reportId = reportId;
        this.reportNo = reportNo;
        this.reportType = reportType;
    }

    @Override
    public String eventName() {
        return "ReportSubmitted";
    }
}
