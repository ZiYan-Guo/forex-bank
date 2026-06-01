package com.forex.rate.domain.repository;

import java.util.List;
import java.util.Optional;

import com.forex.common.base.dto.PageReq;
import com.forex.common.base.dto.PageResp;
import com.forex.rate.domain.model.aggregate.ExchangeRate;

public interface ExchangeRateRepository {

    ExchangeRate save(ExchangeRate rate);

    Optional<ExchangeRate> findById(Long id);

    Optional<ExchangeRate> findLatestByCurrencyPair(String currencyPair);

    List<ExchangeRate> findLatestRates();

    PageResp<ExchangeRate> pageQuery(PageReq pageReq);
}
