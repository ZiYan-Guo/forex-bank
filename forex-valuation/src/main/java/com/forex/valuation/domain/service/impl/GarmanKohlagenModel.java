package com.forex.valuation.domain.service.impl;

import com.forex.valuation.domain.model.valueobject.ValuationInput;
import com.forex.valuation.domain.model.valueobject.ValuationModelType;
import com.forex.valuation.domain.service.ValuationModel;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;

@Slf4j
@Service
public class GarmanKohlagenModel implements ValuationModel {

    private static final double PI = Math.PI;

    @Override
    public ValuationModelType getType() {
        return ValuationModelType.GARMAN_KOHLHAGEN;
    }

    @Override
    public BigDecimal calculateFairValue(ValuationInput input) {
        double S = input.getSpotRate().doubleValue();
        double K = input.getStrikePrice().doubleValue();
        double rd = input.getDomesticRate().doubleValue();
        double rf = input.getForeignRate().doubleValue();
        double sigma = input.getVolatility().doubleValue();
        double T = input.getTimeToMaturity();
        boolean isCall = "CALL".equals(input.getCallPut());

        double d1 = (Math.log(S / K) + (rd - rf + sigma * sigma / 2) * T) / (sigma * Math.sqrt(T));
        double d2 = d1 - sigma * Math.sqrt(T);

        double price;
        if (isCall) {
            price = S * Math.exp(-rf * T) * normalCDF(d1) - K * Math.exp(-rd * T) * normalCDF(d2);
        } else {
            price = K * Math.exp(-rd * T) * normalCDF(-d2) - S * Math.exp(-rf * T) * normalCDF(-d1);
        }
        return BigDecimal.valueOf(price).abs();
    }

    @Override
    public BigDecimal calculateDelta(ValuationInput input) {
        double S = input.getSpotRate().doubleValue();
        double K = input.getStrikePrice().doubleValue();
        double rd = input.getDomesticRate().doubleValue();
        double rf = input.getForeignRate().doubleValue();
        double sigma = input.getVolatility().doubleValue();
        double T = input.getTimeToMaturity();
        boolean isCall = "CALL".equals(input.getCallPut());

        double d1 = (Math.log(S / K) + (rd - rf + sigma * sigma / 2) * T) / (sigma * Math.sqrt(T));
        double delta = isCall
                ? Math.exp(-rf * T) * normalCDF(d1)
                : Math.exp(-rf * T) * (normalCDF(d1) - 1);
        return BigDecimal.valueOf(delta);
    }

    @Override
    public BigDecimal calculateGamma(ValuationInput input) {
        double S = input.getSpotRate().doubleValue();
        double K = input.getStrikePrice().doubleValue();
        double rd = input.getDomesticRate().doubleValue();
        double rf = input.getForeignRate().doubleValue();
        double sigma = input.getVolatility().doubleValue();
        double T = input.getTimeToMaturity();

        double d1 = (Math.log(S / K) + (rd - rf + sigma * sigma / 2) * T) / (sigma * Math.sqrt(T));
        double gamma = Math.exp(-rf * T) * normalPDF(d1) / (S * sigma * Math.sqrt(T));
        return BigDecimal.valueOf(gamma);
    }

    @Override
    public BigDecimal calculateVega(ValuationInput input) {
        double S = input.getSpotRate().doubleValue();
        double K = input.getStrikePrice().doubleValue();
        double rd = input.getDomesticRate().doubleValue();
        double rf = input.getForeignRate().doubleValue();
        double sigma = input.getVolatility().doubleValue();
        double T = input.getTimeToMaturity();

        double d1 = (Math.log(S / K) + (rd - rf + sigma * sigma / 2) * T) / (sigma * Math.sqrt(T));
        double vega = S * Math.exp(-rf * T) * normalPDF(d1) * Math.sqrt(T) / 100;
        return BigDecimal.valueOf(vega);
    }

    @Override
    public BigDecimal calculateTheta(ValuationInput input) {
        double S = input.getSpotRate().doubleValue();
        double K = input.getStrikePrice().doubleValue();
        double rd = input.getDomesticRate().doubleValue();
        double rf = input.getForeignRate().doubleValue();
        double sigma = input.getVolatility().doubleValue();
        double T = input.getTimeToMaturity();
        boolean isCall = "CALL".equals(input.getCallPut());

        double d1 = (Math.log(S / K) + (rd - rf + sigma * sigma / 2) * T) / (sigma * Math.sqrt(T));
        double d2 = d1 - sigma * Math.sqrt(T);

        double term1 = -(S * Math.exp(-rf * T) * normalPDF(d1) * sigma) / (2 * Math.sqrt(T));
        double term2;
        if (isCall) {
            term2 = rd * K * Math.exp(-rd * T) * normalCDF(d2)
                    - rf * S * Math.exp(-rf * T) * normalCDF(d1);
        } else {
            term2 = -rd * K * Math.exp(-rd * T) * normalCDF(-d2)
                    + rf * S * Math.exp(-rf * T) * normalCDF(-d1);
        }
        double theta = (term1 + term2) / 365.0;
        return BigDecimal.valueOf(theta);
    }

    private double normalCDF(double x) {
        double a1 = 0.254829592;
        double a2 = -0.284496736;
        double a3 = 1.421413741;
        double a4 = -1.453152027;
        double a5 = 1.061405429;
        double p = 0.3275911;
        int sign = x < 0 ? -1 : 1;
        x = Math.abs(x) / Math.sqrt(2.0);
        double t = 1.0 / (1.0 + p * x);
        double erf = 1.0 - (((((a5 * t + a4) * t) + a3) * t + a2) * t + a1) * t * Math.exp(-x * x);
        return 0.5 * (1.0 + sign * erf);
    }

    private double normalPDF(double x) {
        return Math.exp(-0.5 * x * x) / Math.sqrt(2 * PI);
    }
}
