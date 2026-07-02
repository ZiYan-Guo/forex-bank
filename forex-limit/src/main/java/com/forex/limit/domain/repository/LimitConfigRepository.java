package com.forex.limit.domain.repository;

import com.forex.limit.domain.model.aggregate.LimitConfig;

import java.util.List;
import java.util.Optional;

public interface LimitConfigRepository {
    void save(LimitConfig config);
    Optional<LimitConfig> findById(Long id);
    Optional<LimitConfig> findByLimitNo(String limitNo);
    List<LimitConfig> findActiveByCustomer(Long customerId, String limitType, String dimension, String dimensionValue);
    List<LimitConfig> findByCustomer(Long customerId);
}
