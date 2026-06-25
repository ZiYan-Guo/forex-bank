package com.forex.rate.domain.service;

import java.math.BigDecimal;
import java.math.RoundingMode;

import org.springframework.stereotype.Service;

import com.forex.rate.domain.model.aggregate.ExchangeRate;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
public class RatePublishDomainService {

    private static final BigDecimal MAX_REASONABLE_RATE = new BigDecimal("10000");
    private static final BigDecimal MIN_REASONABLE_RATE = new BigDecimal("0.0001");

    public ExchangeRate publishRate(ExchangeRate rate, String channelCode, BigDecimal spreadAdjust) {
        if (rate == null) {
            throw new IllegalArgumentException("rate must not be null");
        }
        if (channelCode == null || channelCode.isBlank()) {
            throw new IllegalArgumentException("channelCode must not be blank");
        }
        if (spreadAdjust != null) {
            if (spreadAdjust.compareTo(rate.getAskRate().subtract(rate.getBidRate())) > 0) {
                throw new IllegalArgumentException("spreadAdjust must not exceed current spread");
            }
            BigDecimal adjustedBid = rate.getBidRate().add(spreadAdjust.divide(new BigDecimal("2"), 8, RoundingMode.HALF_UP));
            BigDecimal adjustedAsk = rate.getAskRate().subtract(spreadAdjust.divide(new BigDecimal("2"), 8, RoundingMode.HALF_UP));
            BigDecimal adjustedMid = adjustedBid.add(adjustedAsk).divide(new BigDecimal("2"), 8, RoundingMode.HALF_UP);
            return ExchangeRate.create(rate.getCurrencyPair(), rate.getBaseCurrency(), rate.getQuoteCurrency(),
                    adjustedBid, adjustedAsk, channelCode);
        }
        return ExchangeRate.create(rate.getCurrencyPair(), rate.getBaseCurrency(), rate.getQuoteCurrency(),
                rate.getBidRate(), rate.getAskRate(), channelCode);
    }

    public boolean validateRate(ExchangeRate rate) {
        if (rate == null) {
            return false;
        }
        BigDecimal bidRate = rate.getBidRate();
        BigDecimal askRate = rate.getAskRate();
        if (bidRate == null || bidRate.compareTo(MIN_REASONABLE_RATE) < 0 || bidRate.compareTo(MAX_REASONABLE_RATE) > 0) {
            return false;
        }
        if (askRate == null || askRate.compareTo(MIN_REASONABLE_RATE) < 0 || askRate.compareTo(MAX_REASONABLE_RATE) > 0) {
            return false;
        }
        return true;
    }
}
