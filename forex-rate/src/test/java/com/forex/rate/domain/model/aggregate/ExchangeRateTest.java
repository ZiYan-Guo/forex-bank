package com.forex.rate.domain.model.aggregate;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

class ExchangeRateTest {

    @Test
    @DisplayName("Reconstitute keeps persisted identity and timestamps")
    void testReconstituteKeepsPersistedFields() {
        LocalDate rateDate = LocalDate.of(2026, 7, 16);
        LocalDateTime rateTime = LocalDateTime.of(2026, 7, 16, 9, 30);

        ExchangeRate rate = ExchangeRate.reconstitute(
                1001L,
                "USD_CNY",
                "USD",
                "CNY",
                new BigDecimal("7.23450000"),
                new BigDecimal("7.24560000"),
                new BigDecimal("7.24005000"),
                new BigDecimal("0.01110000"),
                "CFETS",
                rateDate,
                rateTime,
                1
        );

        assertEquals(1001L, rate.getId());
        assertEquals(rateDate, rate.getRateDate());
        assertEquals(rateTime, rate.getRateTime());
        assertEquals(0, rate.getMidRate().compareTo(new BigDecimal("7.24005000")));
        assertEquals(0, rate.getSpread().compareTo(new BigDecimal("0.01110000")));
    }
}
