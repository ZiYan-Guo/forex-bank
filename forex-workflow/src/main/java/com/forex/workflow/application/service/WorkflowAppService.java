package com.forex.workflow.application.service;

import com.forex.common.base.dto.PageResp;
import com.forex.workflow.application.command.StartProcessCmd;
import com.forex.workflow.application.query.WorkflowQuery;
import com.forex.workflow.domain.model.aggregate.WorkflowTask;
import com.forex.workflow.domain.repository.WorkflowTaskRepository;
import com.forex.workflow.domain.service.WorkflowDomainService;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;

import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Map;

@Service
@RequiredArgsConstructor
public class WorkflowAppService {

    private final WorkflowDomainService workflowDomainService;
    private final WorkflowTaskRepository workflowTaskRepository;
    private static final ObjectMapper objectMapper = new ObjectMapper();

    @Transactional
    public WorkflowTask startProcess(StartProcessCmd cmd) {
        Map<String, Object> variables = parseVariables(cmd.getVariables());
        return workflowDomainService.startProcess(
                cmd.getBizType(), cmd.getBizNo(), cmd.getTitle(),
                cmd.getAssignee(), cmd.getAssigneeName(), variables);
    }

    @Transactional
    public WorkflowTask completeTask(String taskId, String approveResult, String comment) {
        return workflowDomainService.completeTask(taskId, approveResult, comment);
    }

    public WorkflowTask getTaskDetail(String taskId) {
        return workflowDomainService.getTask(taskId);
    }

    public PageResp<WorkflowTask> pageQuery(WorkflowQuery query) {
        return workflowTaskRepository.pageQuery(query);
    }

    public PageResp<WorkflowTask> getMyTasks(String assignee) {
        WorkflowQuery query = new WorkflowQuery();
        query.setAssignee(assignee);
        query.setPageNum(1);
        query.setPageSize(20);
        return workflowTaskRepository.pageQuery(query);
    }

    @SneakyThrows
    private Map<String, Object> parseVariables(String variablesJson) {
        if (variablesJson == null || variablesJson.isBlank()) {
            return null;
        }
        return objectMapper.readValue(variablesJson, new TypeReference<Map<String, Object>>() {});
    }
}
