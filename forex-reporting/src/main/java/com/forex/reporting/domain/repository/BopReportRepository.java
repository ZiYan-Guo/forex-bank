package com.forex.reporting.domain.repository;

import com.forex.common.base.dto.PageResp;
import com.forex.reporting.domain.model.aggregate.BopReport;
import com.forex.reporting.domain.model.query.ReportQuery;

import java.util.Optional;

public interface BopReportRepository {

    BopReport save(BopReport report);

    Optional<BopReport> findById(Long id);

    Optional<BopReport> findByReportNo(String reportNo);

    PageResp<BopReport> pageQuery(ReportQuery query);
}
