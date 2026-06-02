package com.forex.valuation.domain.model.valueobject;

public enum ValuationModelType {
    BLACK_SCHOLES("BS", "Black-Scholes"),
    GARMAN_KOHLHAGEN("GK", "Garman-Kohlhagen for FX options"),
    DISCOUNTED_CASH_FLOW("DCF", "Discounted Cash Flow for forwards/swaps"),
    MONTE_CARLO("MC", "Monte Carlo simulation for exotic products");

    private final String code;
    private final String description;

    ValuationModelType(String code, String description) {
        this.code = code;
        this.description = description;
    }

    public String getCode() {
        return code;
    }

    public String getDescription() {
        return description;
    }
}
