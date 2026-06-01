package com.forex.schedule.infrastructure.repository;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.forex.common.base.dto.PageReq;
import com.forex.common.base.dto.PageResp;
import com.forex.schedule.domain.model.aggregate.JobLog;
import com.forex.schedule.domain.repository.JobLogRepository;
import com.forex.schedule.infrastructure.mapper.JobLogMapper;
import com.forex.schedule.infrastructure.persistence.JobLogPO;

import lombok.RequiredArgsConstructor;

import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
@RequiredArgsConstructor
public class JobLogRepositoryImpl implements JobLogRepository {

    private final JobLogMapper jobLogMapper;

    @Override
    public JobLog save(JobLog log) {
        JobLogPO po = toPO(log);
        if (log.getId() == null) {
            jobLogMapper.insert(po);
        } else {
            jobLogMapper.updateById(po);
        }
        return toDomain(po);
    }

    @Override
    public Optional<JobLog> findById(Long id) {
        JobLogPO po = jobLogMapper.selectById(id);
        return Optional.ofNullable(po).map(this::toDomain);
    }

    @Override
    public List<JobLog> findByJobId(Long jobId) {
        return jobLogMapper.selectByJobId(jobId).stream()
                .map(this::toDomain)
                .toList();
    }

    @Override
    public PageResp<JobLog> pageQuery(PageReq pageReq) {
        Page<JobLogPO> page = new Page<>(pageReq.getPageNum(), pageReq.getPageSize());
        Page<JobLogPO> result = jobLogMapper.pageQuery(page);
        List<JobLog> records = result.getRecords().stream()
                .map(this::toDomain)
                .toList();
        return PageResp.of(result.getTotal(), records, pageReq.getPageNum(), pageReq.getPageSize());
    }

    private JobLog toDomain(JobLogPO po) {
        return JobLog.reconstitute(
                po.getId(),
                po.getJobId(),
                po.getJobName(),
                po.getJobHandler(),
                po.getStartTime(),
                po.getEndTime(),
                po.getExecuteStatus(),
                po.getExecuteResult(),
                po.getErrorMsg()
        );
    }

    private JobLogPO toPO(JobLog log) {
        JobLogPO po = new JobLogPO();
        po.setId(log.getId());
        po.setJobId(log.getJobId());
        po.setJobName(log.getJobName());
        po.setJobHandler(log.getJobHandler());
        po.setStartTime(log.getStartTime());
        po.setEndTime(log.getEndTime());
        po.setExecuteStatus(log.getExecuteStatus());
        po.setExecuteResult(log.getExecuteResult());
        po.setErrorMsg(log.getErrorMsg());
        return po;
    }
}
