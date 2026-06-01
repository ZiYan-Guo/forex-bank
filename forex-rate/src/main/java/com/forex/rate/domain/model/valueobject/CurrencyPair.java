package com.forex.rate.domain.model.valueobject;

import java.util.Objects;

import com.forex.common.base.domain.BaseValueObject;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class CurrencyPair extends BaseValueObject {

    private static final long serialVersionUID = 1L;

    private final String base;
    private final String quote;

    public String symbol() {
        return base.toUpperCase() + "_" + quote.toUpperCase();
    }

    public static CurrencyPair parse(String pair) {
        if (pair == null || pair.isBlank()) {
            throw new IllegalArgumentException("Currency pair must not be blank");
        }
        String[] parts = pair.split("_");
        if (parts.length != 2) {
            throw new IllegalArgumentException("Invalid currency pair format: " + pair + ", expected format: BASE_QUOTE (e.g. USD_CNY)");
        }
        return new CurrencyPair(parts[0].toUpperCase(), parts[1].toUpperCase());
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof CurrencyPair that)) return false;
        return Objects.equals(base, that.base) && Objects.equals(quote, that.quote);
    }

    @Override
    public int hashCode() {
        return Objects.hash(base, quote);
    }
}
