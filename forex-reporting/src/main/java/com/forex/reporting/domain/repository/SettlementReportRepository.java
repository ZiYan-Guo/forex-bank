package com.forex.reporting.domain.repository;

import com.forex.reporting.domain.model.entity.ForexSettlementReport;

import java.util.Optional;

public interface SettlementReportRepository {

    ForexSettlementReport save(ForexSettlementReport report);

    Optional<ForexSettlementReport> findById(Long id);

    Optional<ForexSettlementReport> findByReportNo(String reportNo);

    Optional<ForexSettlementReport> findByExchangeOrderNo(String exchangeOrderNo);
}
