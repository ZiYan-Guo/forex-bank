package com.forex.valuation.adapter.controller;

import com.forex.common.base.result.R;
import com.forex.valuation.adapter.dto.ValuationResp;
import com.forex.valuation.application.command.CalculateValuationCmd;
import com.forex.valuation.application.service.ValuationAppService;
import com.forex.valuation.domain.model.aggregate.ValuationResult;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;

import lombok.RequiredArgsConstructor;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDate;
import java.util.List;

@Tag(name = "衍生品估值")
@RestController
@RequestMapping("/api/valuation")
@RequiredArgsConstructor
public class ValuationController {

    private final ValuationAppService valuationAppService;

    @Operation(summary = "查询交易估值记录")
    @GetMapping("/trade/{tradeId}")
    public R<List<ValuationResp>> getValuations(@PathVariable Long tradeId) {
        List<ValuationResult> valuations = valuationAppService.getTradeValuations(tradeId);
        List<ValuationResp> respList = valuations.stream()
                .map(this::toValuationResp)
                .toList();
        return R.ok(respList);
    }

    @Operation(summary = "计算估值")
    @PostMapping("/calculate")
    public R<ValuationResp> calculateValuation(@RequestBody CalculateValuationCmd cmd) {
        ValuationResult result = valuationAppService.calculateValuation(cmd.getTradeId());
        return R.ok("计算成功", toValuationResp(result));
    }

    @Operation(summary = "重新计算所有估值")
    @PostMapping("/recalculate/{date}")
    public R<Void> recalculate(@PathVariable LocalDate date) {
        valuationAppService.recalculateAll(date);
        return R.okMsg("重算已触发");
    }

    private ValuationResp toValuationResp(ValuationResult result) {
        ValuationResp resp = new ValuationResp();
        resp.setId(result.getId());
        resp.setTradeId(result.getTradeId());
        resp.setTradeNo(result.getTradeNo());
        resp.setTradeType(result.getTradeType());
        resp.setValuationDate(result.getValuationDate());
        resp.setCurrencyPair(result.getCurrencyPair());
        resp.setNotionalAmount(result.getNotionalAmount());
        resp.setFairValue(result.getFairValue());
        resp.setPnl(result.getPnl());
        resp.setCumulativePnl(result.getCumulativePnl());
        resp.setValuationMethod(result.getValuationMethod());
        resp.setModelParams(result.getModelParams());
        resp.setMarketDataSnapshot(result.getMarketDataSnapshot());
        return resp;
    }
}
