package com.forex.clearing.adapter.controller;

import com.forex.clearing.adapter.dto.ClearingResp;
import com.forex.clearing.application.command.GenerateClearingCmd;
import com.forex.clearing.application.service.ClearingAppService;
import com.forex.clearing.domain.model.aggregate.ClearingInstruction;
import com.forex.clearing.adapter.dto.ClearingPageQuery;
import com.forex.clearing.domain.model.query.ClearingQuery;
import com.forex.common.base.annotation.Idempotent;
import com.forex.common.base.annotation.RedisLock;
import com.forex.common.base.dto.PageResp;
import com.forex.common.base.result.R;

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
import com.forex.common.security.annotation.RequirePermission;

@Tag(name = "清算管理")
@RestController
@RequestMapping("/api/clearing")
@RequiredArgsConstructor
public class ClearingController {

    private final ClearingAppService clearingAppService;

    @Operation(summary = "生成清算指令")
    @RequirePermission("clearing:generate")
    @PostMapping("/generate")
    @Idempotent(key = "#cmd.bizNo + '_clearing_generate'")
    public R<ClearingResp> generate(@Valid @RequestBody GenerateClearingCmd cmd) {
        ClearingInstruction instruction = clearingAppService.generateInstruction(cmd);
        return R.ok("清算指令已生成", toClearingResp(instruction));
    }

    @Operation(summary = "发送清算指令")
    @RequirePermission("clearing:send")
    @PostMapping("/send/{instructionNo}")
    @RedisLock(key = "#instructionNo")
    public R<Void> send(@PathVariable String instructionNo) {
        clearingAppService.sendInstruction(instructionNo);
        return R.okMsg("清算指令已发送");
    }

    @Operation(summary = "确认回执")
    @RequirePermission("clearing:ack")
    @PostMapping("/ack/{instructionNo}")
    @RedisLock(key = "#instructionNo")
    public R<Void> acknowledge(@PathVariable String instructionNo,
                                @RequestBody(required = false) String swiftRef) {
        clearingAppService.acknowledgeInstruction(instructionNo, swiftRef);
        return R.okMsg("回执已确认");
    }

    @Operation(summary = "结算")
    @RequirePermission("clearing:settle")
    @PostMapping("/settle/{instructionNo}")
    @RedisLock(key = "#instructionNo")
    public R<Void> settle(@PathVariable String instructionNo) {
        clearingAppService.settleInstruction(instructionNo);
        return R.okMsg("结算完成");
    }

    @Operation(summary = "取消清算指令")
    @RequirePermission("clearing:cancel")
    @PostMapping("/cancel/{instructionNo}")
    @RedisLock(key = "#instructionNo")
    public R<Void> cancel(@PathVariable String instructionNo,
                           @RequestBody(required = false) String reason) {
        clearingAppService.cancelInstruction(instructionNo, reason);
        return R.okMsg("清算指令已取消");
    }

    @Operation(summary = "查询清算指令详情")
    @GetMapping("/{instructionNo}")
    public R<ClearingResp> getDetail(@PathVariable String instructionNo) {
        ClearingInstruction instruction = clearingAppService.getInstructionDetail(instructionNo);
        return R.ok(toClearingResp(instruction));
    }

    @Operation(summary = "分页查询清算指令")
    @RequirePermission("clearing:page")
    @PostMapping("/page")
    public R<PageResp<ClearingResp>> pageQuery(@Valid @RequestBody ClearingPageQuery req) {
        ClearingQuery query = new ClearingQuery();
        query.setPageNum(req.getPageNum());
        query.setPageSize(req.getPageSize());
        query.setInstructionNo(req.getInstructionNo());
        query.setClearingChannel(req.getClearingChannel());
        query.setInstructionStatus(req.getInstructionStatus());
        query.setValueDate(req.getValueDate());
        query.setBizType(req.getBizType());
        query.setBizNo(req.getBizNo());
        query.setSettlementType(req.getSettlementType());
        query.setStartDate(req.getStartDate());
        query.setEndDate(req.getEndDate());
        PageResp<ClearingInstruction> page = clearingAppService.pageQuery(query);
        List<ClearingResp> respList = page.getRecords().stream()
                .map(this::toClearingResp)
                .toList();
        PageResp<ClearingResp> result = PageResp.of(
                page.getTotal(), respList, page.getPageNum(), page.getPageSize());
        return R.ok(result);
    }

    private ClearingResp toClearingResp(ClearingInstruction instruction) {
        ClearingResp resp = new ClearingResp();
        resp.setId(instruction.getId());
        resp.setInstructionNo(instruction.getInstructionNo());
        resp.setBizType(instruction.getBizType());
        resp.setBizNo(instruction.getBizNo());
        resp.setClearingChannel(instruction.getClearingChannel());
        resp.setNostroAccount(instruction.getNostroAccount());
        resp.setCounterPartyAccount(instruction.getCounterPartyAccount());
        resp.setPayCurrency(instruction.getPayCurrency());
        resp.setPayAmount(instruction.getPayAmount());
        resp.setReceiveCurrency(instruction.getReceiveCurrency());
        resp.setReceiveAmount(instruction.getReceiveAmount());
        resp.setValueDate(instruction.getValueDate());
        resp.setSettlementDate(instruction.getSettlementDate());
        resp.setSettlementType(instruction.getSettlementType());
        resp.setInstructionStatus(instruction.getInstructionStatus());
        resp.setSwiftRef(instruction.getSwiftRef());
        resp.setCipsRef(instruction.getCipsRef());
        resp.setNostroBalanceBefore(instruction.getNostroBalanceBefore());
        resp.setNostroBalanceAfter(instruction.getNostroBalanceAfter());
        resp.setSendTime(instruction.getSendTime());
        resp.setAckTime(instruction.getAckTime());
        resp.setSettleTime(instruction.getSettleTime());
        resp.setOperatorId(instruction.getOperatorId());
        resp.setRemark(instruction.getRemark());
        return resp;
    }
}
