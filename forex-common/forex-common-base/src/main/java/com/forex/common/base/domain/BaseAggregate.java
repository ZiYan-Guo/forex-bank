package com.forex.common.base.domain;

import java.io.Serializable;
import java.time.LocalDateTime;

import lombok.Getter;

@Getter
public abstract class BaseAggregate implements Serializable {

    private static final long serialVersionUID = 1L;

    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private Integer version;

    protected BaseAggregate() {
        this.createdAt = LocalDateTime.now();
        this.updatedAt = LocalDateTime.now();
        this.version = 0;
    }

    protected void markUpdated() {
        this.updatedAt = LocalDateTime.now();
        this.version++;
    }

    protected void setVersion(Integer version) {
        this.version = version;
    }

    protected abstract void validate();
}
