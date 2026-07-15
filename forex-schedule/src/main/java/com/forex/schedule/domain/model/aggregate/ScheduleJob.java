package com.forex.schedule.domain.model.aggregate;

import com.forex.common.base.domain.BaseAggregate;

import lombok.Getter;

import java.time.LocalDateTime;
import com.forex.common.base.exception.BusinessException;
import com.forex.common.base.result.ResultCode;

@Getter
public class ScheduleJob extends BaseAggregate {

    private Long id;
    private String jobName;
    private String jobGroup;
    private String jobHandler;
    private String cronExpression;
    private String jobDesc;
    private String status;
    private String lastResult;
    private LocalDateTime lastExecuteTime;
    private LocalDateTime nextExecuteTime;

    private ScheduleJob() {
        super();
    }

    public static ScheduleJob create(String jobName, String jobGroup, String jobHandler,
                                      String cronExpression, String jobDesc) {
        ScheduleJob job = new ScheduleJob();
        job.jobName = jobName;
        job.jobGroup = jobGroup;
        job.jobHandler = jobHandler;
        job.cronExpression = cronExpression;
        job.jobDesc = jobDesc;
        job.status = "ENABLED";
        job.validate();
        return job;
    }

    public static ScheduleJob reconstitute(Long id, String jobName, String jobGroup,
                                            String jobHandler, String cronExpression, String jobDesc,
                                            String status, String lastResult, LocalDateTime lastExecuteTime,
                                            LocalDateTime nextExecuteTime) {
        ScheduleJob job = new ScheduleJob();
        job.id = id;
        job.jobName = jobName;
        job.jobGroup = jobGroup;
        job.jobHandler = jobHandler;
        job.cronExpression = cronExpression;
        job.jobDesc = jobDesc;
        job.status = status;
        job.lastResult = lastResult;
        job.lastExecuteTime = lastExecuteTime;
        job.nextExecuteTime = nextExecuteTime;
        return job;
    }

    public void enable() {
        this.status = "ENABLED";
        markUpdated();
    }

    public void disable() {
        this.status = "DISABLED";
        markUpdated();
    }

    /**
     * Update job definition while keeping runtime state.
     * 更新任务定义，同时保留运行状态和最近执行信息。
     */
    public void updateDefinition(String jobName, String jobGroup, String jobHandler,
                                 String cronExpression, String jobDesc) {
        this.jobName = jobName;
        this.jobGroup = jobGroup;
        this.jobHandler = jobHandler;
        this.cronExpression = cronExpression;
        this.jobDesc = jobDesc;
        validate();
        markUpdated();
    }

    public void recordExecution(String result) {
        this.lastResult = result;
        this.lastExecuteTime = LocalDateTime.now();
        markUpdated();
    }

    @Override
    protected void validate() {
        if (jobName == null || jobName.isBlank()) {
            throw new BusinessException(ResultCode.VALIDATE_FAIL, "任务名称不能为空");
        }
        if (jobHandler == null || jobHandler.isBlank()) {
            throw new BusinessException(ResultCode.VALIDATE_FAIL, "任务处理器不能为空");
        }
        if (cronExpression == null || cronExpression.isBlank()) {
            throw new BusinessException(ResultCode.VALIDATE_FAIL, "Cron表达式不能为空");
        }
    }
}
