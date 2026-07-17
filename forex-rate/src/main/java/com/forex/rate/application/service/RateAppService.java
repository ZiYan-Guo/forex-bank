package com.forex.rate.application.service;

import java.math.BigDecimal;
import java.time.Duration;
import java.util.Map;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;

import com.forex.common.base.dto.PageResp;
import com.forex.rate.application.command.RateSaveCmd;
import com.forex.rate.application.query.RateQuery;
import com.forex.rate.domain.event.RateUpdatedEvent;
import com.forex.rate.domain.model.aggregate.ExchangeRate;
import com.forex.rate.domain.repository.ExchangeRateRepository;
import com.forex.rate.domain.service.RateCalculationDomainService;
import com.forex.rate.domain.service.RatePublishDomainService;
import com.github.benmanes.caffeine.cache.Cache;
import com.github.benmanes.caffeine.cache.Caffeine;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import com.forex.common.base.exception.BusinessException;
import com.forex.common.base.result.ResultCode;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional
public class RateAppService {

    private final ExchangeRateRepository exchangeRateRepository;
    private final RateCalculationDomainService rateCalculationDomainService;
    private final RatePublishDomainService ratePublishDomainService;
    private final ApplicationEventPublisher eventPublisher;

    private final Cache<String, ExchangeRate> rateCache = Caffeine.newBuilder()
            .expireAfterWrite(10, TimeUnit.SECONDS)
            .maximumSize(1000)
            .build();

    public ExchangeRate getRate(String currencyPair) {
        ExchangeRate cached = rateCache.getIfPresent(currencyPair);
        if (cached != null && !cached.isExpired(Duration.ofSeconds(10))) {
            log.debug("Rate cache hit, currencyPair={}, rateTime={}", currencyPair, cached.getRateTime());
            return cached;
        }
        ExchangeRate rate = exchangeRateRepository.findLatestByCurrencyPair(currencyPair)
                .orElseThrow(() -> new BusinessException(ResultCode.NOT_FOUND, "Rate not found for: " + currencyPair));
        rateCache.put(currencyPair, rate);
        log.debug("Rate cache refreshed, currencyPair={}, rateTime={}", currencyPair, rate.getRateTime());
        return rate;
    }

    @Transactional(readOnly = true)
    public Map<String, ExchangeRate> getAllRates() {
        return exchangeRateRepository.findLatestRates().stream()
                .collect(Collectors.toMap(ExchangeRate::getCurrencyPair, r -> r, (r1, r2) -> r1));
    }

    /**
     * 查询历史牌价分页数据，供交易前台回看牌价轨迹。
     * Queries paged historical rates for front-office rate review.
     */
    @Transactional(readOnly = true)
    public PageResp<ExchangeRate> pageQuery(RateQuery query) {
        log.info("Querying exchange rate history, currencyPair={}, startDate={}, endDate={}, pageNum={}, pageSize={}",
                query.getCurrencyPair(), query.getStartDate(), query.getEndDate(), query.getPageNum(), query.getPageSize());
        return exchangeRateRepository.pageQuery(query);
    }

    public BigDecimal calculateAmount(String currencyPair, BigDecimal amount) {
        ExchangeRate rate = getRate(currencyPair);
        return rateCalculationDomainService.calculateAmount(amount, rate.getMidRate());
    }

    public ExchangeRate saveRate(RateSaveCmd cmd) {
        log.info("Saving exchange rate, currencyPair={}, source={}, bidRate={}, askRate={}",
                cmd.getCurrencyPair(), cmd.getRateSource(), cmd.getBidRate(), cmd.getAskRate());
        ExchangeRate rate = ExchangeRate.create(cmd.getCurrencyPair(), cmd.getBaseCurrency(),
                cmd.getQuoteCurrency(), cmd.getBidRate(), cmd.getAskRate(), cmd.getRateSource());
        ExchangeRate saved = exchangeRateRepository.save(rate);
        rateCache.invalidate(saved.getCurrencyPair());
        eventPublisher.publishEvent(new RateUpdatedEvent(saved.getCurrencyPair(), saved.getBidRate(), saved.getAskRate()));
        log.info("Exchange rate saved and event published, id={}, currencyPair={}, rateTime={}",
                saved.getId(), saved.getCurrencyPair(), saved.getRateTime());
        return saved;
    }

    public BigDecimal convertCurrency(String fromCcy, String toCcy, BigDecimal amount) {
        String directPair = fromCcy + "_" + toCcy;
        String reversePair = toCcy + "_" + fromCcy;

        ExchangeRate directRate = null;
        try {
            directRate = getRate(directPair);
        } catch (BusinessException ex) {
            log.debug("Direct exchange rate not found, pair={}", directPair);
        }

        if (directRate != null) {
            return rateCalculationDomainService.calculateAmount(amount, directRate.getMidRate());
        }

        ExchangeRate reverseRate = null;
        try {
            reverseRate = getRate(reversePair);
        } catch (BusinessException ex) {
            log.debug("Reverse exchange rate not found, pair={}", reversePair);
        }

        if (reverseRate != null) {
            BigDecimal resolvedRate = BigDecimal.ONE.divide(reverseRate.getMidRate(), 8, java.math.RoundingMode.HALF_UP);
            return rateCalculationDomainService.calculateAmount(amount, resolvedRate);
        }

        ExchangeRate usdFrom = getRate("USD_" + fromCcy);
        ExchangeRate usdTo = getRate("USD_" + toCcy);
        BigDecimal crossRate = rateCalculationDomainService.calculateCrossRate(usdFrom.getMidRate(), usdTo.getMidRate());
        return rateCalculationDomainService.calculateAmount(amount, crossRate);
    }

    public void publishToChannels(Long rateId) {
        ExchangeRate rate = exchangeRateRepository.findById(rateId)
                .orElseThrow(() -> new BusinessException(ResultCode.NOT_FOUND, "Rate not found: " + rateId));
        ratePublishDomainService.validateRate(rate);
        log.info("Exchange rate validated for channel publish, rateId={}, currencyPair={}", rateId, rate.getCurrencyPair());
    }
}
