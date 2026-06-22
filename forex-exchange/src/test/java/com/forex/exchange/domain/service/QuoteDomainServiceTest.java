package com.forex.exchange.domain.service;

import com.forex.exchange.domain.model.entity.ExchangeQuote;
import com.forex.exchange.domain.repository.ExchangeQuoteRepository;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class QuoteDomainServiceTest {

    @Mock private ExchangeQuoteRepository exchangeQuoteRepository;

    private QuoteDomainService quoteDomainService;

    @BeforeEach
    void setUp() {
        quoteDomainService = new QuoteDomainService(exchangeQuoteRepository);
    }

    @Test
    @DisplayName("Create quote generates mid rate and saves")
    void testCreateQuote() {
        when(exchangeQuoteRepository.save(any())).thenAnswer(i -> i.getArgument(0));

        ExchangeQuote result = quoteDomainService.createQuote(1001L, "USD", "CNY",
                new BigDecimal("7.2400"), new BigDecimal("7.2406"), 30);

        assertNotNull(result);
        assertEquals("USD", result.getBaseCurrency());
        assertTrue(result.getMidRate().compareTo(BigDecimal.ZERO) > 0);
        verify(exchangeQuoteRepository).save(any());
    }

    @Test
    @DisplayName("isQuoteValid returns true when not expired")
    void testIsQuoteValid_True() {
        ExchangeQuote quote = new ExchangeQuote(1L, 1001L, "USD", "CNY",
                new BigDecimal("7.2400"), new BigDecimal("7.2406"), new BigDecimal("7.2403"),
                LocalDateTime.now(), LocalDateTime.now().plusMinutes(5), 1);

        assertTrue(quoteDomainService.isQuoteValid(quote));
    }

    @Test
    @DisplayName("isQuoteValid returns false when expired")
    void testIsQuoteValid_Expired() {
        ExchangeQuote quote = new ExchangeQuote(2L, 1001L, "USD", "CNY",
                new BigDecimal("7.2400"), new BigDecimal("7.2406"), new BigDecimal("7.2403"),
                LocalDateTime.now().minusMinutes(10), LocalDateTime.now().minusMinutes(5), 1);

        assertFalse(quoteDomainService.isQuoteValid(quote));
    }

    @Test
    @DisplayName("isQuoteValid returns false for null")
    void testIsQuoteValid_Null() {
        assertFalse(quoteDomainService.isQuoteValid(null));
    }

    @Test
    @DisplayName("isQuoteValid returns false when expireTime is null")
    void testIsQuoteValid_NullExpireTime() {
        ExchangeQuote quote = new ExchangeQuote(3L, 1001L, "USD", "CNY",
                new BigDecimal("7.2400"), new BigDecimal("7.2406"), new BigDecimal("7.2403"),
                LocalDateTime.now(), null, 1);

        assertFalse(quoteDomainService.isQuoteValid(quote));
    }
}
