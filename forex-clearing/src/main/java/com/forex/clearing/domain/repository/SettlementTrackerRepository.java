package com.forex.clearing.domain.repository;

import com.forex.clearing.domain.model.aggregate.SettlementTracker;

import java.util.List;
import java.util.Optional;

public interface SettlementTrackerRepository {

    SettlementTracker save(SettlementTracker tracker);

    Optional<SettlementTracker> findById(Long id);

    Optional<SettlementTracker> findByTrackingId(String trackingId);

    List<SettlementTracker> findAll();
}
