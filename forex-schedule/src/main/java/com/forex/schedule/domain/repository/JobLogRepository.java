package com.forex.schedule.domain.repository;

import com.forex.common.base.dto.PageReq;
import com.forex.common.base.dto.PageResp;
import com.forex.schedule.domain.model.aggregate.JobLog;

import java.util.List;
import java.util.Optional;

public interface JobLogRepository {

    JobLog save(JobLog log);

    Optional<JobLog> findById(Long id);

    List<JobLog> findByJobId(Long jobId);

    PageResp<JobLog> pageQuery(PageReq pageReq);
}
