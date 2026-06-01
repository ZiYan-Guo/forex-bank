package com.forex.workflow.adapter.dto;

import io.swagger.v3.oas.annotations.media.Schema;

import lombok.Data;

import java.time.LocalDateTime;
import java.util.Map;

@Data
public class WorkflowTaskResp {

    @Schema(description = "主键ID")
    private Long id;

    @Schema(description = "任务ID")
    private String taskId;

    @Schema(description = "业务类型")
    private String bizType;

    @Schema(description = "业务编号")
    private String bizNo;

    @Schema(description = "任务标题")
    private String title;

    @Schema(description = "受理人")
    private String assignee;

    @Schema(description = "受理人名称")
    private String assigneeName;

    @Schema(description = "流程定义Key")
    private String processDefinitionKey;

    @Schema(description = "状态")
    private String status;

    @Schema(description = "创建时间")
    private LocalDateTime createTime;

    @Schema(description = "完成时间")
    private LocalDateTime completeTime;

    @Schema(description = "备注")
    private String comment;

    @Schema(description = "流程变量")
    private Map<String, Object> variables;
}
