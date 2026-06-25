package com.forex.hedge.accounting.adapter.controller;

import com.forex.common.base.result.R;
import com.forex.hedge.accounting.domain.model.aggregate.HedgeRelationship;
import com.forex.hedge.accounting.domain.model.entity.HedgeEffectivenessTest;
import com.forex.hedge.accounting.domain.service.HedgeAccountingService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.util.*;
import com.forex.common.security.annotation.RequirePermission;

/**
 * 套保会计控制器.
 * Hedge Accounting Controller — REST endpoints for hedge relationship management,
 * effectiveness testing and accounting entry generation.
 */
@Tag(name = "套保会计")
@RestController
@RequestMapping("/api/hedge-accounting")
@RequiredArgsConstructor
@Slf4j
public class HedgeAccountingController {

    private final HedgeAccountingService hedgeAccountingService;

    /** In-memory store for demo purposes. 内存存储 (演示用). */
    private final Map<String, HedgeRelationship> relationshipStore = new LinkedHashMap<>();

    /** Counter for generating IDs. ID自增计数器. */
    private long idCounter = 1L;

    /**
     * 创建套期关系.
     * Create a new hedge relationship.
     */
    @Operation(summary = "创建套期关系")
    @RequirePermission("hedge:create")
    @PostMapping("/relationship/create")
    public R<Map<String, Object>> createRelationship(@RequestBody Map<String, Object> request) {
        log.info("创建套期关系请求: {}", request);
        Long id = idCounter++;
        String relationId = "HR-" + System.currentTimeMillis() + "-" + id;
        Long customerId = Long.valueOf(request.get("customerId").toString());
        String hedgeType = (String) request.get("hedgeType");
        String hedgedItem = (String) request.get("hedgedItem");
        String hedgingInstrument = (String) request.get("hedgingInstrument");
        BigDecimal hedgedAmount = new BigDecimal(request.get("hedgedAmount").toString());
        String hedgedCurrency = (String) request.get("hedgedCurrency");
        BigDecimal instrumentNotional = new BigDecimal(request.get("instrumentNotional").toString());
        String ifrsStandard = (String) request.getOrDefault("ifrsStandard", "IFRS9");

        HedgeRelationship relationship = HedgeRelationship.create(
                id, relationId, customerId, hedgeType, hedgedItem,
                hedgingInstrument, hedgedAmount, hedgedCurrency, instrumentNotional, ifrsStandard);
        relationshipStore.put(relationId, relationship);
        log.info("套期关系创建成功: relationId={}", relationId);

        Map<String, Object> result = toMap(relationship);
        return R.ok("套期关系创建成功", result);
    }

    /**
     * 查询套期关系详情.
     * Get hedge relationship detail by relationId.
     */
    @Operation(summary = "查询套期关系详情")
    @GetMapping("/relationship/{relationId}")
    public R<Map<String, Object>> getRelationship(@PathVariable String relationId) {
        log.info("查询套期关系: relationId={}", relationId);
        HedgeRelationship relationship = relationshipStore.get(relationId);
        if (relationship == null) {
            log.warn("套期关系不存在: relationId={}", relationId);
            return R.fail("套期关系不存在 / relationship not found");
        }
        return R.ok(toMap(relationship));
    }

    /**
     * 正式指定套期关系.
     * Designate a hedge relationship.
     */
    @Operation(summary = "正式指定套期关系")
    @RequirePermission("hedge:designate")
    @PostMapping("/relationship/{relationId}/designate")
    public R<Map<String, Object>> designate(@PathVariable String relationId) {
        log.info("指定套期关系: relationId={}", relationId);
        HedgeRelationship relationship = relationshipStore.get(relationId);
        if (relationship == null) {
            log.warn("套期关系不存在: relationId={}", relationId);
            return R.fail("套期关系不存在 / relationship not found");
        }
        relationship.designate();
        log.info("套期关系已指定: relationId={}", relationId);
        return R.ok("指定成功", toMap(relationship));
    }

    /**
     * 执行预期有效性测试.
     * Perform prospective effectiveness test.
     */
    @Operation(summary = "执行预期有效性测试")
    @RequirePermission("hedge:prospective")
    @PostMapping("/test/prospective")
    public R<Map<String, Object>> prospectiveTest(@RequestBody Map<String, Object> request) {
        String relationId = (String) request.get("relationId");
        log.info("执行预期有效性测试: relationId={}", relationId);
        HedgeRelationship relationship = relationshipStore.get(relationId);
        if (relationship == null) {
            log.warn("套期关系不存在: relationId={}", relationId);
            return R.fail("套期关系不存在 / relationship not found");
        }
        BigDecimal ratio = hedgeAccountingService.performProspectiveTest(relationship);
        boolean passed = ratio.compareTo(new BigDecimal("0.80")) >= 0
                && ratio.compareTo(new BigDecimal("1.25")) <= 0;
        if (passed) {
            relationship.markEffective(ratio);
        } else {
            relationship.markIneffective("Prospective test ratio " + ratio + " out of range");
        }

        Map<String, Object> result = new LinkedHashMap<>();
        result.put("relationId", relationId);
        result.put("testType", "PROSPECTIVE");
        result.put("testMethod", "DOLLAR_OFFSET");
        result.put("ratio", ratio);
        result.put("resultStatus", passed ? "PASS" : "FAIL");
        result.put("testDate", LocalDate.now().toString());
        return R.ok("预期有效性测试完成", result);
    }

    /**
     * 执行追溯有效性测试.
     * Perform retrospective effectiveness test.
     */
    @Operation(summary = "执行追溯有效性测试")
    @RequirePermission("hedge:retrospective")
    @PostMapping("/test/retrospective")
    public R<Map<String, Object>> retrospectiveTest(@RequestBody Map<String, Object> request) {
        String relationId = (String) request.get("relationId");
        log.info("执行追溯有效性测试: relationId={}", relationId);
        HedgeRelationship relationship = relationshipStore.get(relationId);
        if (relationship == null) {
            log.warn("套期关系不存在: relationId={}", relationId);
            return R.fail("套期关系不存在 / relationship not found");
        }
        HedgeEffectivenessTest test = hedgeAccountingService.performRetrospectiveTest(relationship);
        if (test.isPassed()) {
            relationship.markEffective(test.getTestResult());
        } else {
            relationship.markIneffective("Retrospective test ratio " + test.getTestResult() + " out of range");
        }

        Map<String, Object> result = new LinkedHashMap<>();
        result.put("relationId", test.getRelationId());
        result.put("testType", test.getTestType());
        result.put("testMethod", test.getTestMethod());
        result.put("ratio", test.getTestResult());
        result.put("resultStatus", test.getResultStatus());
        result.put("testDate", test.getTestDate().toString());
        return R.ok("追溯有效性测试完成", result);
    }

    /**
     * 生成套保会计分录.
     * Generate hedge accounting journal entries.
     */
    @Operation(summary = "生成套保会计分录")
    @RequirePermission("hedge:generate")
    @PostMapping("/entries/generate")
    public R<Map<String, Object>> generateEntries(@RequestBody Map<String, Object> request) {
        String relationId = (String) request.get("relationId");
        BigDecimal fairValueChange = new BigDecimal(request.get("fairValueChange").toString());
        log.info("生成套保会计分录: relationId={}, fvChange={}", relationId, fairValueChange);
        HedgeRelationship relationship = relationshipStore.get(relationId);
        if (relationship == null) {
            log.warn("套期关系不存在: relationId={}", relationId);
            return R.fail("套期关系不存在 / relationship not found");
        }
        List<Map<String, Object>> entries = hedgeAccountingService.generateHedgeEntries(
                relationship, fairValueChange);

        Map<String, Object> result = new LinkedHashMap<>();
        result.put("relationId", relationId);
        result.put("fairValueChange", fairValueChange);
        result.put("entries", entries);
        result.put("generateTime", LocalDate.now().toString());
        return R.ok("会计分录生成成功", result);
    }

    /**
     * 生成套期有效性报告.
     * Generate hedge effectiveness report for a customer.
     */
    @Operation(summary = "生成套期有效性报告")
    @GetMapping("/report/{customerId}")
    public R<Map<String, Object>> generateReport(@PathVariable Long customerId) {
        log.info("生成套期有效性报告: customerId={}", customerId);
        List<Map<String, Object>> customerRelations = new ArrayList<>();
        for (HedgeRelationship hr : relationshipStore.values()) {
            if (hr.getCustomerId().equals(customerId)) {
                customerRelations.add(toMap(hr));
            }
        }

        long totalCount = customerRelations.size();
        long effectiveCount = customerRelations.stream()
                .filter(m -> "EFFECTIVE".equals(m.get("relationshipStatus")))
                .count();
        BigDecimal effectiveRate = totalCount > 0
                ? new BigDecimal(String.valueOf(effectiveCount))
                        .divide(new BigDecimal(String.valueOf(totalCount)), 4, RoundingMode.HALF_UP)
                : BigDecimal.ZERO;

        Map<String, Object> report = new LinkedHashMap<>();
        report.put("customerId", customerId);
        report.put("reportDate", LocalDate.now().toString());
        report.put("totalRelationships", totalCount);
        report.put("effectiveRelationships", effectiveCount);
        report.put("effectiveRate", effectiveRate);
        report.put("relationships", customerRelations);
        log.info("套期有效性报告生成完成: customerId={}, total={}, effective={}",
                customerId, totalCount, effectiveCount);
        return R.ok("报告生成成功", report);
    }

    /** Convert aggregate to map for response. 聚合转为Map. */
    private Map<String, Object> toMap(HedgeRelationship hr) {
        Map<String, Object> map = new LinkedHashMap<>();
        map.put("id", hr.getId());
        map.put("relationId", hr.getRelationId());
        map.put("customerId", hr.getCustomerId());
        map.put("hedgeType", hr.getHedgeType());
        map.put("hedgedItem", hr.getHedgedItem());
        map.put("hedgingInstrument", hr.getHedgingInstrument());
        map.put("hedgedAmount", hr.getHedgedAmount());
        map.put("hedgedCurrency", hr.getHedgedCurrency());
        map.put("instrumentNotional", hr.getInstrumentNotional());
        map.put("designationDate", hr.getDesignationDate());
        map.put("deDesignationDate", hr.getDeDesignationDate());
        map.put("relationshipStatus", hr.getRelationshipStatus());
        map.put("effectivenessRatio", hr.getEffectivenessRatio());
        map.put("ifrsStandard", hr.getIfrsStandard());
        return map;
    }
}
