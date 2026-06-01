package com.forex.position.domain.repository;

import com.forex.position.domain.model.entity.PositionLimitConfig;

import java.util.List;
import java.util.Optional;

public interface PositionLimitConfigRepository {

    PositionLimitConfig save(PositionLimitConfig config);

    Optional<PositionLimitConfig> findById(Long id);

    List<PositionLimitConfig> findByCurrencyAndType(String currency, String limitType);

    List<PositionLimitConfig> findAllEnabled();

    List<PositionLimitConfig> findByCurrency(String currency);
}
