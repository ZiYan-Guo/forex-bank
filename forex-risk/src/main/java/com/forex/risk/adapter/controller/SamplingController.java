package com.forex.risk.adapter.controller;

import com.forex.common.base.result.R;
import com.forex.risk.adapter.dto.CompleteTaskReq;
import com.forex.risk.adapter.dto.GenerateSamplingReq;
import com.forex.risk.adapter.dto.SamplingEvaluateReq;
import com.forex.risk.adapter.dto.SamplingRuleReq;
import com.forex.risk.adapter.dto.UpdateRuleStatusReq;
import com.forex.risk.application.service.SamplingRuleEngine;
import com.forex.risk.domain.model.entity.SamplingRule;
import com.forex.risk.domain.model.entity.SamplingTask;
import com.forex.risk.domain.repository.SamplingRuleRepository;
import com.forex.risk.domain.repository.SamplingTaskRepository;

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
import java.math.RoundingMode;
import java.time.LocalDate;
import java.time.LocalDateTime;
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
    private final SamplingRuleRepository samplingRuleRepository;
    private final SamplingTaskRepository samplingTaskRepository;

    @Operation(summary = "查询抽查规则")
    @GetMapping("/rules")
    public R<List<Map<String, Object>>> listRules() {
        log.info("Listing sampling rules / 查询抽查规则列表");
        List<Map<String, Object>> rules = samplingRuleRepository.findAll().stream()
                .map(this::toRuleView)
                .toList();
        log.info("Sampling rules loaded: count={} / 抽查规则加载完成：数量={}", rules.size(), rules.size());
        return R.ok(rules);
    }

    @Operation(summary = "新增抽查规则")
    @RequirePermission("risk:rule:create")
    @PostMapping("/rules")
    public R<Map<String, Object>> createRule(@RequestBody SamplingRuleReq body) {
        log.info("Creating sampling rule: ruleCode={}, targetModule={} / 新增抽查规则：规则编码={}, 目标模块={}",
                body.getRuleCode(), body.getTargetModule(), body.getRuleCode(), body.getTargetModule());
        SamplingRule rule = toRule(null, body, null);
        SamplingRule saved = samplingRuleRepository.save(rule);
        log.info("Sampling rule created: id={}, ruleCode={} / 抽查规则已新增：ID={}, 规则编码={}",
                saved.getId(), saved.getRuleCode(), saved.getId(), saved.getRuleCode());
        return R.ok(toRuleView(saved));
    }

    @Operation(summary = "更新抽查规则")
    @RequirePermission("risk:rule:update")
    @PutMapping("/rules/{id}")
    public R<Map<String, Object>> updateRule(@PathVariable Long id, @RequestBody SamplingRuleReq body) {
        SamplingRule existing = samplingRuleRepository.findById(id).orElse(null);
        if (existing == null) {
            log.warn("Sampling rule update failed, rule not found: id={} / 更新抽查规则失败，规则不存在：ID={}", id, id);
            return R.fail("规则不存在");
        }
        log.info("Updating sampling rule: id={}, ruleCode={} / 更新抽查规则：ID={}, 规则编码={}",
                id, existing.getRuleCode(), id, existing.getRuleCode());
        SamplingRule saved = samplingRuleRepository.save(toRule(id, body, existing));
        return R.ok(toRuleView(saved));
    }

    @Operation(summary = "切换抽查规则状态")
    @RequirePermission("risk:rule:update")
    @PutMapping("/rules/{id}/status")
    public R<Map<String, Object>> updateRuleStatus(@PathVariable Long id, @RequestBody UpdateRuleStatusReq body) {
        SamplingRule existing = samplingRuleRepository.findById(id).orElse(null);
        if (existing == null) {
            log.warn("Sampling rule status update failed, rule not found: id={} / 更新抽查规则状态失败，规则不存在：ID={}", id, id);
            return R.fail("规则不存在");
        }
        SamplingRuleReq merged = toRuleReq(existing);
        merged.setStatus(body.getStatus() == null || body.getStatus().isBlank() ? "INACTIVE" : body.getStatus());
        SamplingRule saved = samplingRuleRepository.save(toRule(id, merged, existing));
        log.info("Sampling rule status updated: id={}, status={} / 抽查规则状态已更新：ID={}, 状态={}",
                id, saved.getStatus(), id, saved.getStatus());
        return R.ok(toRuleView(saved));
    }

    @Operation(summary = "删除抽查规则")
    @RequirePermission("risk:rule:delete")
    @PostMapping("/rules/{id}/delete")
    public R<Void> deleteRule(@PathVariable Long id) {
        log.info("Deleting sampling rule: id={} / 删除抽查规则：ID={}", id, id);
        samplingRuleRepository.deleteById(id);
        return R.ok();
    }

    /**
     * Evaluate a transaction against sampling rules.
     * Returns the applicable sampling rate.
     * 评估交易是否命中抽查规则，返回抽查比例。
     */
    @Operation(summary = "评估交易抽查规则")
    @RequirePermission("risk:evaluate")
    @PostMapping("/evaluate")
    public R<Map<String, Object>> evaluate(@RequestBody SamplingEvaluateReq body) {
        Long customerId = body.getCustomerId();
        String bizType = body.getBizType();
        BigDecimal amount = body.getAmount() == null ? BigDecimal.ZERO : body.getAmount();
        String currency = body.getCurrency();
        String countryCode = body.getCountryCode();
        Integer accountAgeDays = body.getAccountAgeDays();

        log.info("Sampling evaluate request: customerId={}, bizType={}, amount={} {}, country={}, accountAgeDays={} / 抽查评估请求：客户ID={}, 业务类型={}, 金额={} {}, 国家={}, 开户天数={}",
                customerId, bizType, amount, currency, countryCode, accountAgeDays,
                customerId, bizType, amount, currency, countryCode, accountAgeDays);

        BigDecimal rate = samplingRuleEngine.evaluateTransaction(
                customerId, bizType, amount, currency, countryCode, accountAgeDays);

        Map<String, Object> result = new LinkedHashMap<>();
        result.put("customerId", customerId);
        result.put("bizType", bizType);
        result.put("amount", amount);
        result.put("currency", currency);
        result.put("countryCode", countryCode);
        result.put("accountAgeDays", accountAgeDays);
        result.put("samplingRate", rate);

        log.info("Sampling evaluate result: rate={}% / 抽查评估结果：比例={}%", rate, rate);
        return R.ok(result);
    }

    /**
     * Generate sampling inspection tasks for a given date.
     * 根据日期生成抽查任务。
     */
    @Operation(summary = "生成抽查任务")
    @RequirePermission("risk:generate")
    @PostMapping("/tasks/generate")
    public R<Map<String, Object>> generateTasks(@RequestBody GenerateSamplingReq body) {
        LocalDate date = body.getDate() != null && !body.getDate().isBlank()
                ? LocalDate.parse(body.getDate()) : LocalDate.now();
        log.info("Generate sampling tasks request: date={} / 生成抽查任务请求：日期={}", date, date);
        List<SamplingTask> tasks = samplingRuleEngine.generateSamplingTasks(date).stream()
                .map(this::toTask)
                .toList();
        List<SamplingTask> savedTasks = samplingTaskRepository.saveAll(tasks);

        Map<String, Object> result = new LinkedHashMap<>();
        result.put("date", date.toString());
        result.put("count", savedTasks.size());
        result.put("tasks", savedTasks.stream().map(this::toTaskView).toList());

        log.info("Generated and persisted {} sampling tasks / 已生成并持久化 {} 条抽查任务",
                savedTasks.size(), savedTasks.size());
        return R.ok(result);
    }

    /**
     * List all pending sampling tasks.
     * 查询待处理的抽查任务列表。
     */
    @Operation(summary = "查询抽查任务列表")
    @GetMapping("/tasks")
    public R<Map<String, Object>> listTasks() {
        List<Map<String, Object>> tasks = samplingTaskRepository.findAll().stream()
                .map(this::toTaskView)
                .toList();
        log.info("Listing sampling tasks, total={} / 查询抽查任务列表：数量={}", tasks.size(), tasks.size());
        Map<String, Object> result = new LinkedHashMap<>();
        result.put("total", tasks.size());
        result.put("tasks", tasks);
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
                                                @RequestBody CompleteTaskReq body) {
        log.info("Completing sampling task: taskId={}, result={} / 完成抽查任务：任务ID={}, 结果={}",
                taskId, body.getResult(), taskId, body.getResult());
        SamplingTask task = samplingTaskRepository.findByTaskId(taskId).orElse(null);
        if (task != null) {
            SamplingTask saved = samplingTaskRepository.save(task.complete(body.getResult(), body.getComment()));
            log.info("Sampling task marked as COMPLETED: taskId={} / 抽查任务已完成：任务ID={}", taskId, taskId);
            return R.ok(toTaskView(saved));
        }
        log.warn("Sampling task not found: taskId={} / 抽查任务不存在：任务ID={}", taskId, taskId);
        return R.fail("任务不存在");
    }

    /**
     * Get sampling coverage statistics by module, rate, and period.
     * 获取抽查覆盖率统计（按模块/比例/周期）。
     */
    @Operation(summary = "抽查覆盖统计")
    @GetMapping("/statistics")
    public R<Map<String, Object>> getStatistics() {
        log.info("Calculating sampling statistics / 计算抽查统计");
        Map<String, Object> stats = new LinkedHashMap<>();

        // Baseline totals are daily operational denominators for dashboard coverage.
        // 基准总量作为覆盖率看板的业务分母。
        Map<String, Integer> moduleTotals = new LinkedHashMap<>();
        moduleTotals.put("FX_EXCHANGE", 620);
        moduleTotals.put("FX_PAYMENT", 780);
        moduleTotals.put("FX_TRADING", 240);
        moduleTotals.put("FX_SETTLEMENT", 180);

        Map<String, Long> sampledByModule = new LinkedHashMap<>();
        BigDecimal totalAmount = BigDecimal.ZERO;
        List<SamplingTask> persistedTasks = samplingTaskRepository.findAll();
        for (SamplingTask task : persistedTasks) {
            String module = task.getBizType() == null ? "UNKNOWN" : task.getBizType();
            sampledByModule.merge(module, 1L, Long::sum);
            if (task.getAmount() != null) {
                totalAmount = totalAmount.add(task.getAmount());
            }
        }

        long sampledTransactions = persistedTasks.size();
        int totalTransactions = moduleTotals.values().stream().mapToInt(Integer::intValue).sum();
        BigDecimal coverage = totalTransactions == 0 ? BigDecimal.ZERO
                : BigDecimal.valueOf(sampledTransactions * 100.0 / totalTransactions).setScale(1, RoundingMode.HALF_UP);

        stats.put("last30dCoverageRate", coverage + "%");
        stats.put("totalTransactions", totalTransactions);
        stats.put("sampledTransactions", sampledTransactions);
        stats.put("totalAmount", totalAmount);

        List<Map<String, Object>> moduleBreakdown = new ArrayList<>();
        for (Map.Entry<String, Integer> entry : moduleTotals.entrySet()) {
            long sampled = sampledByModule.getOrDefault(entry.getKey(), 0L);
            BigDecimal rate = BigDecimal.valueOf(sampled * 100.0 / entry.getValue())
                    .setScale(1, RoundingMode.HALF_UP);
            moduleBreakdown.add(Map.of(
                    "module", entry.getKey(),
                    "moduleName", moduleName(entry.getKey()),
                    "sampledCount", sampled,
                    "totalCount", entry.getValue(),
                    "rate", rate + "%"));
        }
        stats.put("moduleBreakdown", moduleBreakdown);

        log.info("Sampling statistics calculated: coverageRate={}%, sampled={}, total={} / 抽查统计完成：覆盖率={}%，抽查笔数={}，总笔数={}",
                coverage, sampledTransactions, totalTransactions, coverage, sampledTransactions, totalTransactions);
        return R.ok(stats);
    }

    private SamplingRule toRule(Long id, SamplingRuleReq body, SamplingRule existing) {
        return new SamplingRule(
                id,
                valueOrDefault(body.getRuleCode(), existing == null ? null : existing.getRuleCode()),
                valueOrDefault(body.getRuleName(), existing == null ? null : existing.getRuleName()),
                valueOrDefault(body.getConditionJson(), existing == null ? "{}" : existing.getConditionJson()),
                body.getSamplingRate() == null ? (existing == null ? BigDecimal.TEN : existing.getSamplingRate()) : body.getSamplingRate(),
                valueOrDefault(body.getTargetModule(), existing == null ? "FX_PAYMENT" : existing.getTargetModule()),
                date(valueOrDefault(body.getEffectiveDate(), existing == null ? LocalDate.now().toString() : existing.getEffectiveDate())),
                date(valueOrDefault(body.getExpireDate(), existing == null ? null : existing.getExpireDate())),
                body.getPriority() == null ? (existing == null ? 0 : existing.getPriority()) : body.getPriority(),
                valueOrDefault(body.getStatus(), existing == null ? "ACTIVE" : existing.getStatus()),
                body.getIsAutoExtract() == null ? existing == null || Boolean.TRUE.equals(existing.getIsAutoExtract()) : body.getIsAutoExtract()
        );
    }

    private SamplingRuleReq toRuleReq(SamplingRule rule) {
        SamplingRuleReq req = new SamplingRuleReq();
        req.setRuleCode(rule.getRuleCode());
        req.setRuleName(rule.getRuleName());
        req.setConditionJson(rule.getConditionJson());
        req.setSamplingRate(rule.getSamplingRate());
        req.setTargetModule(rule.getTargetModule());
        req.setEffectiveDate(rule.getEffectiveDate() == null ? null : rule.getEffectiveDate().toString());
        req.setExpireDate(rule.getExpireDate() == null ? null : rule.getExpireDate().toString());
        req.setPriority(rule.getPriority());
        req.setStatus(rule.getStatus());
        req.setIsAutoExtract(rule.getIsAutoExtract());
        return req;
    }

    private Map<String, Object> toRuleView(SamplingRule rule) {
        Map<String, Object> view = new LinkedHashMap<>();
        view.put("id", rule.getId());
        view.put("ruleCode", rule.getRuleCode());
        view.put("ruleName", rule.getRuleName());
        view.put("conditionJson", rule.getConditionJson());
        view.put("samplingRate", rule.getSamplingRate());
        view.put("targetModule", rule.getTargetModule());
        view.put("effectiveDate", rule.getEffectiveDate());
        view.put("expireDate", rule.getExpireDate());
        view.put("priority", rule.getPriority());
        view.put("status", rule.getStatus());
        view.put("isAutoExtract", rule.getIsAutoExtract());
        return view;
    }

    @SuppressWarnings("unchecked")
    private SamplingTask toTask(Map<String, Object> payload) {
        // The engine returns normalized task snapshots; this adapter converts them into persistent domain tasks.
        // 规则引擎返回标准化任务快照，本适配层将其转换为可持久化的领域任务。
        Object matchedRules = payload.get("matchedRules");
        return new SamplingTask(
                null,
                str(payload.get("taskId")),
                str(payload.get("bizNo")),
                str(payload.get("bizType")),
                toLong(payload.get("customerId")),
                decimal(payload.get("amount")),
                str(payload.get("currency")),
                str(payload.get("countryCode")),
                integer(payload.get("accountAgeDays")),
                decimal(payload.get("samplingRate")),
                str(payload.get("reason")),
                matchedRules instanceof List<?> list
                        ? list.stream().map(Object::toString).toList()
                        : List.of(),
                valueOrDefault(str(payload.get("status")), "PENDING"),
                date(payload.get("businessDate")),
                dateTime(payload.get("createdAt")),
                null,
                null,
                null
        );
    }

    private Map<String, Object> toTaskView(SamplingTask task) {
        Map<String, Object> view = new LinkedHashMap<>();
        view.put("id", task.getId());
        view.put("taskId", task.getTaskId());
        view.put("bizNo", task.getBizNo());
        view.put("bizType", task.getBizType());
        view.put("customerId", task.getCustomerId());
        view.put("amount", task.getAmount());
        view.put("currency", task.getCurrency());
        view.put("countryCode", task.getCountryCode());
        view.put("accountAgeDays", task.getAccountAgeDays());
        view.put("samplingRate", task.getSamplingRate());
        view.put("reason", task.getReason());
        view.put("matchedRules", task.getMatchedRules());
        view.put("status", task.getStatus());
        view.put("businessDate", task.getBusinessDate());
        view.put("createdAt", task.getCreatedAt());
        view.put("completedAt", task.getCompletedAt());
        view.put("reviewResult", task.getReviewResult());
        view.put("reviewComment", task.getReviewComment());
        return view;
    }

    private String moduleName(String module) {
        return switch (module) {
            case "FX_EXCHANGE" -> "结售汇 Exchange";
            case "FX_PAYMENT" -> "跨境支付 Payment";
            case "FX_TRADING" -> "外汇买卖 Trading";
            case "FX_SETTLEMENT" -> "国际结算 Settlement";
            default -> module;
        };
    }

    private String valueOrDefault(String value, Object defaultValue) {
        if (value == null || value.isBlank()) {
            return defaultValue == null ? null : defaultValue.toString();
        }
        return value;
    }

    private BigDecimal decimal(Object value) {
        return value == null || value.toString().isBlank() ? BigDecimal.ZERO : new BigDecimal(value.toString());
    }

    private Long toLong(Object value) {
        return value == null || value.toString().isBlank() ? null : Long.valueOf(value.toString());
    }

    private Integer integer(Object value) {
        return value == null || value.toString().isBlank() ? null : Integer.valueOf(value.toString());
    }

    private String str(Object value) {
        return value == null ? null : value.toString();
    }

    private LocalDate date(Object value) {
        return value == null || value.toString().isBlank() ? null : LocalDate.parse(value.toString());
    }

    private LocalDateTime dateTime(Object value) {
        return value == null || value.toString().isBlank() ? LocalDateTime.now() : LocalDateTime.parse(value.toString());
    }
}
