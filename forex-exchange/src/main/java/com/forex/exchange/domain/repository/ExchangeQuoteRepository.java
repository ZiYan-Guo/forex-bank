package com.forex.exchange.domain.repository;

import com.forex.exchange.domain.model.entity.ExchangeQuote;

import java.util.Optional;

public interface ExchangeQuoteRepository {

    ExchangeQuote save(ExchangeQuote quote);

    Optional<ExchangeQuote> findLatestQuote(Long customerId, String baseCcy, String quoteCcy);
}
