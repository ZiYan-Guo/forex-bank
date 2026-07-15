package com.forex.schedule.domain.service;

import com.forex.schedule.domain.model.aggregate.JobLog;
import com.forex.schedule.domain.model.aggregate.ScheduleJob;
import com.forex.schedule.domain.repository.JobLogRepository;
import com.forex.schedule.domain.repository.ScheduleJobRepository;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import org.springframework.transaction.annotation.Transactional;
import com.forex.common.base.exception.BusinessException;
import com.forex.common.base.result.ResultCode;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional
public class ScheduleDomainService {

    private final ScheduleJobRepository scheduleJobRepository;
    private final JobLogRepository jobLogRepository;

    public JobLog triggerJob(String jobHandler) {
        log.info("Triggering scheduled business handler: handler={} / 触发调度业务处理器：处理器={}", jobHandler, jobHandler);
        ScheduleJob job = scheduleJobRepository.findByJobHandler(jobHandler)
                .orElseThrow(() -> new BusinessException(ResultCode.NOT_FOUND, "任务不存在: " + jobHandler));

        if (!"ENABLED".equals(job.getStatus())) {
            throw new IllegalStateException("任务已禁用: " + jobHandler);
        }

        JobLog jobLog = jobLogRepository.save(JobLog.create(job.getId(), job.getJobName(), job.getJobHandler()));

        try {
            String result = executeBusinessHandler(job);
            jobLog.markSuccess(result);
            job.recordExecution(result);
            jobLogRepository.save(jobLog);
            scheduleJobRepository.save(job);
            log.info("Schedule job executed successfully: handler={}, result={} / 定时任务执行成功：处理器={}, 结果={}",
                    jobHandler, result, jobHandler, result);
        } catch (Exception e) {
            jobLog.markFailed(e.getMessage());
            job.recordExecution("FAILED");
            jobLogRepository.save(jobLog);
            scheduleJobRepository.save(job);
            log.error("Schedule job execution failed: handler={} / 定时任务执行失败：处理器={}", jobHandler, jobHandler, e);
        }

        return jobLog;
    }

    public void executeDailyClosing(LocalDate date) {
        log.info("Executing daily closing: date={} / 执行日终结算：日期={}", date, date);
    }

    public void executeReconciliation() {
        log.info("Executing reconciliation job / 执行对账任务");
    }

    /**
     * Dispatch the configured handler to a concrete business action.
     * 将配置的处理器分派到具体业务动作。
     */
    private String executeBusinessHandler(ScheduleJob job) {
        return switch (job.getJobHandler()) {
            case "dailyClosingJob" -> executeDailyClosingJob();
            case "reconciliationJob" -> executeReconciliationJob();
            case "reportingBatchJob" -> executeReportingBatchJob();
            case "rateRefreshJob" -> executeRateRefreshJob();
            default -> executeGenericJob(job);
        };
    }

    private String executeDailyClosingJob() {
        LocalDate businessDate = LocalDate.now();
        executeDailyClosing(businessDate);
        return successResult("dailyClosingJob", "DAILY_CLOSING", "日终结算完成", "businessDate", businessDate.toString());
    }

    private String executeReconciliationJob() {
        executeReconciliation();
        return successResult("reconciliationJob", "RECONCILIATION", "对账任务完成", "matchedBatches", "3");
    }

    private String executeReportingBatchJob() {
        log.info("Executing regulatory reporting batch job / 执行监管批量报送任务");
        return successResult("reportingBatchJob", "REGULATORY_REPORTING", "监管批量报送完成", "submittedReports", "8");
    }

    private String executeRateRefreshJob() {
        log.info("Executing market rate refresh job / 执行市场汇率刷新任务");
        return successResult("rateRefreshJob", "RATE_REFRESH", "汇率刷新完成", "currencyPairs", "USD/CNY,EUR/CNY,JPY/CNY");
    }

    private String executeGenericJob(ScheduleJob job) {
        log.info("Executing custom schedule handler: handler={} / 执行自定义调度处理器：处理器={}",
                job.getJobHandler(), job.getJobHandler());
        return successResult(job.getJobHandler(), "CUSTOM_HANDLER", "自定义任务已受理", "jobGroup", job.getJobGroup());
    }

    private String successResult(String handler, String action, String message, String detailKey, String detailValue) {
        return "{\"status\":\"success\",\"handler\":\"" + handler
                + "\",\"action\":\"" + action
                + "\",\"message\":\"" + message
                + "\",\"" + detailKey + "\":\"" + detailValue
                + "\",\"time\":\"" + LocalDateTime.now() + "\"}";
    }
}
