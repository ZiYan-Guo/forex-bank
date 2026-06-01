package com.forex.schedule.adapter.dto;

import io.swagger.v3.oas.annotations.media.Schema;

import lombok.Data;

import java.time.LocalDateTime;

@Data
@Schema(description = "定时任务响应")
public class ScheduleJobResp {

    @Schema(description = "主键ID")
    private Long id;

    @Schema(description = "任务名称")
    private String jobName;

    @Schema(description = "任务组")
    private String jobGroup;

    @Schema(description = "任务处理器")
    private String jobHandler;

    @Schema(description = "Cron表达式")
    private String cronExpression;

    @Schema(description = "任务描述")
    private String jobDesc;

    @Schema(description = "状态: ENABLED/DISABLED")
    private String status;

    @Schema(description = "最近执行结果")
    private String lastResult;

    @Schema(description = "上次执行时间")
    private LocalDateTime lastExecuteTime;

    @Schema(description = "下次执行时间")
    private LocalDateTime nextExecuteTime;
}
