package com.forex.schedule.domain.service;

import com.forex.schedule.domain.model.aggregate.JobLog;
import com.forex.schedule.domain.model.aggregate.ScheduleJob;
import com.forex.schedule.domain.repository.JobLogRepository;
import com.forex.schedule.domain.repository.ScheduleJobRepository;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import org.springframework.stereotype.Service;

import java.time.LocalDate;

@Slf4j
@Service
@RequiredArgsConstructor
public class ScheduleDomainService {

    private final ScheduleJobRepository scheduleJobRepository;
    private final JobLogRepository jobLogRepository;

    public JobLog triggerJob(String jobHandler) {
        ScheduleJob job = scheduleJobRepository.findByJobHandler(jobHandler)
                .orElseThrow(() -> new IllegalArgumentException("任务不存在: " + jobHandler));

        if (!"ENABLED".equals(job.getStatus())) {
            throw new IllegalStateException("任务已禁用: " + jobHandler);
        }

        JobLog jobLog = JobLog.create(job.getId(), job.getJobName(), job.getJobHandler());
        jobLogRepository.save(jobLog);

        try {
            String result = simulateExecution(job);
            jobLog.markSuccess(result);
            job.recordExecution(result);
            jobLogRepository.save(jobLog);
            scheduleJobRepository.save(job);
            log.info("任务执行成功: handler={}, result={}", jobHandler, result);
        } catch (Exception e) {
            jobLog.markFailed(e.getMessage());
            job.recordExecution("FAILED");
            jobLogRepository.save(jobLog);
            scheduleJobRepository.save(job);
            log.error("任务执行失败: handler={}", jobHandler, e);
        }

        return jobLog;
    }

    public void executeDailyClosing(LocalDate date) {
        log.info("执行日终结算: date={}", date);
    }

    public void executeReconciliation() {
        log.info("执行对账任务");
    }

    private String simulateExecution(ScheduleJob job) {
        log.info("模拟执行任务: handler={}", job.getJobHandler());
        return "{\"status\":\"success\",\"handler\":\"" + job.getJobHandler() + "\",\"time\":\"" + java.time.LocalDateTime.now() + "\"}";
    }
}
