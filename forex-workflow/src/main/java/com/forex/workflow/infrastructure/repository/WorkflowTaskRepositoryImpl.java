package com.forex.workflow.infrastructure.repository;

import com.forex.common.base.dto.PageResp;
import com.forex.workflow.application.query.WorkflowQuery;
import com.forex.workflow.domain.model.aggregate.WorkflowTask;
import com.forex.workflow.domain.repository.WorkflowTaskRepository;
import com.forex.workflow.infrastructure.mapper.WorkflowTaskMapper;
import com.forex.workflow.infrastructure.persistence.WorkflowTaskPO;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;

import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;

import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Map;
import java.util.Optional;

@Repository
@RequiredArgsConstructor
public class WorkflowTaskRepositoryImpl implements WorkflowTaskRepository {

    private final WorkflowTaskMapper workflowTaskMapper;
    private static final ObjectMapper objectMapper = new ObjectMapper();

    @Override
    public WorkflowTask save(WorkflowTask task) {
        WorkflowTaskPO po = toPO(task);
        if (task.getId() == null) {
            workflowTaskMapper.insert(po);
        } else {
            workflowTaskMapper.updateById(po);
        }
        return toDomain(po);
    }

    @Override
    public Optional<WorkflowTask> findById(Long id) {
        WorkflowTaskPO po = workflowTaskMapper.selectById(id);
        return Optional.ofNullable(po).map(this::toDomain);
    }

    @Override
    public Optional<WorkflowTask> findByTaskId(String taskId) {
        WorkflowTaskPO po = workflowTaskMapper.selectByTaskId(taskId);
        return Optional.ofNullable(po).map(this::toDomain);
    }

    @Override
    public Optional<WorkflowTask> findByBizNo(String bizNo) {
        WorkflowTaskPO po = workflowTaskMapper.selectByBizNo(bizNo);
        return Optional.ofNullable(po).map(this::toDomain);
    }

    @Override
    public PageResp<WorkflowTask> pageQuery(WorkflowQuery query) {
        Page<WorkflowTaskPO> page = new Page<>(query.getPageNum(), query.getPageSize());
        Page<WorkflowTaskPO> result = workflowTaskMapper.pageQuery(page, query);
        List<WorkflowTask> records = result.getRecords().stream()
                .map(this::toDomain)
                .toList();
        return PageResp.of(result.getTotal(), records, query.getPageNum(), query.getPageSize());
    }

    @SneakyThrows
    private WorkflowTask toDomain(WorkflowTaskPO po) {
        Map<String, Object> variables = null;
        if (po.getVariablesJson() != null && !po.getVariablesJson().isBlank()) {
            variables = objectMapper.readValue(po.getVariablesJson(),
                    new TypeReference<Map<String, Object>>() {});
        }
        return WorkflowTask.reconstitute(
                po.getId(),
                po.getTaskId(),
                po.getBizType(),
                po.getBizNo(),
                po.getTitle(),
                po.getAssignee(),
                po.getAssigneeName(),
                po.getProcessDefinitionKey(),
                po.getStatus(),
                po.getCreateTime(),
                po.getCompleteTime(),
                po.getComment(),
                variables
        );
    }

    @SneakyThrows
    private WorkflowTaskPO toPO(WorkflowTask task) {
        WorkflowTaskPO po = new WorkflowTaskPO();
        po.setId(task.getId());
        po.setTaskId(task.getTaskId());
        po.setBizType(task.getBizType());
        po.setBizNo(task.getBizNo());
        po.setTitle(task.getTitle());
        po.setAssignee(task.getAssignee());
        po.setAssigneeName(task.getAssigneeName());
        po.setProcessDefinitionKey(task.getProcessDefinitionKey());
        po.setStatus(task.getStatus());
        po.setCreateTime(task.getCreateTime());
        po.setCompleteTime(task.getCompleteTime());
        po.setComment(task.getComment());
        if (task.getVariables() != null) {
            po.setVariablesJson(objectMapper.writeValueAsString(task.getVariables()));
        }
        return po;
    }
}
