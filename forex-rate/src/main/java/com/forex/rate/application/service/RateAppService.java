package com.forex.rate.application.service;

import java.math.BigDecimal;
import java.time.Duration;
import java.util.Map;
import java.util.stream.Collectors;

import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;

import com.forex.common.base.constant.CacheConstants;
import com.forex.common.base.dto.PageResp;
import com.forex.rate.application.command.RateSaveCmd;
import com.forex.rate.application.query.RateQuery;
import com.forex.rate.application.port.ExchangeRateCache;
import com.forex.rate.domain.event.RateUpdatedEvent;
import com.forex.rate.domain.model.aggregate.ExchangeRate;
import com.forex.rate.domain.repository.ExchangeRateRepository;
import com.forex.rate.domain.service.RateCalculationDomainService;
import com.forex.rate.domain.service.RatePublishDomainService;
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
    private final ExchangeRateCache exchangeRateCache;

    public ExchangeRate getRate(String currencyPair) {
        ExchangeRate cached = exchangeRateCache
                .getLatest(currencyPair, Duration.ofSeconds(CacheConstants.RATE_CACHE_TTL_SECONDS))
                .orElse(null);
        if (cached != null) {
            return cached;
        }
        ExchangeRate rate = exchangeRateRepository.findLatestByCurrencyPair(currencyPair)
                .orElseThrow(() -> new BusinessException(ResultCode.NOT_FOUND, "Rate not found for: " + currencyPair));
        exchangeRateCache.putLatest(rate);
        log.debug("Rate loaded from database / 汇率已从数据库加载, currencyPair={}, rateTime={}",
                currencyPair, rate.getRateTime());
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
        exchangeRateCache.putLatest(saved);
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
