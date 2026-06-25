package com.forex.clearing.adapter.controller;

import com.forex.clearing.domain.model.aggregate.ClsSession;
import com.forex.clearing.domain.model.aggregate.PvpSettlementPair;
import com.forex.clearing.domain.model.valueobject.NettingPosition;
import com.forex.clearing.domain.service.ClsSessionService;
import com.forex.clearing.domain.service.NettingService;
import com.forex.clearing.domain.service.PvpSettlementService;
import com.forex.clearing.domain.repository.ClsSessionRepository;
import com.forex.clearing.domain.repository.PvpSettlementRepository;
import com.forex.common.base.result.R;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;

import lombok.RequiredArgsConstructor;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;
import com.forex.common.security.annotation.RequirePermission;

@Tag(name = "PVP结算")
@RestController
@RequestMapping("/api/clearing/pvp")
@RequiredArgsConstructor
public class PvpController {

    private final PvpSettlementService pvpSettlementService;
    private final ClsSessionService clsSessionService;
    private final NettingService nettingService;
    private final PvpSettlementRepository pvpSettlementRepository;
    private final ClsSessionRepository clsSessionRepository;

    @Operation(summary = "创建PVP结算对")
    @RequirePermission("clearing:create")
    @PostMapping("/pair/create")
    public R<PvpSettlementPair> createPvpPair(Long payInstructionId, Long receiveInstructionId) {
        PvpSettlementPair pair = pvpSettlementService.createPvpPair(payInstructionId, receiveInstructionId);
        return R.ok("PVP结算对已创建", pair);
    }

    @Operation(summary = "执行PVP结算")
    @RequirePermission("clearing:settle")
    @PostMapping("/pair/{pairId}/settle")
    public R<Void> executePvpSettlement(@PathVariable String pairId) {
        pvpSettlementService.executePvpSettlement(pairId);
        return R.okMsg("PVP结算已执行");
    }

    @Operation(summary = "查询PVP结算对详情")
    @GetMapping("/pair/{pairId}")
    public R<PvpSettlementPair> getPvpPairDetail(@PathVariable String pairId) {
        PvpSettlementPair pair = pvpSettlementRepository.findByPairId(pairId)
                .orElseThrow(() -> new IllegalArgumentException("PVP pair not found"));
        return R.ok(pair);
    }

    @Operation(summary = "排期CLS结算场次")
    @RequirePermission("clearing:schedule")
    @PostMapping("/cls/session/schedule")
    public R<ClsSession> scheduleClsSession() {
        ClsSession session = clsSessionService.scheduleSession(LocalDate.now());
        return R.ok("CLS场次已排期", session);
    }

    @Operation(summary = "开启Pay-In窗口")
    @RequirePermission("clearing:open")
    @PostMapping("/cls/session/{sessionId}/open")
    public R<Void> openPayInWindow(@PathVariable String sessionId) {
        clsSessionService.openPayInWindow(sessionId);
        return R.okMsg("Pay-In窗口已开启");
    }

    @Operation(summary = "关闭Pay-In窗口")
    @RequirePermission("clearing:close")
    @PostMapping("/cls/session/{sessionId}/close")
    public R<Void> closePayInWindow(@PathVariable String sessionId) {
        clsSessionService.closePayInWindow(sessionId);
        return R.okMsg("Pay-In窗口已关闭");
    }

    @Operation(summary = "查询今日CLS场次")
    @GetMapping("/cls/session/today")
    public R<ClsSession> getTodaySession() {
        ClsSession session = clsSessionRepository.findBySettlementDate(LocalDate.now())
                .orElseThrow(() -> new IllegalArgumentException("No CLS session for today"));
        return R.ok(session);
    }

    @Operation(summary = "计算双边净额")
    @RequirePermission("clearing:calculate")
    @PostMapping("/netting/calculate")
    public R<List<NettingPosition>> calculateBilateralNetting(List<Long> instructionIds) {
        List<NettingPosition> positions = List.of();
        return R.ok(positions);
    }
}
