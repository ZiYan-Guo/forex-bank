package com.forex.ai.domain.model.aggregate;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDateTime;
import java.util.UUID;

import com.forex.common.base.domain.BaseAggregate;

import lombok.Getter;

@Getter
public class RatePrediction extends BaseAggregate {

    private static final long serialVersionUID = 1L;

    public static final String HOURLY = "HOURLY";
    public static final String DAILY = "DAILY";
    public static final String WEEKLY = "WEEKLY";

    private Long id;
    private String predNo;
    private String currencyPair;
    private String predType;
    private LocalDateTime predTime;
    private LocalDateTime targetTime;
    private BigDecimal predictedRate;
    private BigDecimal lowerBound;
    private BigDecimal upperBound;
    private BigDecimal confidence;
    private String modelName;

    private RatePrediction() {
        super();
    }

    public static RatePrediction create(String currencyPair, String predType,
                                         LocalDateTime targetTime, BigDecimal predictedRate,
                                         BigDecimal lowerBound, BigDecimal upperBound,
                                         BigDecimal confidence, String modelName) {
        RatePrediction pred = new RatePrediction();
        pred.predNo = "PRED-" + UUID.randomUUID().toString().substring(0, 8).toUpperCase();
        pred.currencyPair = currencyPair;
        pred.predType = predType;
        pred.predTime = LocalDateTime.now();
        pred.targetTime = targetTime;
        pred.predictedRate = predictedRate;
        pred.lowerBound = lowerBound;
        pred.upperBound = upperBound;
        pred.confidence = confidence;
        pred.modelName = modelName;
        pred.validate();
        return pred;
    }

    public static RatePrediction reconstitute(Long id, String predNo, String currencyPair,
                                               String predType, LocalDateTime predTime,
                                               LocalDateTime targetTime, BigDecimal predictedRate,
                                               BigDecimal lowerBound, BigDecimal upperBound,
                                               BigDecimal confidence, String modelName,
                                               LocalDateTime createdAt, LocalDateTime updatedAt,
                                               Integer version) {
        RatePrediction pred = new RatePrediction();
        pred.id = id;
        pred.predNo = predNo;
        pred.currencyPair = currencyPair;
        pred.predType = predType;
        pred.predTime = predTime;
        pred.targetTime = targetTime;
        pred.predictedRate = predictedRate;
        pred.lowerBound = lowerBound;
        pred.upperBound = upperBound;
        pred.confidence = confidence;
        pred.modelName = modelName;
        return pred;
    }

    public BigDecimal calculateRange() {
        return upperBound.subtract(lowerBound).setScale(8, RoundingMode.HALF_UP);
    }

    public boolean isAccurate(BigDecimal actual, BigDecimal tolerance) {
        if (actual == null || tolerance == null) {
            return false;
        }
        BigDecimal diff = predictedRate.subtract(actual).abs();
        return diff.compareTo(tolerance) <= 0;
    }

    @Override
    protected void validate() {
        if (currencyPair == null || currencyPair.isBlank()) {
            throw new IllegalArgumentException("currencyPair must not be blank");
        }
        if (predType == null || predType.isBlank()) {
            throw new IllegalArgumentException("predType must not be blank");
        }
        if (predictedRate == null || predictedRate.compareTo(BigDecimal.ZERO) <= 0) {
            throw new IllegalArgumentException("predictedRate must be greater than 0");
        }
        if (lowerBound != null && upperBound != null && lowerBound.compareTo(upperBound) > 0) {
            throw new IllegalArgumentException("lowerBound must not exceed upperBound");
        }
    }
}
