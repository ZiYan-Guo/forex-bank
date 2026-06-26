package com.forex.schedule.domain.service;

import com.forex.schedule.domain.model.aggregate.JobLog;
import com.forex.schedule.domain.model.aggregate.ScheduleJob;
import com.forex.schedule.domain.repository.JobLogRepository;
import com.forex.schedule.domain.repository.ScheduleJobRepository;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import com.forex.common.base.exception.BusinessException;

@ExtendWith(MockitoExtension.class)
class ScheduleDomainServiceTest {

    @Mock private ScheduleJobRepository scheduleJobRepository;
    @Mock private JobLogRepository jobLogRepository;

    @InjectMocks
    private ScheduleDomainService scheduleDomainService;

    @Test
    @DisplayName("Trigger job marks success and saves")
    void testTriggerJob_Success() {
        ScheduleJob job = mock(ScheduleJob.class);
        when(job.getStatus()).thenReturn("ENABLED");
        when(job.getId()).thenReturn(1L);
        when(job.getJobName()).thenReturn("DailyClose");
        when(job.getJobHandler()).thenReturn("dailyCloseHandler");
        when(scheduleJobRepository.findByJobHandler("dailyCloseHandler"))
                .thenReturn(Optional.of(job));
        when(jobLogRepository.save(any())).thenAnswer(i -> i.getArgument(0));

        JobLog result = scheduleDomainService.triggerJob("dailyCloseHandler");

        assertNotNull(result);
        verify(jobLogRepository, atLeastOnce()).save(any());
        verify(job).recordExecution(anyString());
    }

    @Test
    @DisplayName("Trigger disabled job throws")
    void testTriggerJob_Disabled() {
        ScheduleJob job = mock(ScheduleJob.class);
        when(job.getStatus()).thenReturn("DISABLED");
        when(scheduleJobRepository.findByJobHandler("disabledHandler"))
                .thenReturn(Optional.of(job));

        assertThrows(IllegalStateException.class,
                () -> scheduleDomainService.triggerJob("disabledHandler"));
    }

    @Test
    @DisplayName("Trigger non-existent job throws")
    void testTriggerJob_NotFound() {
        when(scheduleJobRepository.findByJobHandler("unknown"))
                .thenReturn(Optional.empty());

        assertThrows(BusinessException.class,
                () -> scheduleDomainService.triggerJob("unknown"));
    }

    @Test
    @DisplayName("Execute daily closing logs")
    void testExecuteDailyClosing() {
        scheduleDomainService.executeDailyClosing(java.time.LocalDate.now());
    }

    @Test
    @DisplayName("Execute reconciliation logs")
    void testExecuteReconciliation() {
        scheduleDomainService.executeReconciliation();
    }
}
