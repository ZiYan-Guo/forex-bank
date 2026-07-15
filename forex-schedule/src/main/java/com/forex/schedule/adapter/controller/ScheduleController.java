package com.forex.schedule.adapter.controller;

import com.forex.common.base.annotation.Idempotent;
import com.forex.common.base.dto.PageResp;
import com.forex.common.base.result.R;
import com.forex.schedule.adapter.dto.JobLogResp;
import com.forex.schedule.adapter.dto.ScheduleJobResp;
import com.forex.schedule.application.command.JobCmd;
import com.forex.schedule.application.query.JobQuery;
import com.forex.schedule.application.service.ScheduleAppService;
import com.forex.schedule.domain.model.aggregate.JobLog;
import com.forex.schedule.domain.model.aggregate.ScheduleJob;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;

import jakarta.validation.Valid;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import com.forex.common.security.annotation.RequirePermission;

@Tag(name = "定时任务")
@RestController
@RequestMapping("/api/schedule")
@RequiredArgsConstructor
@Slf4j
public class ScheduleController {

    private final ScheduleAppService scheduleAppService;

    @Operation(summary = "添加定时任务")
    @RequirePermission("schedule:add")
    @PostMapping("/job/add")
    @Idempotent(key = "'schedule:job:add:' + #cmd.jobHandler", expireSeconds = 10)
    public R<ScheduleJobResp> addJob(@Valid @RequestBody JobCmd cmd) {
        log.info("Schedule add job API called: handler={} / 调度新增任务接口调用：处理器={}", cmd.getJobHandler(), cmd.getJobHandler());
        ScheduleJob job = scheduleAppService.addJob(cmd);
        return R.ok("任务添加成功", toResp(job));
    }

    @Operation(summary = "更新定时任务")
    @RequirePermission("schedule:update")
    @PutMapping("/job/update/{id}")
    public R<ScheduleJobResp> updateJob(@PathVariable Long id, @Valid @RequestBody JobCmd cmd) {
        log.info("Schedule update job API called: id={}, handler={} / 调度更新任务接口调用：ID={}, 处理器={}",
                id, cmd.getJobHandler(), id, cmd.getJobHandler());
        ScheduleJob job = scheduleAppService.updateJob(id, cmd);
        return R.ok("任务更新成功", toResp(job));
    }

    @Operation(summary = "启停定时任务")
    @RequirePermission("schedule:toggle")
    @PostMapping("/job/toggle/{id}")
    public R<Void> toggleJob(@PathVariable Long id) {
        log.info("Schedule toggle job API called: id={} / 调度启停任务接口调用：ID={}", id, id);
        scheduleAppService.toggleJob(id);
        return R.okMsg("任务状态已切换");
    }

    @Operation(summary = "手动触发任务")
    @RequirePermission("schedule:trigger")
    @PostMapping("/job/trigger/{id}")
    public R<Void> triggerJob(@PathVariable Long id) {
        log.info("Schedule trigger job API called: id={} / 调度触发任务接口调用：ID={}", id, id);
        scheduleAppService.triggerJob(id);
        return R.okMsg("任务已触发");
    }

    @Operation(summary = "查询任务详情")
    @GetMapping("/job/{id}")
    public R<ScheduleJobResp> getJob(@PathVariable Long id) {
        ScheduleJob job = scheduleAppService.getJob(id);
        return R.ok(toResp(job));
    }

    @Operation(summary = "分页查询任务")
    @RequirePermission("schedule:page")
    @PostMapping("/job/page")
    public R<PageResp<ScheduleJobResp>> pageQuery(@RequestBody JobQuery query) {
        log.info("Schedule page query API called: pageNum={}, pageSize={} / 调度分页查询接口调用：页码={}, 页大小={}",
                query.getPageNum(), query.getPageSize(), query.getPageNum(), query.getPageSize());
        PageResp<ScheduleJob> pageResp = scheduleAppService.pageQuery(query);
        List<ScheduleJobResp> records = pageResp.getRecords().stream()
                .map(this::toResp)
                .toList();
        PageResp<ScheduleJobResp> resp = PageResp.of(
                pageResp.getTotal(), records, pageResp.getPageNum(), pageResp.getPageSize());
        return R.ok(resp);
    }

    @Operation(summary = "查询任务执行日志")
    @GetMapping("/job/{jobId}/logs")
    public R<List<JobLogResp>> getJobLogs(@PathVariable Long jobId) {
        log.info("Schedule logs API called: jobId={} / 调度日志查询接口调用：任务ID={}", jobId, jobId);
        List<JobLog> logs = scheduleAppService.getJobLogs(jobId);
        List<JobLogResp> respList = logs.stream()
                .map(this::toLogResp)
                .toList();
        return R.ok(respList);
    }

    private ScheduleJobResp toResp(ScheduleJob job) {
        ScheduleJobResp resp = new ScheduleJobResp();
        resp.setId(job.getId());
        resp.setJobName(job.getJobName());
        resp.setJobGroup(job.getJobGroup());
        resp.setJobHandler(job.getJobHandler());
        resp.setCronExpression(job.getCronExpression());
        resp.setJobDesc(job.getJobDesc());
        resp.setStatus(job.getStatus());
        resp.setLastResult(job.getLastResult());
        resp.setLastExecuteTime(job.getLastExecuteTime());
        resp.setNextExecuteTime(job.getNextExecuteTime());
        return resp;
    }

    private JobLogResp toLogResp(JobLog log) {
        JobLogResp resp = new JobLogResp();
        resp.setId(log.getId());
        resp.setJobId(log.getJobId());
        resp.setJobName(log.getJobName());
        resp.setJobHandler(log.getJobHandler());
        resp.setStartTime(log.getStartTime());
        resp.setEndTime(log.getEndTime());
        resp.setExecuteStatus(log.getExecuteStatus());
        resp.setExecuteResult(log.getExecuteResult());
        resp.setErrorMsg(log.getErrorMsg());
        return resp;
    }
}
