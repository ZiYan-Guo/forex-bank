package com.forex.notification.domain.event;

import com.forex.common.base.domain.BaseDomainEvent;

import lombok.Getter;

@Getter
public class NotificationSentEvent extends BaseDomainEvent {

    private final String notifyType;
    private final String bizNo;

    public NotificationSentEvent(String notifyType, String bizNo) {
        this.notifyType = notifyType;
        this.bizNo = bizNo;
    }

    @Override
    public String eventName() {
        return "NotificationSent";
    }
}
