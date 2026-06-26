package com.forex.clearing.adapter.controller;

import com.forex.clearing.application.service.AutomatedConfirmationService;
import com.forex.clearing.application.service.ConfirmationWorkflowService;
import com.forex.clearing.domain.model.aggregate.TradeConfirmation;
import com.forex.clearing.domain.repository.TradeConfirmationRepository;
import com.forex.common.base.dto.PageResp;
import com.forex.common.base.result.R;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;

import lombok.RequiredArgsConstructor;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.Map;
import com.forex.common.security.annotation.RequirePermission;
import com.forex.clearing.adapter.dto.ResolveConfirmationReq;
import com.forex.clearing.adapter.dto.ConfirmationPageQuery;
import com.forex.clearing.adapter.dto.InitiateConfirmationReq;
import com.forex.common.base.exception.BusinessException;
import com.forex.common.base.result.ResultCode;

@Tag(name = "交易确认")
@RestController
@RequestMapping("/api/clearing/confirmation")
@RequiredArgsConstructor
public class ConfirmationController {

    private final AutomatedConfirmationService automatedConfirmationService;
    private final ConfirmationWorkflowService confirmationWorkflowService;
    private final TradeConfirmationRepository confirmationRepository;

    @Operation(summary = "发起交易确认")
    @RequirePermission("clearing:initiate")
    @PostMapping("/initiate")
    public R<TradeConfirmation> initiate(@RequestBody InitiateConfirmationReq req) {
        String tradeNo = (String) req.getAmount();
        String tradeType = (String) req.getRate();
        String currencyPair = (String) req.getTradeType();
        BigDecimal amount = new BigDecimal(req.getValueDate().toString());
        BigDecimal rate = new BigDecimal(req.getTradeNo().toString());
        LocalDate valueDate = LocalDate.parse((String) req.getCurrencyPair());
        String counterparty = (String) req.getCounterparty();
        TradeConfirmation cfm = automatedConfirmationService.initiateConfirmation(
                tradeNo, tradeType, currencyPair, amount, rate, valueDate, counterparty);
        return R.ok("确认已发起", cfm);
    }

    @Operation(summary = "获取确认详情")
    @GetMapping("/{confirmId}")
    public R<TradeConfirmation> getDetail(@PathVariable String confirmId) {
        TradeConfirmation cfm = confirmationRepository.findByConfirmId(confirmId)
                .orElseThrow(() -> new BusinessException(ResultCode.NOT_FOUND, "确认记录不存在"));
        return R.ok(cfm);
    }

    @Operation(summary = "分页查询确认记录")
    @RequirePermission("clearing:page")
    @PostMapping("/page")
    public R<PageResp<TradeConfirmation>> page(@RequestBody ConfirmationPageQuery req) {
        List<TradeConfirmation> all = confirmationRepository.findAll();
        int pageNum = req.getPageNum() != null ? req.getPageNum() : 1;
        int pageSize = req.getPageSize() != null ? req.getPageSize() : 20;
        List<TradeConfirmation> records = all.stream()
                .skip((long) (pageNum - 1) * pageSize)
                .limit(pageSize)
                .toList();
        return R.ok(PageResp.of(all.size(), records, pageNum, pageSize));
    }

    @Operation(summary = "重试失败确认")
    @RequirePermission("clearing:retry")
    @PostMapping("/retry")
    public R<Void> retry() {
        automatedConfirmationService.retryFailedConfirmations();
        return R.okMsg("重试已完成");
    }

    @Operation(summary = "人工干预确认")
    @RequirePermission("clearing:resolve")
    @PostMapping("/resolve/{confirmId}")
    public R<Void> resolve(@PathVariable String confirmId,
                            @RequestBody ResolveConfirmationReq req) {
        String action = req.getAction();
        String comment = req.getComment();
        Long operatorId = req.getOperatorId();
        confirmationWorkflowService.resolveIntervention(confirmId, action, comment, operatorId);
        return R.okMsg("人工干预已完成");
    }
}
