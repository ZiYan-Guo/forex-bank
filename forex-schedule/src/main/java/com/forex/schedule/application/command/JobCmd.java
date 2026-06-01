package com.forex.schedule.application.command;

import jakarta.validation.constraints.NotBlank;

import lombok.Data;

@Data
public class JobCmd {

    @NotBlank(message = "任务名称不能为空")
    private String jobName;

    @NotBlank(message = "任务组不能为空")
    private String jobGroup;

    @NotBlank(message = "任务处理器不能为空")
    private String jobHandler;

    @NotBlank(message = "Cron表达式不能为空")
    private String cronExpression;

    private String jobDesc;
}
