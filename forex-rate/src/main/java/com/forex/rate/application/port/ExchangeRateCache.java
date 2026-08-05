package com.forex.rate.application.port;

import com.forex.rate.domain.model.aggregate.ExchangeRate;

import java.time.Duration;
import java.util.Optional;

/**
 * Cache port for the latest exchange rate.
 * 最新汇率缓存端口。
 *
 * <p>The application layer depends on this port rather than Redis or Caffeine directly.
 * 应用层依赖该端口而不是直接依赖 Redis 或 Caffeine，保持领域分层清晰。</p>
 */
public interface ExchangeRateCache {

    Optional<ExchangeRate> getLatest(String currencyPair, Duration freshness);

    void putLatest(ExchangeRate rate);

    void evictLatest(String currencyPair);
}
