package com.forex.risk.adapter.controller;

import com.forex.common.base.result.R;
import com.forex.risk.application.service.SamplingRuleEngine;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import com.forex.common.security.annotation.RequirePermission;

/**
 * Capital account facilitation sampling inspection controller.
 * 资本项目便利化抽查控制器。
 */
@Tag(name = "便利化抽查")
@RestController
@RequestMapping("/api/risk/sampling")
@RequiredArgsConstructor
@Slf4j
public class SamplingController {

    private final SamplingRuleEngine samplingRuleEngine;

    // Mock task storage 模拟任务存储
    private final List<Map<String, Object>> taskStore = new ArrayList<>();

    /**
     * Evaluate a transaction against sampling rules.
     * Returns the applicable sampling rate.
     * 评估交易是否命中抽查规则，返回抽查比例。
     */
    @Operation(summary = "评估交易抽查规则")
    @RequirePermission("risk:evaluate")
    @PostMapping("/evaluate")
    public R<Map<String, Object>> evaluate(@RequestBody Map<String, Object> body) {
        Long customerId = body.get("customerId") != null
                ? Long.valueOf(body.get("customerId").toString()) : null;
        String bizType = (String) body.get("bizType");
        BigDecimal amount = body.get("amount") != null
                ? new BigDecimal(body.get("amount").toString()) : BigDecimal.ZERO;
        String currency = (String) body.get("currency");
        String countryCode = (String) body.get("countryCode");

        log.info("Sampling evaluate request: customerId={}, bizType={}, amount={} {}, country={}",
                customerId, bizType, amount, currency, countryCode);

        BigDecimal rate = samplingRuleEngine.evaluateTransaction(
                customerId, bizType, amount, currency, countryCode);

        Map<String, Object> result = new LinkedHashMap<>();
        result.put("customerId", customerId);
        result.put("bizType", bizType);
        result.put("amount", amount);
        result.put("currency", currency);
        result.put("countryCode", countryCode);
        result.put("samplingRate", rate);

        log.info("Sampling evaluate result: rate={}%", rate);
        return R.ok(result);
    }

    /**
     * Generate sampling inspection tasks for a given date.
     * 根据日期生成抽查任务。
     */
    @Operation(summary = "生成抽查任务")
    @RequirePermission("risk:generate")
    @PostMapping("/tasks/generate")
    public R<Map<String, Object>> generateTasks(@RequestBody Map<String, Object> body) {
        log.info("Generate sampling tasks request: {}", body);
        LocalDate date = body.get("date") != null
                ? LocalDate.parse(body.get("date").toString()) : LocalDate.now();
        List<Map<String, Object>> tasks = samplingRuleEngine.generateSamplingTasks(date);
        // Persist to mock store 持久化到模拟存储
        taskStore.clear();
        taskStore.addAll(tasks);

        Map<String, Object> result = new LinkedHashMap<>();
        result.put("date", date.toString());
        result.put("count", tasks.size());
        result.put("tasks", tasks);

        log.info("Generated {} sampling tasks", tasks.size());
        return R.ok(result);
    }

    /**
     * List all pending sampling tasks.
     * 查询待处理的抽查任务列表。
     */
    @Operation(summary = "查询抽查任务列表")
    @GetMapping("/tasks")
    public R<Map<String, Object>> listTasks() {
        log.info("Listing sampling tasks, total: {}", taskStore.size());
        Map<String, Object> result = new LinkedHashMap<>();
        result.put("total", taskStore.size());
        result.put("tasks", taskStore);
        return R.ok(result);
    }

    /**
     * Mark a sampling task as completed with inspection result.
     * 将抽查任务标记为已完成并记录检查结果。
     */
    @Operation(summary = "完成抽查任务")
    @RequirePermission("risk:complete")
    @PutMapping("/tasks/{taskId}/complete")
    public R<Map<String, Object>> completeTask(@PathVariable String taskId,
                                                @RequestBody Map<String, String> body) {
        log.info("Completing sampling task: taskId={}, result={}", taskId, body);
        for (Map<String, Object> task : taskStore) {
            if (taskId.equals(task.get("taskId"))) {
                task.put("status", "COMPLETED");
                task.put("completedAt", java.time.LocalDateTime.now().toString());
                task.put("reviewResult", body.getOrDefault("result", "PASS"));
                task.put("reviewComment", body.getOrDefault("comment", ""));
                log.info("Task {} marked as COMPLETED", taskId);
                return R.ok(task);
            }
        }
        log.warn("Task not found: {}", taskId);
        return R.fail("任务不存在");
    }

    /**
     * Get sampling coverage statistics by module, rate, and period.
     * 获取抽查覆盖率统计（按模块/比例/周期）。
     */
    @Operation(summary = "抽查覆盖统计")
    @GetMapping("/statistics")
    public R<Map<String, Object>> getStatistics() {
        log.info("Calculating sampling statistics");
        Map<String, Object> stats = new LinkedHashMap<>();

        // Mock statistics 模拟统计数据
        stats.put("last30dCoverageRate", "28.5%");
        stats.put("totalTransactions", 1520);
        stats.put("sampledTransactions", 433);
        stats.put("totalAmount", "¥ 892,450,000.00");

        List<Map<String, Object>> moduleBreakdown = new ArrayList<>();
        moduleBreakdown.add(Map.of(
                "module", "FX_EXCHANGE", "moduleName", "结售汇 结售汇",
                "sampledCount", 180, "totalCount", 600, "rate", "30.0%"));
        moduleBreakdown.add(Map.of(
                "module", "FX_PAYMENT", "moduleName", "跨境支付 跨境支付",
                "sampledCount", 200, "totalCount", 750, "rate", "26.7%"));
        moduleBreakdown.add(Map.of(
                "module", "FX_TRADING", "moduleName", "外汇买卖 外汇买卖",
                "sampledCount", 53, "totalCount", 170, "rate", "31.2%"));
        stats.put("moduleBreakdown", moduleBreakdown);

        log.info("Sampling statistics calculated: coverageRate=28.5%");
        return R.ok(stats);
    }
}
