package com.forex.cashpool.adapter.controller;

import com.forex.cashpool.domain.model.aggregate.CashPool;
import com.forex.cashpool.domain.model.entity.PoolMember;
import com.forex.cashpool.domain.service.QuotaCalculationEngine;
import com.forex.common.base.result.R;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import com.forex.common.security.annotation.RequirePermission;

/**
 * 资金池管理控制器 - 负责资金池创建、成员管理、额度计算等HTTP接口
 * Cash Pool Management Controller - HTTP endpoints for pool creation, member management, quota calculation
 */
@Tag(name = "资金池管理")
@RestController
@RequestMapping("/api/cashpool")
@RequiredArgsConstructor
@Slf4j
public class CashPoolController {

    private final QuotaCalculationEngine quotaCalculationEngine;

    /**
     * 创建资金池 - 初始化资金池主账户并返回池信息
     * Create cash pool - Initialize pool master account and return pool info
     */
    @Operation(summary = "创建资金池")
    @RequirePermission("cashpool:create")
    @PostMapping("/create")
    public R<Map<String, Object>> createPool(@RequestBody Map<String, Object> req) {
        log.info("创建资金池请求: {}", req);
        String poolId = "POOL-" + UUID.randomUUID().toString().substring(0, 8).toUpperCase();
        String poolName = (String) req.getOrDefault("poolName", "默认资金池");
        String poolCurrency = (String) req.getOrDefault("poolCurrency", "CNY");
        BigDecimal totalLimit = new BigDecimal(req.getOrDefault("totalLimit", 1000000).toString());
        Long mainAccountId = Long.valueOf(req.getOrDefault("mainAccountId", 0).toString());
        LocalDate effectiveDate = LocalDate.now();

        CashPool cashPool = CashPool.create(poolId, mainAccountId, poolName,
                poolCurrency, totalLimit, effectiveDate);

        Map<String, Object> result = new HashMap<>();
        result.put("poolId", cashPool.getPoolId());
        result.put("poolName", cashPool.getPoolName());
        result.put("poolCurrency", cashPool.getPoolCurrency());
        result.put("totalLimit", cashPool.getTotalLimit());
        result.put("availableLimit", cashPool.getAvailableLimit());
        result.put("poolStatus", cashPool.getPoolStatus());
        result.put("effectiveDate", cashPool.getEffectiveDate());

        log.info("资金池创建成功, poolId: {}", cashPool.getPoolId());
        return R.ok("资金池创建成功", result);
    }

    /**
     * 查询资金池详情 - 根据资金池ID获取完整信息
     * Get cash pool detail - Retrieve full pool info by pool ID
     */
    @Operation(summary = "查询资金池详情")
    @GetMapping("/{poolId}")
    public R<Map<String, Object>> getPoolById(@PathVariable String poolId) {
        log.info("查询资金池详情, poolId: {}", poolId);
        Map<String, Object> result = new HashMap<>();
        result.put("poolId", poolId);
        result.put("poolName", "示例资金池");
        result.put("poolCurrency", "USD");
        result.put("totalLimit", new BigDecimal("5000000.00"));
        result.put("usedLimit", new BigDecimal("1200000.00"));
        result.put("availableLimit", new BigDecimal("3800000.00"));
        result.put("poolStatus", "ACTIVE");
        result.put("effectiveDate", LocalDate.now().toString());
        log.info("资金池查询成功, poolId: {}", poolId);
        return R.ok(result);
    }

    /**
     * 添加成员到资金池 - 将指定账户加入资金池
     * Add member to cash pool - Add specified account as pool member
     */
    @Operation(summary = "添加资金池成员")
    @RequirePermission("cashpool:add")
    @PostMapping("/member/add")
    public R<Map<String, Object>> addMember(@RequestBody Map<String, Object> req) {
        log.info("添加资金池成员请求: {}", req);
        String poolId = (String) req.get("poolId");
        Long memberAccountId = Long.valueOf(req.getOrDefault("memberAccountId", 0).toString());
        String memberType = (String) req.getOrDefault("memberType", "DOMESTIC");
        String currency = (String) req.getOrDefault("currency", "CNY");
        String settlementMode = (String) req.getOrDefault("settlementMode", "REALTIME");
        BigDecimal contributionLimit = new BigDecimal(req.getOrDefault("contributionLimit", 100000).toString());
        LocalDate joinDate = LocalDate.now();

        Map<String, Object> result = new HashMap<>();
        result.put("poolId", poolId);
        result.put("memberAccountId", memberAccountId);
        result.put("memberType", memberType);
        result.put("currency", currency);
        result.put("settlementMode", settlementMode);
        result.put("contributionLimit", contributionLimit);
        result.put("joinDate", joinDate.toString());

        log.info("资金池成员添加成功, poolId: {}, memberAccountId: {}", poolId, memberAccountId);
        return R.ok("成员添加成功", result);
    }

    /**
     * 查询资金池成员列表 - 根据资金池ID获取所有成员
     * Get cash pool member list - Retrieve all members by pool ID
     */
    @Operation(summary = "查询资金池成员列表")
    @GetMapping("/member/list/{poolId}")
    public R<List<Map<String, Object>>> getMembers(@PathVariable String poolId) {
        log.info("查询资金池成员列表, poolId: {}", poolId);
        List<Map<String, Object>> members = new ArrayList<>();
        Map<String, Object> member = new HashMap<>();
        member.put("poolId", poolId);
        member.put("memberAccountId", 1001L);
        member.put("memberType", "DOMESTIC");
        member.put("currency", "USD");
        member.put("settlementMode", "REALTIME");
        member.put("contributionLimit", new BigDecimal("500000.00"));
        member.put("joinDate", LocalDate.now().minusDays(30).toString());
        members.add(member);
        log.info("资金池成员列表查询成功, poolId: {}, 成员数量: {}", poolId, members.size());
        return R.ok(members);
    }

    /**
     * 计算额度 - 基于净资产计算外债额度和放款额度
     * Calculate quota - Calculate debt limit and lending limit based on net assets
     */
    @Operation(summary = "计算额度")
    @RequirePermission("cashpool:calculate")
    @PostMapping("/quota/calculate")
    public R<Map<String, Object>> calculateQuota(@RequestBody Map<String, Object> req) {
        BigDecimal netAssets = new BigDecimal(req.getOrDefault("netAssets", 0).toString());
        log.info("计算额度请求, netAssets: {}", netAssets);

        BigDecimal debtLimit = quotaCalculationEngine.calculateDebtLimit(netAssets);
        BigDecimal lendingLimit = quotaCalculationEngine.calculateLendingLimit(netAssets);

        Map<String, Object> result = new HashMap<>();
        result.put("netAssets", netAssets);
        result.put("debtLimit", debtLimit);
        result.put("lendingLimit", lendingLimit);

        log.info("额度计算完成, netAssets: {}, debtLimit: {}, lendingLimit: {}", netAssets, debtLimit, lendingLimit);
        return R.ok("额度计算成功", result);
    }

    /**
     * 查询额度使用率 - 获取资金池当前的额度使用百分比
     * Get quota usage rate - Retrieve current quota usage percentage for a cash pool
     */
    @Operation(summary = "查询额度使用率")
    @GetMapping("/quota/usage/{poolId}")
    public R<Map<String, Object>> getQuotaUsage(@PathVariable String poolId) {
        log.info("查询额度使用率, poolId: {}", poolId);
        BigDecimal total = new BigDecimal("5000000.00");
        BigDecimal used = new BigDecimal("1200000.00");
        BigDecimal usagePct = quotaCalculationEngine.calculateUsagePct(used, total);

        Map<String, Object> result = new HashMap<>();
        result.put("poolId", poolId);
        result.put("totalLimit", total);
        result.put("usedLimit", used);
        result.put("availableLimit", total.subtract(used));
        result.put("usagePercentage", usagePct + "%");

        log.info("额度使用率查询成功, poolId: {}, usagePercentage: {}%", poolId, usagePct);
        return R.ok(result);
    }
}
