package com.forex.valuation.domain.repository;

import com.forex.valuation.domain.model.aggregate.PnlAttribution;

import java.util.List;
import java.util.Optional;

public interface PnlAttributionRepository {

    PnlAttribution save(PnlAttribution attribution);

    Optional<PnlAttribution> findById(Long id);

    List<PnlAttribution> findByTradeId(Long tradeId);
}
