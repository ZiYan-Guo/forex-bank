package com.forex.schedule.domain.repository;

import com.forex.common.base.dto.PageReq;
import com.forex.common.base.dto.PageResp;
import com.forex.schedule.domain.model.aggregate.ScheduleJob;

import java.util.List;
import java.util.Optional;

public interface ScheduleJobRepository {

    ScheduleJob save(ScheduleJob job);

    Optional<ScheduleJob> findById(Long id);

    Optional<ScheduleJob> findByJobHandler(String jobHandler);

    List<ScheduleJob> findAllEnabled();

    PageResp<ScheduleJob> pageQuery(PageReq pageReq);
}
