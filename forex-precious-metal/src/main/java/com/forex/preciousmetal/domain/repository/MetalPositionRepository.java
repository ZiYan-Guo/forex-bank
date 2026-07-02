package com.forex.preciousmetal.domain.repository;

import com.forex.preciousmetal.domain.model.aggregate.MetalPosition;

import java.util.Optional;

public interface MetalPositionRepository {
    void save(MetalPosition position);
    Optional<MetalPosition> findById(Long id);
    Optional<MetalPosition> findByCustomerAndMetal(Long customerId, String metalType);
}
