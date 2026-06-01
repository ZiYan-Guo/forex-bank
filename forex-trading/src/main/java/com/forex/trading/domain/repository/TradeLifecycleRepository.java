package com.forex.trading.domain.repository;

import com.forex.trading.domain.model.entity.TradeLifecycle;

import java.util.List;

public interface TradeLifecycleRepository {

    TradeLifecycle save(TradeLifecycle event);

    List<TradeLifecycle> findByTradeId(Long tradeId);
}
