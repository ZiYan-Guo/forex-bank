package com.forex.schedule.application.command;

import jakarta.validation.constraints.NotBlank;

import io.swagger.v3.oas.annotations.media.Schema;

import lombok.Data;

@Data
@Schema(description = "Schedule job command / 定时任务命令")
public class JobCmd {

    @Schema(description = "Job name / 任务名称")
    @NotBlank(message = "任务名称不能为空")
    private String jobName;

    @Schema(description = "Job group / 任务分组")
    @NotBlank(message = "任务组不能为空")
    private String jobGroup;

    @Schema(description = "Job handler / 任务处理器")
    @NotBlank(message = "任务处理器不能为空")
    private String jobHandler;

    @Schema(description = "Cron expression / Cron表达式")
    @NotBlank(message = "Cron表达式不能为空")
    private String cronExpression;

    @Schema(description = "Job description / 任务描述")
    private String jobDesc;
}
