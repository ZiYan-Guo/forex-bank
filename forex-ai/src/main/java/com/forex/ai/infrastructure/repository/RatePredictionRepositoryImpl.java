package com.forex.ai.infrastructure.repository;

import com.forex.ai.domain.model.aggregate.RatePrediction;
import com.forex.ai.domain.repository.RatePredictionRepository;
import com.forex.ai.infrastructure.mapper.RatePredictionMapper;
import com.forex.ai.infrastructure.persistence.RatePredictionPO;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
@RequiredArgsConstructor
public class RatePredictionRepositoryImpl implements RatePredictionRepository {

    private final RatePredictionMapper ratePredictionMapper;

    @Override
    public Optional<RatePrediction> findByPredNo(String predNo) {
        RatePredictionPO po = ratePredictionMapper.findByPredNo(predNo);
        return Optional.ofNullable(po).map(this::toDomain);
    }

    @Override
    public List<RatePrediction> findByCurrencyPairAndPredType(String currencyPair, String predType) {
        return ratePredictionMapper.findByCurrencyPairAndPredType(currencyPair, predType).stream()
                .map(this::toDomain)
                .toList();
    }

    @Override
    public void save(RatePrediction prediction) {
        RatePredictionPO po = toPO(prediction);
        if (po.getId() == null) {
            ratePredictionMapper.insert(po);
        } else {
            ratePredictionMapper.updateById(po);
        }
    }

    private RatePrediction toDomain(RatePredictionPO po) {
        return RatePrediction.reconstitute(
                po.getId(), po.getPredNo(), po.getCurrencyPair(), po.getPredType(),
                po.getPredTime(), po.getTargetTime(), po.getPredictedRate(),
                po.getLowerBound(), po.getUpperBound(), po.getConfidence(), po.getModelName(),
                po.getCreateTime(), po.getUpdateTime(), po.getVersion());
    }

    private RatePredictionPO toPO(RatePrediction prediction) {
        RatePredictionPO po = new RatePredictionPO();
        po.setId(prediction.getId());
        po.setPredNo(prediction.getPredNo());
        po.setCurrencyPair(prediction.getCurrencyPair());
        po.setPredType(prediction.getPredType());
        po.setPredTime(prediction.getPredTime());
        po.setTargetTime(prediction.getTargetTime());
        po.setPredictedRate(prediction.getPredictedRate());
        po.setLowerBound(prediction.getLowerBound());
        po.setUpperBound(prediction.getUpperBound());
        po.setConfidence(prediction.getConfidence());
        po.setModelName(prediction.getModelName());
        return po;
    }
}
