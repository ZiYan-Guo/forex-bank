package com.forex.common.base.domain;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.UUID;

import lombok.Getter;

@Getter
public abstract class BaseDomainEvent implements Serializable {

    private static final long serialVersionUID = 1L;

    private final String eventId;
    private final LocalDateTime occurredAt;

    protected BaseDomainEvent() {
        this.eventId = UUID.randomUUID().toString();
        this.occurredAt = LocalDateTime.now();
    }

    public abstract String eventName();
}
