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

@Tag(name = "交易确认")
@RestController
@RequestMapping("/api/clearing/confirmation")
@RequiredArgsConstructor
public class ConfirmationController {

    private final AutomatedConfirmationService automatedConfirmationService;
    private final ConfirmationWorkflowService confirmationWorkflowService;
    private final TradeConfirmationRepository confirmationRepository;

    @Operation(summary = "发起交易确认")
    @PostMapping("/initiate")
    public R<TradeConfirmation> initiate(@RequestBody Map<String, Object> req) {
        String tradeNo = (String) req.get("tradeNo");
        String tradeType = (String) req.get("tradeType");
        String currencyPair = (String) req.get("currencyPair");
        BigDecimal amount = new BigDecimal(req.get("amount").toString());
        BigDecimal rate = new BigDecimal(req.get("rate").toString());
        LocalDate valueDate = LocalDate.parse((String) req.get("valueDate"));
        String counterparty = (String) req.get("counterparty");
        TradeConfirmation cfm = automatedConfirmationService.initiateConfirmation(
                tradeNo, tradeType, currencyPair, amount, rate, valueDate, counterparty);
        return R.ok("确认已发起", cfm);
    }

    @Operation(summary = "获取确认详情")
    @GetMapping("/{confirmId}")
    public R<TradeConfirmation> getDetail(@PathVariable String confirmId) {
        TradeConfirmation cfm = confirmationRepository.findByConfirmId(confirmId)
                .orElseThrow(() -> new IllegalArgumentException("确认记录不存在"));
        return R.ok(cfm);
    }

    @Operation(summary = "分页查询确认记录")
    @PostMapping("/page")
    public R<PageResp<TradeConfirmation>> page(@RequestBody Map<String, Object> req) {
        List<TradeConfirmation> all = confirmationRepository.findAll();
        int pageNum = req.get("pageNum") != null ? ((Number) req.get("pageNum")).intValue() : 1;
        int pageSize = req.get("pageSize") != null ? ((Number) req.get("pageSize")).intValue() : 20;
        List<TradeConfirmation> records = all.stream()
                .skip((long) (pageNum - 1) * pageSize)
                .limit(pageSize)
                .toList();
        return R.ok(PageResp.of(all.size(), records, pageNum, pageSize));
    }

    @Operation(summary = "重试失败确认")
    @PostMapping("/retry")
    public R<Void> retry() {
        automatedConfirmationService.retryFailedConfirmations();
        return R.okMsg("重试已完成");
    }

    @Operation(summary = "人工干预确认")
    @PostMapping("/resolve/{confirmId}")
    public R<Void> resolve(@PathVariable String confirmId,
                            @RequestBody Map<String, Object> req) {
        String action = (String) req.get("action");
        String comment = (String) req.get("comment");
        Long operatorId = req.get("operatorId") != null
                ? ((Number) req.get("operatorId")).longValue() : 0L;
        confirmationWorkflowService.resolveIntervention(confirmId, action, comment, operatorId);
        return R.okMsg("人工干预已完成");
    }
}
