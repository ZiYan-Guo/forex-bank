package com.forex.workflow.domain.repository;

import com.forex.common.base.dto.PageResp;
import com.forex.workflow.application.query.WorkflowQuery;
import com.forex.workflow.domain.model.aggregate.WorkflowTask;

import java.util.Optional;

public interface WorkflowTaskRepository {

    WorkflowTask save(WorkflowTask task);

    Optional<WorkflowTask> findById(Long id);

    Optional<WorkflowTask> findByTaskId(String taskId);

    Optional<WorkflowTask> findByBizNo(String bizNo);

    PageResp<WorkflowTask> pageQuery(WorkflowQuery query);
}
