package com.forex.clearing.domain.repository;

import com.forex.clearing.domain.model.aggregate.ClsSession;

import java.time.LocalDate;
import java.util.Optional;

public interface ClsSessionRepository {

    ClsSession save(ClsSession session);

    Optional<ClsSession> findById(Long id);

    Optional<ClsSession> findBySessionId(String sessionId);

    Optional<ClsSession> findBySettlementDate(LocalDate settlementDate);
}
