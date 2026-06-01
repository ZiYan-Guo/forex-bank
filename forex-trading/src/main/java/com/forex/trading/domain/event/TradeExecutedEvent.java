package com.forex.trading.domain.event;

import com.forex.common.base.domain.BaseDomainEvent;
import lombok.Getter;

import java.math.BigDecimal;

@Getter
public class TradeExecutedEvent extends BaseDomainEvent {

    private final Long tradeId;
    private final String tradeNo;
    private final String tradeType;
    private final BigDecimal amount;
    private final String buyCcy;
    private final String sellCcy;

    public TradeExecutedEvent(Long tradeId, String tradeNo, String tradeType,
                               BigDecimal amount, String buyCcy, String sellCcy) {
        super();
        this.tradeId = tradeId;
        this.tradeNo = tradeNo;
        this.tradeType = tradeType;
        this.amount = amount;
        this.buyCcy = buyCcy;
        this.sellCcy = sellCcy;
    }

    @Override
    public String eventName() {
        return "FxTradeExecuted";
    }
}
