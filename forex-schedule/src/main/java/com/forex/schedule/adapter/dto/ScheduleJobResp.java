package com.forex.schedule.adapter.dto;

import io.swagger.v3.oas.annotations.media.Schema;

import lombok.Data;

import java.time.LocalDateTime;

@Data
@Schema(description = "Schedule job response / 定时任务响应")
public class ScheduleJobResp {

    @Schema(description = "Primary key / 主键ID")
    private Long id;

    @Schema(description = "Job name / 任务名称")
    private String jobName;

    @Schema(description = "Job group / 任务组")
    private String jobGroup;

    @Schema(description = "Job handler / 任务处理器")
    private String jobHandler;

    @Schema(description = "Cron expression / Cron表达式")
    private String cronExpression;

    @Schema(description = "Job description / 任务描述")
    private String jobDesc;

    @Schema(description = "Status: ENABLED/DISABLED / 状态")
    private String status;

    @Schema(description = "Last execution result / 最近执行结果")
    private String lastResult;

    @Schema(description = "Last execution time / 上次执行时间")
    private LocalDateTime lastExecuteTime;

    @Schema(description = "Next execution time / 下次执行时间")
    private LocalDateTime nextExecuteTime;
}
