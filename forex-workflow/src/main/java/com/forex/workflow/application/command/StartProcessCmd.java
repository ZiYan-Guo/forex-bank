package com.forex.workflow.application.command;

import jakarta.validation.constraints.NotBlank;

import lombok.Data;

@Data
public class StartProcessCmd {

    @NotBlank(message = "业务类型不能为空")
    private String bizType;

    @NotBlank(message = "业务编号不能为空")
    private String bizNo;

    @NotBlank(message = "任务标题不能为空")
    private String title;

    private String assignee;

    private String assigneeName;

    private String variables;
}
