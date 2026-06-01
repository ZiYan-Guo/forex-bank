package com.forex.workflow.infrastructure.persistence;

import com.forex.common.mybatis.base.BasePO;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;

import lombok.Data;
import lombok.EqualsAndHashCode;

import java.time.LocalDateTime;

@Data
@EqualsAndHashCode(callSuper = true)
@TableName("t_workflow_task")
public class WorkflowTaskPO extends BasePO {

    private String taskId;
    private String bizType;
    private String bizNo;
    private String title;
    private String assignee;
    private String assigneeName;
    private String processDefinitionKey;
    private String status;
    private LocalDateTime completeTime;
    private String comment;

    @TableField("variables")
    private String variablesJson;
}
