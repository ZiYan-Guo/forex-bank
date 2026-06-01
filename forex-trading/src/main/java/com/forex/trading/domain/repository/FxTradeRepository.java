package com.forex.trading.domain.repository;

import com.forex.common.base.dto.PageResp;
import com.forex.trading.domain.model.aggregate.FxTrade;
import com.forex.trading.domain.model.query.TradeQuery;

import java.util.List;
import java.util.Optional;

public interface FxTradeRepository {

    FxTrade save(FxTrade trade);

    Optional<FxTrade> findById(Long id);

    Optional<FxTrade> findByTradeNo(String tradeNo);

    List<FxTrade> findByCustomerId(Long customerId);

    PageResp<FxTrade> pageQuery(TradeQuery query);
}
