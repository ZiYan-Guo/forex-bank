package com.forex.valuation.domain.repository;

import com.forex.common.base.dto.PageResp;
import com.forex.valuation.domain.model.aggregate.ValuationResult;
import com.forex.valuation.domain.model.query.ValuationQuery;

import java.util.List;
import java.util.Optional;

public interface ValuationResultRepository {

    ValuationResult save(ValuationResult valuationResult);

    Optional<ValuationResult> findById(Long id);

    List<ValuationResult> findByTradeId(Long tradeId);

    PageResp<ValuationResult> pageQuery(ValuationQuery query);
}
