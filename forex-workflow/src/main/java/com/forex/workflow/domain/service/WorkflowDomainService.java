package com.forex.workflow.domain.service;

import com.forex.workflow.domain.model.aggregate.WorkflowTask;
import com.forex.workflow.domain.model.entity.ApprovalRecord;
import com.forex.workflow.domain.repository.WorkflowTaskRepository;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import org.springframework.transaction.annotation.Transactional;
import com.forex.common.base.exception.BusinessException;
import com.forex.common.base.result.ResultCode;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional
public class WorkflowDomainService {

    private final WorkflowTaskRepository workflowTaskRepository;

    public WorkflowTask startProcess(String bizType, String bizNo, String title,
                                      String assignee, String assigneeName,
                                      Map<String, Object> variables) {
        String taskId = generateTaskId();

        WorkflowTask task = WorkflowTask.create(
                taskId, bizType, bizNo, title, assignee, assigneeName,
                bizType, variables);

        WorkflowTask saved = workflowTaskRepository.save(task);
        log.info("工作流任务创建成功: taskId={}, bizType={}, bizNo={}", taskId, bizType, bizNo);
        return saved;
    }

    public WorkflowTask completeTask(String taskId, String approveResult, String comment) {
        WorkflowTask task = workflowTaskRepository.findByTaskId(taskId)
                .orElseThrow(() -> new BusinessException(ResultCode.NOT_FOUND, "任务不存在: " + taskId));

        if ("APPROVED".equals(approveResult)) {
            task.approve(comment);
        } else if ("REJECTED".equals(approveResult)) {
            task.reject(comment);
        } else {
            throw new BusinessException(ResultCode.VALIDATE_FAIL, "不支持的审批结果: " + approveResult);
        }

        WorkflowTask saved = workflowTaskRepository.save(task);
        log.info("工作流任务处理完成: taskId={}, result={}", taskId, approveResult);
        return saved;
    }

    public WorkflowTask getTask(String taskId) {
        return workflowTaskRepository.findByTaskId(taskId)
                .orElseThrow(() -> new BusinessException(ResultCode.NOT_FOUND, "任务不存在: " + taskId));
    }

    public List<WorkflowTask> getTasksByAssignee(String assignee) {
        return List.of();
    }

    private String generateTaskId() {
        return "WF" + UUID.randomUUID().toString().replace("-", "").substring(0, 24).toUpperCase();
    }
}
