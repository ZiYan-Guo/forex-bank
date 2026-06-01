package com.forex.reporting.domain.repository;

import com.forex.reporting.domain.model.entity.CapitalAccountReport;

import java.util.Optional;

public interface CapitalReportRepository {

    CapitalAccountReport save(CapitalAccountReport report);

    Optional<CapitalAccountReport> findById(Long id);

    Optional<CapitalAccountReport> findByReportNo(String reportNo);
}
