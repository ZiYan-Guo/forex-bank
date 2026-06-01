package com.forex.exchange.domain.model.valueobject;

import com.forex.common.base.domain.BaseValueObject;
import lombok.Getter;

import java.util.Objects;

@Getter
public class CurrencyPair extends BaseValueObject {

    private final String base;
    private final String quote;

    private CurrencyPair(String base, String quote) {
        this.base = base;
        this.quote = quote;
    }

    public static CurrencyPair of(String base, String quote) {
        return new CurrencyPair(base, quote);
    }

    public String symbol() {
        return base + "/" + quote;
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
