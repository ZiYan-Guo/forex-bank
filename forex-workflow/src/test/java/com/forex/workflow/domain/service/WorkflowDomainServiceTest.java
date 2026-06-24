package com.forex.workflow.domain.service;

import com.forex.workflow.domain.model.aggregate.WorkflowTask;
import com.forex.workflow.domain.repository.WorkflowTaskRepository;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Map;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class WorkflowDomainServiceTest {

    @Mock private WorkflowTaskRepository workflowTaskRepository;

    private WorkflowDomainService workflowDomainService;

    @BeforeEach
    void setUp() {
        workflowDomainService = new WorkflowDomainService(workflowTaskRepository);
    }

    @Test
    @DisplayName("Start process creates task with PENDING status")
    void testStartProcess() {
        when(workflowTaskRepository.save(any())).thenAnswer(i -> i.getArgument(0));

        WorkflowTask result = workflowDomainService.startProcess(
                "LOAN_APPROVAL", "LN001", "大额贷款审批",
                "user01", "张三", Map.of("amount", 500000));

        assertNotNull(result);
        assertTrue(result.getTaskId().startsWith("WF"));
        assertEquals("PENDING", result.getStatus());
        assertEquals("LOAN_APPROVAL", result.getBizType());
        verify(workflowTaskRepository).save(any());
    }

    @Test
    @DisplayName("Complete task with APPROVED status")
    void testCompleteTask_Approved() {
        WorkflowTask task = WorkflowTask.create("WF001", "LOAN_APPROVAL", "LN001",
                "审批任务", "user01", "张三", "LOAN_PROCESS", Map.of());
        task.start();
        when(workflowTaskRepository.findByTaskId("WF001")).thenReturn(Optional.of(task));
        when(workflowTaskRepository.save(any())).thenReturn(task);

        WorkflowTask result = workflowDomainService.completeTask("WF001", "APPROVED", "同意");

        assertEquals("APPROVED", result.getStatus());
        assertEquals("同意", result.getComment());
        assertNotNull(result.getCompleteTime());
    }

    @Test
    @DisplayName("Complete task with REJECTED status")
    void testCompleteTask_Rejected() {
        WorkflowTask task = WorkflowTask.create("WF002", "LOAN_APPROVAL", "LN002",
                "审批任务", "user01", "张三", "LOAN_PROCESS", Map.of());
        task.start();
        when(workflowTaskRepository.findByTaskId("WF002")).thenReturn(Optional.of(task));
        when(workflowTaskRepository.save(any())).thenReturn(task);

        WorkflowTask result = workflowDomainService.completeTask("WF002", "REJECTED", "不合规");

        assertEquals("REJECTED", result.getStatus());
    }

    @Test
    @DisplayName("Get task throws when not found")
    void testGetTask_NotFound() {
        when(workflowTaskRepository.findByTaskId("NONEXIST")).thenReturn(Optional.empty());
        assertThrows(IllegalArgumentException.class,
                () -> workflowDomainService.getTask("NONEXIST"));
    }

    @Test
    @DisplayName("Get task returns existing task")
    void testGetTask() {
        WorkflowTask task = WorkflowTask.create("WF003", "LOAN_APPROVAL", "LN003",
                "审批任务", "user01", "张三", "LOAN_PROCESS", Map.of());
        when(workflowTaskRepository.findByTaskId("WF003")).thenReturn(Optional.of(task));
        WorkflowTask result = workflowDomainService.getTask("WF003");
        assertEquals("WF003", result.getTaskId());
    }
}
