package com.forex.exchange.domain.service;

import com.forex.exchange.domain.model.entity.ExchangeQuote;
import com.forex.exchange.domain.repository.ExchangeQuoteRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDateTime;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional
public class QuoteDomainService {

    private final ExchangeQuoteRepository exchangeQuoteRepository;

    public ExchangeQuote createQuote(Long customerId, String baseCcy, String quoteCcy,
                                      BigDecimal bidRate, BigDecimal askRate, int validSeconds) {
        BigDecimal midRate = bidRate.add(askRate).divide(BigDecimal.valueOf(2), 8, RoundingMode.HALF_UP);
        LocalDateTime now = LocalDateTime.now();
        ExchangeQuote quote = new ExchangeQuote(
                null, customerId, baseCcy, quoteCcy,
                bidRate, askRate, midRate,
                now, now.plusSeconds(validSeconds), 1);
        ExchangeQuote saved = exchangeQuoteRepository.save(quote);
        log.info("Created exchange quote for customer: {}, pair: {}/{}", customerId, baseCcy, quoteCcy);
        return saved;
    }

    public boolean isQuoteValid(ExchangeQuote quote) {
        if (quote == null || quote.getExpireTime() == null) {
            return false;
        }
        return LocalDateTime.now().isBefore(quote.getExpireTime());
    }
}
