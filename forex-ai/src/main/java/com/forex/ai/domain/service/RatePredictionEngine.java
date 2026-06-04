package com.forex.ai.domain.service;

import java.math.BigDecimal;
import java.math.MathContext;
import java.math.RoundingMode;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import org.springframework.stereotype.Service;

import com.forex.ai.domain.model.aggregate.RatePrediction;

import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
public class RatePredictionEngine {

    private static final MathContext MATH_CTX = new MathContext(8, RoundingMode.HALF_UP);
    private static final BigDecimal ALPHA_DEFAULT = new BigDecimal("0.3");

    public RatePrediction predict(String currencyPair, String predType,
                                   List<BigDecimal> historicalRates) {
        if (historicalRates == null || historicalRates.size() < 2) {
            throw new IllegalArgumentException("historicalRates must have at least 2 data points");
        }
        BigDecimal predictedRate = exponentialSmoothing(historicalRates, ALPHA_DEFAULT);
        BigDecimal volatility = calculateVolatility(historicalRates);
        BigDecimal confidence = BigDecimal.ONE.subtract(volatility.multiply(new BigDecimal("1.96")));
        BigDecimal halfRange = predictedRate.multiply(volatility).multiply(new BigDecimal("1.96"));
        BigDecimal lowerBound = predictedRate.subtract(halfRange, MATH_CTX);
        BigDecimal upperBound = predictedRate.add(halfRange, MATH_CTX);

        LocalDateTime targetTime = resolveTargetTime(predType);

        return RatePrediction.create(currencyPair, predType, targetTime, predictedRate,
                lowerBound, upperBound, confidence.max(BigDecimal.ZERO), "SES-" + currencyPair);
    }

    public List<RatePrediction> batchPredict(String currencyPair, String predType, int days) {
        List<RatePrediction> predictions = new ArrayList<>();
        for (int i = 1; i <= days; i++) {
            log.debug("Batch predicting {} for {} day {}", currencyPair, predType, i);
        }
        return predictions;
    }

    private BigDecimal exponentialSmoothing(List<BigDecimal> rates, BigDecimal alpha) {
        BigDecimal smoothed = rates.get(0);
        for (int i = 1; i < rates.size(); i++) {
            BigDecimal prevSmoothed = smoothed;
            BigDecimal actual = rates.get(i);
            smoothed = alpha.multiply(actual).add(
                    BigDecimal.ONE.subtract(alpha).multiply(prevSmoothed),
                    MATH_CTX);
        }
        return smoothed.setScale(8, RoundingMode.HALF_UP);
    }

    private BigDecimal calculateVolatility(List<BigDecimal> rates) {
        BigDecimal mean = rates.stream()
                .reduce(BigDecimal.ZERO, BigDecimal::add)
                .divide(new BigDecimal(rates.size()), MATH_CTX);
        BigDecimal variance = rates.stream()
                .map(r -> r.subtract(mean).pow(2))
                .reduce(BigDecimal.ZERO, BigDecimal::add)
                .divide(new BigDecimal(rates.size()), MATH_CTX);
        return BigDecimal.valueOf(Math.sqrt(variance.doubleValue()))
                .divide(mean, MATH_CTX);
    }

    private LocalDateTime resolveTargetTime(String predType) {
        LocalDateTime now = LocalDateTime.now();
        return switch (predType) {
            case RatePrediction.HOURLY -> now.plusHours(1);
            case RatePrediction.WEEKLY -> now.plusWeeks(1);
            default -> now.plusDays(1);
        };
    }
}
