package com.forex.schedule.application.service;

import com.forex.common.base.dto.PageResp;
import com.forex.schedule.application.command.JobCmd;
import com.forex.schedule.application.query.JobQuery;
import com.forex.schedule.domain.model.aggregate.JobLog;
import com.forex.schedule.domain.model.aggregate.ScheduleJob;
import com.forex.schedule.domain.repository.JobLogRepository;
import com.forex.schedule.domain.repository.ScheduleJobRepository;
import com.forex.schedule.domain.service.ScheduleDomainService;

import lombok.RequiredArgsConstructor;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class ScheduleAppService {

    private final ScheduleDomainService scheduleDomainService;
    private final ScheduleJobRepository scheduleJobRepository;
    private final JobLogRepository jobLogRepository;

    @Transactional
    public ScheduleJob addJob(JobCmd cmd) {
        ScheduleJob job = ScheduleJob.create(
                cmd.getJobName(),
                cmd.getJobGroup(),
                cmd.getJobHandler(),
                cmd.getCronExpression(),
                cmd.getJobDesc()
        );
        return scheduleJobRepository.save(job);
    }

    @Transactional
    public ScheduleJob updateJob(Long id, JobCmd cmd) {
        ScheduleJob job = scheduleJobRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("任务不存在: " + id));

        ScheduleJob updated = ScheduleJob.create(
                cmd.getJobName(),
                cmd.getJobGroup(),
                cmd.getJobHandler(),
                cmd.getCronExpression(),
                cmd.getJobDesc()
        );
        return scheduleJobRepository.save(updated);
    }

    @Transactional
    public void toggleJob(Long id) {
        ScheduleJob job = scheduleJobRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("任务不存在: " + id));

        if ("ENABLED".equals(job.getStatus())) {
            job.disable();
        } else {
            job.enable();
        }
        scheduleJobRepository.save(job);
    }

    public void triggerJob(Long id) {
        ScheduleJob job = scheduleJobRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("任务不存在: " + id));
        scheduleDomainService.triggerJob(job.getJobHandler());
    }

    public ScheduleJob getJob(Long id) {
        return scheduleJobRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("任务不存在: " + id));
    }

    public PageResp<ScheduleJob> pageQuery(JobQuery query) {
        return scheduleJobRepository.pageQuery(query);
    }

    public List<JobLog> getJobLogs(Long jobId) {
        return jobLogRepository.findByJobId(jobId);
    }
}
