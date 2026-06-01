package com.forex.rate.application.service;

import java.math.BigDecimal;
import java.time.Duration;
import java.util.Map;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;

import com.forex.rate.application.command.RateSaveCmd;
import com.forex.rate.domain.event.RateUpdatedEvent;
import com.forex.rate.domain.model.aggregate.ExchangeRate;
import com.forex.rate.domain.repository.ExchangeRateRepository;
import com.forex.rate.domain.service.RateCalculationDomainService;
import com.forex.rate.domain.service.RatePublishDomainService;
import com.github.benmanes.caffeine.cache.Cache;
import com.github.benmanes.caffeine.cache.Caffeine;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
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
            return cached;
        }
        ExchangeRate rate = exchangeRateRepository.findLatestByCurrencyPair(currencyPair)
                .orElseThrow(() -> new IllegalArgumentException("Rate not found for: " + currencyPair));
        rateCache.put(currencyPair, rate);
        return rate;
    }

    public Map<String, ExchangeRate> getAllRates() {
        return exchangeRateRepository.findLatestRates().stream()
                .collect(Collectors.toMap(ExchangeRate::getCurrencyPair, r -> r, (r1, r2) -> r1));
    }

    public BigDecimal calculateAmount(String currencyPair, BigDecimal amount) {
        ExchangeRate rate = getRate(currencyPair);
        return rateCalculationDomainService.calculateAmount(amount, rate.getMidRate());
    }

    public ExchangeRate saveRate(RateSaveCmd cmd) {
        ExchangeRate rate = ExchangeRate.create(cmd.getCurrencyPair(), cmd.getBaseCurrency(),
                cmd.getQuoteCurrency(), cmd.getBidRate(), cmd.getAskRate(), cmd.getRateSource());
        ExchangeRate saved = exchangeRateRepository.save(rate);
        rateCache.invalidate(saved.getCurrencyPair());
        eventPublisher.publishEvent(new RateUpdatedEvent(saved.getCurrencyPair(), saved.getBidRate(), saved.getAskRate()));
        return saved;
    }

    public BigDecimal convertCurrency(String fromCcy, String toCcy, BigDecimal amount) {
        String directPair = fromCcy + "_" + toCcy;
        String reversePair = toCcy + "_" + fromCcy;

        ExchangeRate directRate = null;
        try {
            directRate = getRate(directPair);
        } catch (IllegalArgumentException ignored) {
        }

        if (directRate != null) {
            return rateCalculationDomainService.calculateAmount(amount, directRate.getMidRate());
        }

        ExchangeRate reverseRate = null;
        try {
            reverseRate = getRate(reversePair);
        } catch (IllegalArgumentException ignored) {
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
                .orElseThrow(() -> new IllegalArgumentException("Rate not found: " + rateId));
        ratePublishDomainService.validateRate(rate);
    }
}
