package com.forex.risk.domain.repository;

import com.forex.common.base.dto.PageResp;
import com.forex.risk.domain.model.entity.RiskReport;
import com.forex.risk.domain.model.query.RiskQuery;

import java.util.Optional;

public interface RiskReportRepository {

    RiskReport save(RiskReport report);

    Optional<RiskReport> findById(Long id);

    Optional<RiskReport> findByReportNo(String reportNo);

    PageResp<RiskReport> pageQuery(RiskQuery query);
}
