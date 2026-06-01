package com.forex.risk.domain.repository;

import com.forex.common.base.dto.PageResp;
import com.forex.risk.domain.model.aggregate.RiskMonitorLog;
import com.forex.risk.domain.model.query.RiskQuery;

import java.util.List;
import java.util.Optional;

public interface RiskMonitorLogRepository {

    RiskMonitorLog save(RiskMonitorLog log);

    Optional<RiskMonitorLog> findById(Long id);

    Optional<RiskMonitorLog> findByLogNo(String logNo);

    List<RiskMonitorLog> findByBizNo(String bizNo);

    PageResp<RiskMonitorLog> pageQuery(RiskQuery query);
}
