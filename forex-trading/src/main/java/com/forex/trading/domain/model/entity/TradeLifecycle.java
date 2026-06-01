package com.forex.trading.domain.model.entity;

import com.forex.common.base.domain.BaseEntity;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
public class TradeLifecycle extends BaseEntity {

    private Long id;
    private Long tradeId;
    private String tradeNo;
    private String eventType;
    private LocalDateTime eventTime;
    private String beforeStatus;
    private String afterStatus;
    private String eventData;
    private Long operatorId;

    public TradeLifecycle(Long id, Long tradeId, String tradeNo, String eventType,
                           LocalDateTime eventTime, String beforeStatus, String afterStatus,
                           String eventData, Long operatorId) {
        this.id = id;
        this.tradeId = tradeId;
        this.tradeNo = tradeNo;
        this.eventType = eventType;
        this.eventTime = eventTime;
        this.beforeStatus = beforeStatus;
        this.afterStatus = afterStatus;
        this.eventData = eventData;
        this.operatorId = operatorId;
    }
}
