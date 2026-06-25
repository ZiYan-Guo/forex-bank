package com.forex.clearing.adapter.controller;

import com.forex.clearing.application.service.SettlementTrackerService;
import com.forex.clearing.domain.model.aggregate.SettlementTracker;
import com.forex.clearing.domain.repository.SettlementTrackerRepository;
import com.forex.common.base.result.R;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;

import lombok.RequiredArgsConstructor;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Map;
import com.forex.common.security.annotation.RequirePermission;

@Tag(name = "结算追踪")
@RestController
@RequestMapping("/api/clearing/tracker")
@RequiredArgsConstructor
public class TrackerController {

    private final SettlementTrackerService trackerService;
    private final SettlementTrackerRepository trackerRepository;

    @Operation(summary = "创建结算追踪")
    @RequirePermission("clearing:create")
    @PostMapping("/create")
    public R<SettlementTracker> create(@RequestBody Map<String, Object> req) {
        String paymentNo = (String) req.get("paymentNo");
        String instructionNo = (String) req.get("instructionNo");
        String channel = (String) req.get("channel");
        SettlementTracker tracker = trackerService.createTracker(paymentNo, instructionNo, channel);
        return R.ok("追踪已创建", tracker);
    }

    @Operation(summary = "更新追踪状态")
    @RequirePermission("clearing:status")
    @PutMapping("/{trackingId}/status")
    public R<Void> updateStatus(@PathVariable String trackingId,
                                 @RequestBody Map<String, Object> req) {
        String newStatus = (String) req.get("status");
        trackerService.updateStatus(trackingId, newStatus);
        return R.okMsg("状态已更新");
    }

    @Operation(summary = "获取追踪详情")
    @GetMapping("/{trackingId}")
    public R<SettlementTracker> getDetail(@PathVariable String trackingId) {
        SettlementTracker tracker = trackerRepository.findByTrackingId(trackingId)
                .orElseThrow(() -> new IllegalArgumentException("追踪记录不存在"));
        return R.ok(tracker);
    }

    @Operation(summary = "查询逾期结算")
    @GetMapping("/overdue")
    public R<List<SettlementTracker>> overdue() {
        return R.ok(trackerService.findOverdue());
    }
}
