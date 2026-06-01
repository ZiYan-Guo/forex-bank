package com.forex.workflow.domain.model.aggregate;

import com.forex.common.base.domain.BaseAggregate;

import lombok.Getter;

import java.time.LocalDateTime;
import java.util.Map;

@Getter
public class WorkflowTask extends BaseAggregate {

    private Long id;
    private String taskId;
    private String bizType;
    private String bizNo;
    private String title;
    private String assignee;
    private String assigneeName;
    private String processDefinitionKey;
    private String status;
    private LocalDateTime createTime;
    private LocalDateTime completeTime;
    private String comment;
    private Map<String, Object> variables;

    private WorkflowTask() {
        super();
    }

    public static WorkflowTask create(String taskId, String bizType, String bizNo, String title,
                                       String assignee, String assigneeName, String processDefinitionKey,
                                       Map<String, Object> variables) {
        WorkflowTask task = new WorkflowTask();
        task.taskId = taskId;
        task.bizType = bizType;
        task.bizNo = bizNo;
        task.title = title;
        task.assignee = assignee;
        task.assigneeName = assigneeName;
        task.processDefinitionKey = processDefinitionKey;
        task.status = "PENDING";
        task.createTime = LocalDateTime.now();
        task.variables = variables;
        task.validate();
        return task;
    }

    public static WorkflowTask reconstitute(Long id, String taskId, String bizType, String bizNo,
                                             String title, String assignee, String assigneeName,
                                             String processDefinitionKey, String status,
                                             LocalDateTime createTime, LocalDateTime completeTime,
                                             String comment, Map<String, Object> variables) {
        WorkflowTask task = new WorkflowTask();
        task.id = id;
        task.taskId = taskId;
        task.bizType = bizType;
        task.bizNo = bizNo;
        task.title = title;
        task.assignee = assignee;
        task.assigneeName = assigneeName;
        task.processDefinitionKey = processDefinitionKey;
        task.status = status;
        task.createTime = createTime;
        task.completeTime = completeTime;
        task.comment = comment;
        task.variables = variables;
        return task;
    }

    public void start() {
        if (!"PENDING".equals(this.status)) {
            throw new IllegalStateException("只有待办状态的任务才能启动");
        }
        this.status = "PROCESSING";
        markUpdated();
    }

    public void approve(String comment) {
        if (!"PROCESSING".equals(this.status)) {
            throw new IllegalStateException("只有处理中的任务才能审批通过");
        }
        this.status = "APPROVED";
        this.comment = comment;
        this.completeTime = LocalDateTime.now();
        markUpdated();
    }

    public void reject(String comment) {
        if (!"PROCESSING".equals(this.status)) {
            throw new IllegalStateException("只有处理中的任务才能审批拒绝");
        }
        this.status = "REJECTED";
        this.comment = comment;
        this.completeTime = LocalDateTime.now();
        markUpdated();
    }

    public void cancel() {
        if ("APPROVED".equals(this.status) || "REJECTED".equals(this.status)) {
            throw new IllegalStateException("已完成的任务不能取消");
        }
        this.status = "CANCELLED";
        this.completeTime = LocalDateTime.now();
        markUpdated();
    }

    public boolean isCompleted() {
        return "APPROVED".equals(this.status) || "REJECTED".equals(this.status)
                || "CANCELLED".equals(this.status);
    }

    @Override
    protected void validate() {
        if (taskId == null || taskId.isBlank()) {
            throw new IllegalArgumentException("任务ID不能为空");
        }
        if (bizType == null || bizType.isBlank()) {
            throw new IllegalArgumentException("业务类型不能为空");
        }
        if (bizNo == null || bizNo.isBlank()) {
            throw new IllegalArgumentException("业务编号不能为空");
        }
        if (title == null || title.isBlank()) {
            throw new IllegalArgumentException("任务标题不能为空");
        }
    }
}
