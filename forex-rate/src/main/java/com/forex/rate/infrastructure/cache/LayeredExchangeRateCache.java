package com.forex.rate.infrastructure.cache;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.forex.common.base.constant.CacheConstants;
import com.forex.rate.application.port.ExchangeRateCache;
import com.forex.rate.domain.model.aggregate.ExchangeRate;

import lombok.Data;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import com.github.benmanes.caffeine.cache.Cache;
import com.github.benmanes.caffeine.cache.Caffeine;

import java.math.BigDecimal;
import java.time.Duration;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Optional;
import java.util.concurrent.ThreadLocalRandom;
import java.util.concurrent.TimeUnit;

/**
 * Two-level cache for the latest exchange rate.
 * 最新汇率两级缓存实现。
 *
 * <p>L1 is Caffeine for local low latency; L2 is Redis for cross-instance sharing. The database
 * remains the source of truth and cache failures degrade to a database read.
 * L1 使用 Caffeine 降低本机延迟，L2 使用 Redis 支持多实例共享；数据库仍是事实来源，
 * 缓存异常时自动降级为数据库查询。</p>
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class LayeredExchangeRateCache implements ExchangeRateCache {

    private static final int MAX_CACHE_SIZE = 1_000;
    private static final int RANDOM_TTL_SECONDS = 5;

    private final ObjectMapper objectMapper;
    private final StringRedisTemplate stringRedisTemplate;

    private final Cache<String, ExchangeRate> localCache = Caffeine.newBuilder()
            .expireAfterWrite(CacheConstants.RATE_CACHE_TTL_SECONDS, TimeUnit.SECONDS)
            .maximumSize(MAX_CACHE_SIZE)
            .build();

    @Override
    public Optional<ExchangeRate> getLatest(String currencyPair, Duration freshness) {
        String normalizedPair = normalizePair(currencyPair);
        ExchangeRate localRate = localCache.getIfPresent(normalizedPair);
        if (isFresh(localRate, freshness)) {
            log.debug(
                    "Rate L1 cache hit / 汇率L1缓存命中, currencyPair={}, rateTime={}",
                    normalizedPair, localRate.getRateTime());
            return Optional.of(localRate);
        }
        localCache.invalidate(normalizedPair);

        String redisKey = redisKey(normalizedPair);
        try {
            String cachedJson = stringRedisTemplate.opsForValue().get(redisKey);
            if (!StringUtils.hasText(cachedJson)) {
                return Optional.empty();
            }
            ExchangeRate redisRate = toDomain(objectMapper.readValue(cachedJson, RateCacheValue.class));
            if (!isFresh(redisRate, freshness)) {
                safeDelete(redisKey);
                return Optional.empty();
            }
            localCache.put(normalizedPair, redisRate);
            log.debug(
                    "Rate L2 cache hit / 汇率L2缓存命中, currencyPair={}, rateTime={}",
                    normalizedPair, redisRate.getRateTime());
            return Optional.of(redisRate);
        } catch (JsonProcessingException exception) {
            log.warn(
                    "Rate L2 cache deserialize failed / 汇率L2缓存反序列化失败, currencyPair={}, keyDigest={}",
                    normalizedPair, Integer.toHexString(redisKey.hashCode()));
            safeDelete(redisKey);
        } catch (RuntimeException exception) {
            log.warn(
                    "Rate cache read failed, fallback to database / 汇率缓存读取失败，将回源数据库, currencyPair={}, keyDigest={}",
                    normalizedPair, Integer.toHexString(redisKey.hashCode()), exception);
        }
        return Optional.empty();
    }

    @Override
    public void putLatest(ExchangeRate rate) {
        String normalizedPair = normalizePair(rate.getCurrencyPair());
        localCache.put(normalizedPair, rate);
        String redisKey = redisKey(normalizedPair);
        try {
            String cacheValue = objectMapper.writeValueAsString(RateCacheValue.from(rate));
            long ttlSeconds = CacheConstants.RATE_CACHE_L2_TTL_SECONDS
                    + ThreadLocalRandom.current().nextLong(RANDOM_TTL_SECONDS);
            stringRedisTemplate.opsForValue().set(redisKey, cacheValue,
                    Duration.ofSeconds(ttlSeconds));
            log.debug(
                    "Rate cache populated / 汇率缓存已写入, currencyPair={}, ttlSeconds={}",
                    normalizedPair, ttlSeconds);
        } catch (JsonProcessingException exception) {
            log.warn(
                    "Rate cache serialize failed / 汇率缓存序列化失败, currencyPair={}",
                    normalizedPair, exception);
        } catch (RuntimeException exception) {
            log.warn(
                    "Rate cache write failed, local cache remains available / 汇率缓存写入失败，本地缓存继续可用, currencyPair={}, keyDigest={}",
                    normalizedPair, Integer.toHexString(redisKey.hashCode()), exception);
        }
    }

    @Override
    public void evictLatest(String currencyPair) {
        String normalizedPair = normalizePair(currencyPair);
        localCache.invalidate(normalizedPair);
        try {
            stringRedisTemplate.delete(redisKey(normalizedPair));
            log.debug("Rate cache evicted / 汇率缓存已失效, currencyPair={}", normalizedPair);
        } catch (RuntimeException exception) {
            log.warn(
                    "Rate cache eviction failed / 汇率缓存失效失败, currencyPair={}",
                    normalizedPair, exception);
        }
    }

    private boolean isFresh(ExchangeRate rate, Duration freshness) {
        return rate != null && !rate.isExpired(freshness);
    }

    private String normalizePair(String currencyPair) {
        return currencyPair == null ? "" : currencyPair.trim().toUpperCase().replace('/', '_');
    }

    private String redisKey(String currencyPair) {
        return cacheKey(currencyPair);
    }

    private String cacheKey(String currencyPair) {
        return CacheConstants.RATE_PREFIX + "latest:" + currencyPair;
    }

    private void safeDelete(String redisKey) {
        try {
            stringRedisTemplate.delete(redisKey);
        } catch (RuntimeException exception) {
            log.warn(
                    "Rate cache delete failed / 汇率缓存删除失败, keyDigest={}",
                    Integer.toHexString(redisKey.hashCode()), exception);
        }
    }

    private ExchangeRate toDomain(RateCacheValue value) {
        return ExchangeRate.reconstitute(
                value.id, value.currencyPair, value.baseCurrency, value.quoteCurrency,
                value.bidRate, value.askRate, value.midRate, value.spread, value.rateSource,
                value.rateDate, value.rateTime, value.status);
    }

    @Data
    static class RateCacheValue {

        private Long id;
        private String currencyPair;
        private String baseCurrency;
        private String quoteCurrency;
        private BigDecimal bidRate;
        private BigDecimal askRate;
        private BigDecimal midRate;
        private BigDecimal spread;
        private String rateSource;
        private LocalDate rateDate;
        private LocalDateTime rateTime;
        private Integer status;

        public static RateCacheValue from(ExchangeRate rate) {
            RateCacheValue value = new RateCacheValue();
            value.id = rate.getId();
            value.currencyPair = rate.getCurrencyPair();
            value.baseCurrency = rate.getBaseCurrency();
            value.quoteCurrency = rate.getQuoteCurrency();
            value.bidRate = rate.getBidRate();
            value.askRate = rate.getAskRate();
            value.midRate = rate.getMidRate();
            value.spread = rate.getSpread();
            value.rateSource = rate.getRateSource();
            value.rateDate = rate.getRateDate();
            value.rateTime = rate.getRateTime();
            value.status = rate.getStatus();
            return value;
        }
    }
}
