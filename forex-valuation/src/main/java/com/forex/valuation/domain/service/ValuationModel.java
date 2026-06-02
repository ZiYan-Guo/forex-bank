package com.forex.valuation.domain.service;

import com.forex.valuation.domain.model.valueobject.ValuationInput;
import com.forex.valuation.domain.model.valueobject.ValuationModelType;

import java.math.BigDecimal;

public interface ValuationModel {
    ValuationModelType getType();
    BigDecimal calculateFairValue(ValuationInput input);
    BigDecimal calculateDelta(ValuationInput input);
    BigDecimal calculateGamma(ValuationInput input);
    BigDecimal calculateVega(ValuationInput input);
    BigDecimal calculateTheta(ValuationInput input);
}
