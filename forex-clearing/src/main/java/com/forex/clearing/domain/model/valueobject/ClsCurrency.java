package com.forex.clearing.domain.model.valueobject;

import java.util.Arrays;
import java.util.List;

public enum ClsCurrency {

    USD, EUR, JPY, GBP, CHF, CAD, AUD, SEK, DKK, NOK,
    SGD, HKD, NZD, KRW, ZAR, MXN, HUF, ILS;

    public static boolean isSupported(String currencyCode) {
        if (currencyCode == null || currencyCode.isBlank()) {
            return false;
        }
        for (ClsCurrency c : values()) {
            if (c.name().equalsIgnoreCase(currencyCode)) {
                return true;
            }
        }
        return false;
    }

    public static List<ClsCurrency> getSupportedCurrencies() {
        return Arrays.asList(values());
    }
}
