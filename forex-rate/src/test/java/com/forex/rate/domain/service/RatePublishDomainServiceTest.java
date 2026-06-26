package com.forex.rate.domain.service;

import com.forex.rate.domain.model.aggregate.ExchangeRate;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;

import static org.junit.jupiter.api.Assertions.*;
import com.forex.common.base.exception.BusinessException;

class RatePublishDomainServiceTest {

    private final RatePublishDomainService service = new RatePublishDomainService();

    @Test
    @DisplayName("Publish rate creates new rate from source")
    void testPublishRate() {
        ExchangeRate rate = ExchangeRate.create("USD/CNY", "USD", "CNY",
                new BigDecimal("7.2400"), new BigDecimal("7.2406"), "Reuters");

        ExchangeRate published = service.publishRate(rate, "ONLINE", null);

        assertNotNull(published);
        assertEquals("USD/CNY", published.getCurrencyPair());
        assertEquals(0, published.getBidRate().compareTo(new BigDecimal("7.2400")));
    }

    @Test
    @DisplayName("Publish rate with spread adjustment")
    void testPublishRate_WithSpread() {
        ExchangeRate rate = ExchangeRate.create("USD/CNY", "USD", "CNY",
                new BigDecimal("7.2400"), new BigDecimal("7.2410"), "Reuters");

        ExchangeRate published = service.publishRate(rate, "ONLINE",
                new BigDecimal("0.0004"));

        assertNotNull(published);
        assertTrue(published.getBidRate().compareTo(new BigDecimal("7.2400")) > 0);
        assertTrue(published.getAskRate().compareTo(new BigDecimal("7.2410")) < 0);
    }

    @Test
    @DisplayName("Publish rate throws when spread exceeds gap")
    void testPublishRate_SpreadExceeds() {
        ExchangeRate rate = ExchangeRate.create("USD/CNY", "USD", "CNY",
                new BigDecimal("7.2400"), new BigDecimal("7.2410"), "Reuters");

        assertThrows(BusinessException.class,
                () -> service.publishRate(rate, "ONLINE", new BigDecimal("0.0100")));
    }

    @Test
    @DisplayName("Validate rate returns true for valid rate")
    void testValidateRate_Valid() {
        ExchangeRate rate = ExchangeRate.create("EUR/USD", "EUR", "USD",
                new BigDecimal("1.1000"), new BigDecimal("1.1004"), "Reuters");
        assertTrue(service.validateRate(rate));
    }

    @Test
    @DisplayName("Validate rate returns false for extreme bid")
    void testValidateRate_ExtremeBid() {
        ExchangeRate rate = ExchangeRate.create("USD/CNY", "USD", "CNY",
                new BigDecimal("0.00001"), new BigDecimal("7.2410"), "Reuters");
        assertFalse(service.validateRate(rate));
    }

    @Test
    @DisplayName("Validate rate returns false for null")
    void testValidateRate_Null() {
        assertFalse(service.validateRate(null));
    }
}
