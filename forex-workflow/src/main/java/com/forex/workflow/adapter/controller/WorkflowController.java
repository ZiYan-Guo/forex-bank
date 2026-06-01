package com.forex.workflow.adapter.controller;

import com.forex.common.base.annotation.Idempotent;
import com.forex.common.base.annotation.RedisLock;
import com.forex.common.base.dto.PageResp;
import com.forex.common.base.result.R;
import com.forex.workflow.adapter.dto.CompleteTaskReq;
import com.forex.workflow.adapter.dto.StartProcessReq;
import com.forex.workflow.adapter.dto.WorkflowTaskResp;
import com.forex.workflow.application.command.StartProcessCmd;
import com.forex.workflow.application.query.WorkflowQuery;
import com.forex.workflow.application.service.WorkflowAppService;
import com.forex.workflow.domain.model.aggregate.WorkflowTask;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;

import lombok.RequiredArgsConstructor;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@Tag(name = "工作流管理")
@RestController
@RequestMapping("/api/workflow")
@RequiredArgsConstructor
public class WorkflowController {

    private final WorkflowAppService workflowAppService;

    @Operation(summary = "启动工作流")
    @PostMapping("/start")
    @Idempotent(key = "'workflow:start:' + #req.bizNo", expireSeconds = 30)
    public R<WorkflowTaskResp> start(@Valid @RequestBody StartProcessReq req) {
        StartProcessCmd cmd = new StartProcessCmd();
        cmd.setBizType(req.getBizType());
        cmd.setBizNo(req.getBizNo());
        cmd.setTitle(req.getTitle());
        cmd.setAssignee(req.getAssignee());
        cmd.setAssigneeName(req.getAssigneeName());
        cmd.setVariables(req.getVariables());
        WorkflowTask task = workflowAppService.startProcess(cmd);
        return R.ok("工作流启动成功", toResp(task));
    }

    @Operation(summary = "完成任务")
    @PostMapping("/complete/{taskId}")
    @RedisLock(key = "'workflow:complete:' + #taskId")
    public R<WorkflowTaskResp> complete(@PathVariable String taskId,
                                         @Valid @RequestBody CompleteTaskReq req) {
        WorkflowTask task = workflowAppService.completeTask(taskId, req.getApproveResult(), req.getComment());
        return R.ok("任务处理成功", toResp(task));
    }

    @Operation(summary = "查询任务详情")
    @GetMapping("/task/{taskId}")
    public R<WorkflowTaskResp> getTask(@PathVariable String taskId) {
        WorkflowTask task = workflowAppService.getTaskDetail(taskId);
        return R.ok(toResp(task));
    }

    @Operation(summary = "分页查询任务")
    @PostMapping("/task/page")
    public R<PageResp<WorkflowTaskResp>> pageQuery(@RequestBody WorkflowQuery query) {
        PageResp<WorkflowTask> pageResp = workflowAppService.pageQuery(query);
        List<WorkflowTaskResp> records = pageResp.getRecords().stream()
                .map(this::toResp)
                .toList();
        PageResp<WorkflowTaskResp> resp = PageResp.of(
                pageResp.getTotal(), records, pageResp.getPageNum(), pageResp.getPageSize());
        return R.ok(resp);
    }

    @Operation(summary = "查询我的任务")
    @GetMapping("/my-tasks/{assignee}")
    public R<PageResp<WorkflowTaskResp>> getMyTasks(@PathVariable String assignee) {
        PageResp<WorkflowTask> pageResp = workflowAppService.getMyTasks(assignee);
        List<WorkflowTaskResp> records = pageResp.getRecords().stream()
                .map(this::toResp)
                .toList();
        PageResp<WorkflowTaskResp> resp = PageResp.of(
                pageResp.getTotal(), records, pageResp.getPageNum(), pageResp.getPageSize());
        return R.ok(resp);
    }

    private WorkflowTaskResp toResp(WorkflowTask task) {
        WorkflowTaskResp resp = new WorkflowTaskResp();
        resp.setId(task.getId());
        resp.setTaskId(task.getTaskId());
        resp.setBizType(task.getBizType());
        resp.setBizNo(task.getBizNo());
        resp.setTitle(task.getTitle());
        resp.setAssignee(task.getAssignee());
        resp.setAssigneeName(task.getAssigneeName());
        resp.setProcessDefinitionKey(task.getProcessDefinitionKey());
        resp.setStatus(task.getStatus());
        resp.setCreateTime(task.getCreateTime());
        resp.setCompleteTime(task.getCompleteTime());
        resp.setComment(task.getComment());
        resp.setVariables(task.getVariables());
        return resp;
    }
}
