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
import com.forex.common.base.exception.BusinessException;
import com.forex.common.base.result.ResultCode;

import lombok.extern.slf4j.Slf4j;

@Service
@RequiredArgsConstructor
@Slf4j
public class ScheduleAppService {

    private final ScheduleDomainService scheduleDomainService;
    private final ScheduleJobRepository scheduleJobRepository;
    private final JobLogRepository jobLogRepository;

    @Transactional
    public ScheduleJob addJob(JobCmd cmd) {
        log.info("Adding schedule job: name={}, group={}, handler={} / 新增定时任务：名称={}, 分组={}, 处理器={}",
                cmd.getJobName(), cmd.getJobGroup(), cmd.getJobHandler(),
                cmd.getJobName(), cmd.getJobGroup(), cmd.getJobHandler());
        ScheduleJob job = ScheduleJob.create(
                cmd.getJobName(),
                cmd.getJobGroup(),
                cmd.getJobHandler(),
                cmd.getCronExpression(),
                cmd.getJobDesc()
        );
        ScheduleJob saved = scheduleJobRepository.save(job);
        log.info("Schedule job added: id={}, handler={} / 定时任务已新增：ID={}, 处理器={}",
                saved.getId(), saved.getJobHandler(), saved.getId(), saved.getJobHandler());
        return saved;
    }

    @Transactional
    public ScheduleJob updateJob(Long id, JobCmd cmd) {
        ScheduleJob job = scheduleJobRepository.findById(id)
                .orElseThrow(() -> new BusinessException(ResultCode.NOT_FOUND, "任务不存在: " + id));

        log.info("Updating schedule job: id={}, oldHandler={}, newHandler={} / 更新定时任务：ID={}, 原处理器={}, 新处理器={}",
                id, job.getJobHandler(), cmd.getJobHandler(), id, job.getJobHandler(), cmd.getJobHandler());
        job.updateDefinition(
                cmd.getJobName(),
                cmd.getJobGroup(),
                cmd.getJobHandler(),
                cmd.getCronExpression(),
                cmd.getJobDesc()
        );
        return scheduleJobRepository.save(job);
    }

    @Transactional
    public void toggleJob(Long id) {
        ScheduleJob job = scheduleJobRepository.findById(id)
                .orElseThrow(() -> new BusinessException(ResultCode.NOT_FOUND, "任务不存在: " + id));

        if ("ENABLED".equals(job.getStatus())) {
            job.disable();
        } else {
            job.enable();
        }
        scheduleJobRepository.save(job);
        log.info("Schedule job status toggled: id={}, status={} / 定时任务状态已切换：ID={}, 状态={}",
                id, job.getStatus(), id, job.getStatus());
    }

    public void triggerJob(Long id) {
        ScheduleJob job = scheduleJobRepository.findById(id)
                .orElseThrow(() -> new BusinessException(ResultCode.NOT_FOUND, "任务不存在: " + id));
        log.info("Triggering schedule job manually: id={}, handler={} / 手动触发定时任务：ID={}, 处理器={}",
                id, job.getJobHandler(), id, job.getJobHandler());
        scheduleDomainService.triggerJob(job.getJobHandler());
    }

    public ScheduleJob getJob(Long id) {
        return scheduleJobRepository.findById(id)
                .orElseThrow(() -> new BusinessException(ResultCode.NOT_FOUND, "任务不存在: " + id));
    }

    public PageResp<ScheduleJob> pageQuery(JobQuery query) {
        log.info("Querying schedule jobs: pageNum={}, pageSize={}, name={}, group={}, status={} / 查询定时任务：页码={}, 页大小={}, 名称={}, 分组={}, 状态={}",
                query.getPageNum(), query.getPageSize(), query.getJobName(), query.getJobGroup(), query.getStatus(),
                query.getPageNum(), query.getPageSize(), query.getJobName(), query.getJobGroup(), query.getStatus());
        return scheduleJobRepository.pageQuery(query);
    }

    public List<JobLog> getJobLogs(Long jobId) {
        log.info("Loading schedule job logs: jobId={} / 查询定时任务执行日志：任务ID={}", jobId, jobId);
        return jobLogRepository.findByJobId(jobId);
    }
}
