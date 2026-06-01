package com.forex.ocr.domain.event;

import com.forex.common.base.domain.BaseDomainEvent;

import lombok.Getter;

@Getter
public class OcrCompletedEvent extends BaseDomainEvent {

    private final Long taskId;
    private final String docType;

    public OcrCompletedEvent(Long taskId, String docType) {
        this.taskId = taskId;
        this.docType = docType;
    }

    @Override
    public String eventName() {
        return "OcrCompleted";
    }
}
