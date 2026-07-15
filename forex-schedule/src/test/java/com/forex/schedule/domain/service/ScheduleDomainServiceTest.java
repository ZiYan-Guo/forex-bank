package com.forex.schedule.domain.service;

import com.forex.common.base.dto.PageReq;
import com.forex.common.base.dto.PageResp;
import com.forex.common.base.exception.BusinessException;
import com.forex.schedule.domain.model.aggregate.JobLog;
import com.forex.schedule.domain.model.aggregate.ScheduleJob;
import com.forex.schedule.domain.repository.JobLogRepository;
import com.forex.schedule.domain.repository.ScheduleJobRepository;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

class ScheduleDomainServiceTest {

    private StubScheduleJobRepository scheduleJobRepository;
    private StubJobLogRepository jobLogRepository;
    private ScheduleDomainService scheduleDomainService;

    @BeforeEach
    void setUp() {
        scheduleJobRepository = new StubScheduleJobRepository();
        jobLogRepository = new StubJobLogRepository();
        scheduleDomainService = new ScheduleDomainService(scheduleJobRepository, jobLogRepository);
    }

    @Test
    @DisplayName("Trigger job dispatches business handler and marks success")
    void testTriggerJob_Success() {
        ScheduleJob job = ScheduleJob.reconstitute(
                1L, "DailyClose", "DAILY", "dailyClosingJob", "0 0 18 * * ?",
                "Daily close", "ENABLED", null, null, null);
        scheduleJobRepository.jobs = List.of(job);

        JobLog result = scheduleDomainService.triggerJob("dailyClosingJob");

        assertNotNull(result);
        assertEquals("SUCCESS", result.getExecuteStatus());
        assertTrue(result.getExecuteResult().contains("DAILY_CLOSING"));
        assertEquals(1, jobLogRepository.logs.size(), "running log is updated to completed state");
        assertTrue(scheduleJobRepository.savedJob.getLastResult().contains("DAILY_CLOSING"));
    }

    @Test
    @DisplayName("Trigger disabled job throws")
    void testTriggerJob_Disabled() {
        ScheduleJob job = ScheduleJob.reconstitute(
                2L, "Disabled", "SYSTEM", "disabledHandler", "0 0 1 * * ?",
                "Disabled job", "DISABLED", null, null, null);
        scheduleJobRepository.jobs = List.of(job);

        assertThrows(IllegalStateException.class,
                () -> scheduleDomainService.triggerJob("disabledHandler"));
    }

    @Test
    @DisplayName("Trigger non-existent job throws")
    void testTriggerJob_NotFound() {
        scheduleJobRepository.jobs = List.of();

        assertThrows(BusinessException.class,
                () -> scheduleDomainService.triggerJob("unknown"));
    }

    @Test
    @DisplayName("Execute daily closing logs")
    void testExecuteDailyClosing() {
        scheduleDomainService.executeDailyClosing(LocalDate.now());
    }

    @Test
    @DisplayName("Execute reconciliation logs")
    void testExecuteReconciliation() {
        scheduleDomainService.executeReconciliation();
    }

    /**
     * In-memory job repository for deterministic domain tests.
     * 用于稳定领域测试的内存任务仓储。
     */
    private static class StubScheduleJobRepository implements ScheduleJobRepository {

        private List<ScheduleJob> jobs = List.of();
        private ScheduleJob savedJob;

        @Override
        public ScheduleJob save(ScheduleJob job) {
            savedJob = job;
            return job;
        }

        @Override
        public Optional<ScheduleJob> findById(Long id) {
            return jobs.stream().filter(job -> job.getId().equals(id)).findFirst();
        }

        @Override
        public Optional<ScheduleJob> findByJobHandler(String jobHandler) {
            return jobs.stream().filter(job -> job.getJobHandler().equals(jobHandler)).findFirst();
        }

        @Override
        public List<ScheduleJob> findAllEnabled() {
            return jobs.stream().filter(job -> "ENABLED".equals(job.getStatus())).toList();
        }

        @Override
        public PageResp<ScheduleJob> pageQuery(PageReq pageReq) {
            return PageResp.of(jobs.size(), jobs, pageReq.getPageNum(), pageReq.getPageSize());
        }
    }

    /**
     * In-memory log repository that behaves like insert/update by ID.
     * 按 ID 模拟新增/更新行为的内存日志仓储。
     */
    private static class StubJobLogRepository implements JobLogRepository {

        private final List<JobLog> logs = new ArrayList<>();
        private long nextId = 1L;

        @Override
        public JobLog save(JobLog log) {
            JobLog persisted = log.getId() == null
                    ? JobLog.reconstitute(nextId++, log.getJobId(), log.getJobName(), log.getJobHandler(),
                    log.getStartTime(), log.getEndTime(), log.getExecuteStatus(), log.getExecuteResult(), log.getErrorMsg())
                    : log;
            if (persisted.getId() != null) {
                logs.removeIf(existing -> persisted.getId().equals(existing.getId()));
            }
            logs.add(persisted);
            return persisted;
        }

        @Override
        public Optional<JobLog> findById(Long id) {
            return logs.stream().filter(log -> log.getId().equals(id)).findFirst();
        }

        @Override
        public List<JobLog> findByJobId(Long jobId) {
            return logs.stream().filter(log -> jobId.equals(log.getJobId())).toList();
        }

        @Override
        public PageResp<JobLog> pageQuery(PageReq pageReq) {
            return PageResp.of(logs.size(), logs, pageReq.getPageNum(), pageReq.getPageSize());
        }
    }
}
