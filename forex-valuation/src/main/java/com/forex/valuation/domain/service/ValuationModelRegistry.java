package com.forex.valuation.domain.service;

import com.forex.valuation.domain.model.valueobject.ValuationModelType;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

@Slf4j
@Service
public class ValuationModelRegistry {

    private final Map<ValuationModelType, ValuationModel> modelMap;

    public ValuationModelRegistry(List<ValuationModel> models) {
        this.modelMap = models.stream()
                .collect(Collectors.toMap(ValuationModel::getType, Function.identity()));
        log.info("Registered {} valuation models: {}", modelMap.size(), modelMap.keySet());
    }

    public ValuationModel getModel(ValuationModelType type) {
        ValuationModel model = modelMap.get(type);
        if (model == null) {
            throw new IllegalArgumentException("No valuation model found for type: " + type);
        }
        return model;
    }
}
