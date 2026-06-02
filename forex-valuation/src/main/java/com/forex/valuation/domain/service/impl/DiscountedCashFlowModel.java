package com.forex.valuation.domain.service.impl;

import com.forex.valuation.domain.model.valueobject.ValuationInput;
import com.forex.valuation.domain.model.valueobject.ValuationModelType;
import com.forex.valuation.domain.service.ValuationModel;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;

@Slf4j
@Service
public class DiscountedCashFlowModel implements ValuationModel {

    @Override
    public ValuationModelType getType() {
        return ValuationModelType.DISCOUNTED_CASH_FLOW;
    }

    @Override
    public BigDecimal calculateFairValue(ValuationInput input) {
        double r = input.getDomesticRate().doubleValue();
        double T = input.getTimeToMaturity();
        double notional = input.getNotionalAmount().doubleValue();
        double forwardRate = input.getForwardRate() != null ? input.getForwardRate().doubleValue() : 0;
        double spotRate = input.getSpotRate().doubleValue();

        double diff = forwardRate - spotRate;
        double fv = diff * notional / Math.pow(1 + r, T);
        return BigDecimal.valueOf(fv).abs();
    }

    @Override
    public BigDecimal calculateDelta(ValuationInput input) {
        return input.getNotionalAmount();
    }

    @Override
    public BigDecimal calculateGamma(ValuationInput input) {
        return BigDecimal.ZERO;
    }

    @Override
    public BigDecimal calculateVega(ValuationInput input) {
        return BigDecimal.ZERO;
    }

    @Override
    public BigDecimal calculateTheta(ValuationInput input) {
        return BigDecimal.ZERO;
    }
}
