package com.forex.risk.domain.repository;

import com.forex.risk.domain.model.entity.RiskParamConfig;

import java.util.List;
import java.util.Optional;

public interface RiskParamConfigRepository {

    List<RiskParamConfig> findAllEnabled();

    Optional<RiskParamConfig> findByParamKey(String key);

    List<RiskParamConfig> findByParamType(String type);
}
