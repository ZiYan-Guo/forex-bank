package com.forex.schedule.infrastructure.repository;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.forex.common.base.dto.PageReq;
import com.forex.common.base.dto.PageResp;
import com.forex.schedule.application.query.JobQuery;
import com.forex.schedule.domain.model.aggregate.ScheduleJob;
import com.forex.schedule.domain.repository.ScheduleJobRepository;
import com.forex.schedule.infrastructure.mapper.ScheduleJobMapper;
import com.forex.schedule.infrastructure.persistence.ScheduleJobPO;

import lombok.RequiredArgsConstructor;

import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
@RequiredArgsConstructor
public class ScheduleJobRepositoryImpl implements ScheduleJobRepository {

    private final ScheduleJobMapper scheduleJobMapper;

    @Override
    public ScheduleJob save(ScheduleJob job) {
        ScheduleJobPO po = toPO(job);
        if (job.getId() == null) {
            scheduleJobMapper.insert(po);
        } else {
            scheduleJobMapper.updateById(po);
        }
        return toDomain(po);
    }

    @Override
    public Optional<ScheduleJob> findById(Long id) {
        ScheduleJobPO po = scheduleJobMapper.selectById(id);
        return Optional.ofNullable(po).map(this::toDomain);
    }

    @Override
    public Optional<ScheduleJob> findByJobHandler(String jobHandler) {
        ScheduleJobPO po = scheduleJobMapper.selectByJobHandler(jobHandler);
        return Optional.ofNullable(po).map(this::toDomain);
    }

    @Override
    public List<ScheduleJob> findAllEnabled() {
        return scheduleJobMapper.selectAllEnabled().stream()
                .map(this::toDomain)
                .toList();
    }

    @Override
    public PageResp<ScheduleJob> pageQuery(PageReq pageReq) {
        Page<ScheduleJobPO> page = new Page<>(pageReq.getPageNum(), pageReq.getPageSize());
        JobQuery query = new JobQuery();
        query.setPageNum(pageReq.getPageNum());
        query.setPageSize(pageReq.getPageSize());
        Page<ScheduleJobPO> result = scheduleJobMapper.pageQuery(page, query);
        List<ScheduleJob> records = result.getRecords().stream()
                .map(this::toDomain)
                .toList();
        return PageResp.of(result.getTotal(), records, pageReq.getPageNum(), pageReq.getPageSize());
    }

    private ScheduleJob toDomain(ScheduleJobPO po) {
        return ScheduleJob.reconstitute(
                po.getId(),
                po.getJobName(),
                po.getJobGroup(),
                po.getJobHandler(),
                po.getCronExpression(),
                po.getJobDesc(),
                po.getStatus(),
                po.getLastResult(),
                po.getLastExecuteTime(),
                po.getNextExecuteTime()
        );
    }

    private ScheduleJobPO toPO(ScheduleJob job) {
        ScheduleJobPO po = new ScheduleJobPO();
        po.setId(job.getId());
        po.setJobName(job.getJobName());
        po.setJobGroup(job.getJobGroup());
        po.setJobHandler(job.getJobHandler());
        po.setCronExpression(job.getCronExpression());
        po.setJobDesc(job.getJobDesc());
        po.setStatus(job.getStatus());
        po.setLastResult(job.getLastResult());
        po.setLastExecuteTime(job.getLastExecuteTime());
        po.setNextExecuteTime(job.getNextExecuteTime());
        return po;
    }
}
