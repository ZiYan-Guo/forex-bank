package com.forex.trading.domain.event;

import com.forex.common.base.domain.BaseDomainEvent;
import lombok.Getter;

@Getter
public class TradeSettledEvent extends BaseDomainEvent {

    private final Long tradeId;
    private final String tradeNo;
    private final String settlementStatus;

    public TradeSettledEvent(Long tradeId, String tradeNo, String settlementStatus) {
        super();
        this.tradeId = tradeId;
        this.tradeNo = tradeNo;
        this.settlementStatus = settlementStatus;
    }

    @Override
    public String eventName() {
        return "FxTradeSettled";
    }
}
