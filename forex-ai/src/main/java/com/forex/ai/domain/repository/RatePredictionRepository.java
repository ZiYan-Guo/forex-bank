package com.forex.ai.domain.repository;

import com.forex.ai.domain.model.aggregate.RatePrediction;

import java.util.List;
import java.util.Optional;

public interface RatePredictionRepository {

    Optional<RatePrediction> findByPredNo(String predNo);

    List<RatePrediction> findByCurrencyPairAndPredType(String currencyPair, String predType);

    void save(RatePrediction prediction);
}
