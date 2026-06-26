package com.forex.clearing.adapter.controller;

import com.forex.clearing.application.service.CipsMessageGenerator;
import com.forex.clearing.application.service.CipsRoutingService;
import com.forex.common.base.result.R;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.math.BigDecimal;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;
import com.forex.common.security.annotation.RequirePermission;
import com.forex.clearing.adapter.dto.Cips113Req;
import com.forex.clearing.adapter.dto.Cips112Req;
import com.forex.clearing.adapter.dto.Cips111Req;

/**
 * CIPS management controller providing message generation, routing lookup and statistics.
 * CIPS 管理控制器，提供报文生成、路由查询和统计功能。
 */
@Slf4j
@Tag(name = "CIPS管理")
@RestController
@RequestMapping("/api/clearing/cips")
@RequiredArgsConstructor
public class CipsController {

    private final CipsMessageGenerator cipsMessageGenerator;
    private final CipsRoutingService cipsRoutingService;

    /**
     * Generate CIPS.111 Customer Credit Transfer message.
     * 生成 CIPS.111 客户汇款报文。
     */
    @Operation(summary = "生成CIPS.111客户汇款报文")
    @RequirePermission("clearing:111")
    @PostMapping("/message/generate/111")
    public R<String> generateCips111(@Valid @RequestBody Cips111Req req) {
        log.info("POST /message/generate/111 start: {}", req);
        String msgId = req.getMsgId() != null ? req.getMsgId() : UUID.randomUUID().toString();
        String debtorName = req.getDebtorName();
        String debtorAcct = req.getDebtorAcct();
        String creditorName = req.getCreditorName();
        String creditorAcct = req.getCreditorAcct();
        String creditorCipsId = req.getCreditorCipsId();
        String currency = req.getCurrency() != null ? req.getCurrency() : "CNY";
        BigDecimal amount = req.getAmount() != null ? req.getAmount() : BigDecimal.ZERO;
        String remittanceInfo = req.getRemittanceInfo() != null ? req.getRemittanceInfo() : "";
        String message = cipsMessageGenerator.generateCips111(msgId, debtorName, debtorAcct,
                creditorName, creditorAcct, creditorCipsId, currency, amount, remittanceInfo);
        log.info("POST /message/generate/111 completed: msgId={}", msgId);
        return R.ok(message);
    }

    /**
     * Generate CIPS.112 Payment Status Report.
     * 生成 CIPS.112 支付状态报告。
     */
    @Operation(summary = "生成CIPS.112支付状态报告")
    @RequirePermission("clearing:112")
    @PostMapping("/message/generate/112")
    public R<String> generateCips112(@Valid @RequestBody Cips112Req req) {
        log.info("POST /message/generate/112 start: {}", req);
        String originalMsgId = req.getOriginalMsgId();
        String status = req.getStatus() != null ? req.getStatus() : "ACSC";
        String reason = req.getReason() != null ? req.getReason() : "AC01";
        String message = cipsMessageGenerator.generateCips112(originalMsgId, status, reason);
        log.info("POST /message/generate/112 completed: originalMsgId={}, status={}", originalMsgId, status);
        return R.ok(message);
    }

    /**
     * Generate CIPS.113 Payment Return message.
     * 生成 CIPS.113 退汇报文。
     */
    @Operation(summary = "生成CIPS.113退汇报文")
    @RequirePermission("clearing:113")
    @PostMapping("/message/generate/113")
    public R<String> generateCips113(@Valid @RequestBody Cips113Req req) {
        log.info("POST /message/generate/113 start: {}", req);
        String originalMsgId = req.getOriginalMsgId();
        String returnReason = req.getReturnReason() != null ? req.getReturnReason() : "AC01";
        BigDecimal returnAmount = req.getReturnAmount() != null ? req.getReturnAmount() : BigDecimal.ZERO;
        String message = cipsMessageGenerator.generateCips113(originalMsgId, returnReason, returnAmount);
        log.info("POST /message/generate/113 completed: originalMsgId={}", originalMsgId);
        return R.ok(message);
    }

    /**
     * Lookup CIPS participant by BIC code.
     * 根据 BIC 码查询 CIPS 参与行。
     */
    @Operation(summary = "根据BIC码查询CIPS参与行")
    @GetMapping("/routing/lookup/{bic}")
    public R<Map<String, Object>> lookupByBic(@PathVariable String bic) {
        log.info("GET /routing/lookup/{}", bic);
        String cipsId = cipsRoutingService.resolveByBic(bic);
        Map<String, Object> result = new LinkedHashMap<>();
        result.put("bic", bic);
        result.put("cipsId", cipsId);
        result.put("isParticipant", cipsId != null);
        log.info("Routing lookup result: BIC={} → {}", bic, cipsId);
        return R.ok(result);
    }

    /**
     * List all registered CIPS participants.
     * 获取所有已注册 CIPS 参与行列表。
     */
    @Operation(summary = "获取所有CIPS参与行列表")
    @GetMapping("/routing/participants")
    public R<Map<String, Object>> listParticipants() {
        log.info("GET /routing/participants");
        Map<String, Object> result = new LinkedHashMap<>();
        result.put("count", cipsRoutingService.getParticipantCount());
        result.put("participants", java.util.List.of(
                Map.of("bic", "BKCHCNBJ", "cipsId", "CIPS00001", "bankName", "Bank of China"),
                Map.of("bic", "ICBKCNBJ", "cipsId", "CIPS00002", "bankName", "ICBC"),
                Map.of("bic", "ABOCCNBJ", "cipsId", "CIPS00003", "bankName", "Agricultural Bank of China"),
                Map.of("bic", "MSBCCNBJ", "cipsId", "CIPS00004", "bankName", "China Construction Bank"),
                Map.of("bic", "PCBCCNBJ", "cipsId", "CIPS00005", "bankName", "China Postal Savings Bank"),
                Map.of("bic", "BOFACN3X", "cipsId", "CIPS00010", "bankName", "Bank of America"),
                Map.of("bic", "CITICNSX", "cipsId", "CIPS00011", "bankName", "CITIC Bank"),
                Map.of("bic", "HSBCCNSH", "cipsId", "CIPS00012", "bankName", "HSBC China")
        ));
        log.info("Listed {} CIPS participants", cipsRoutingService.getParticipantCount());
        return R.ok(result);
    }

    /**
     * Get CIPS business statistics (mocked data).
     * 获取 CIPS 业务统计数据（模拟数据）。
     */
    @Operation(summary = "获取CIPS业务统计数据")
    @GetMapping("/statistics")
    public R<Map<String, Object>> getStatistics() {
        log.info("GET /statistics");
        Map<String, Object> stats = new LinkedHashMap<>();
        // Mocked daily count and amount. 模拟每日笔数和金额。
        stats.put("dailyTransactionCount", 15680);
        stats.put("dailyAmountBillion", new BigDecimal("325.67"));
        stats.put("currency", "CNY");
        // Mocked channel share percentages. 模拟渠道占比。
        Map<String, Object> channelShare = new LinkedHashMap<>();
        channelShare.put("CIPS", "62.5%");
        channelShare.put("SWIFT", "28.3%");
        channelShare.put("CFXPS", "7.8%");
        channelShare.put("LOCAL", "1.4%");
        stats.put("channelShare", channelShare);
        stats.put("activeParticipants", cipsRoutingService.getParticipantCount());
        stats.put("averageProcessingTimeMs", 120);
        log.info("Statistics returned: dailyCount={}, dailyAmount={} billion", stats.get("dailyTransactionCount"), stats.get("dailyAmountBillion"));
        return R.ok(stats);
    }
}
