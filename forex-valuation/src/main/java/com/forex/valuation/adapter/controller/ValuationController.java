package com.forex.valuation.adapter.controller;

import com.forex.common.base.result.R;
import com.forex.valuation.adapter.dto.PnlAttributionResp;
import com.forex.valuation.adapter.dto.ValuationResp;
import com.forex.valuation.application.command.CalculateValuationCmd;
import com.forex.valuation.application.service.ValuationAppService;
import com.forex.valuation.domain.model.aggregate.PnlAttribution;
import com.forex.valuation.domain.model.aggregate.ValuationResult;
import com.forex.valuation.domain.model.valueobject.ValuationInput;
import com.forex.valuation.domain.model.valueobject.ValuationModelType;
import com.forex.valuation.domain.repository.PnlAttributionRepository;
import com.forex.valuation.domain.service.PnlAttributionService;

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
    private final PnlAttributionService pnlAttributionService;
    private final PnlAttributionRepository pnlAttributionRepository;

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

    @Operation(summary = "计算损益归因")
    @PostMapping("/pnl/attribution")
    public R<PnlAttributionResp> calculateAttribution(@RequestBody CalculateValuationCmd cmd) {
        LocalDate today = cmd.getValuationDate() != null ? cmd.getValuationDate() : LocalDate.now();
        LocalDate yesterday = today.minusDays(1);

        ValuationInput todayInput = ValuationInput.builder()
                .tradeId(cmd.getTradeId())
                .valuationDate(today)
                .notionalAmount(java.math.BigDecimal.valueOf(100000))
                .spotRate(java.math.BigDecimal.valueOf(7.25))
                .strikePrice(java.math.BigDecimal.valueOf(7.20))
                .timeToMaturity(0.5)
                .callPut("CALL")
                .build();

        ValuationInput yesterdayInput = ValuationInput.builder()
                .tradeId(cmd.getTradeId())
                .valuationDate(yesterday)
                .notionalAmount(java.math.BigDecimal.valueOf(100000))
                .spotRate(java.math.BigDecimal.valueOf(7.20))
                .strikePrice(java.math.BigDecimal.valueOf(7.20))
                .timeToMaturity(0.5 + 1.0 / 365.0)
                .callPut("CALL")
                .build();

        PnlAttribution attr = pnlAttributionService.calculateAttribution(
                todayInput, yesterdayInput, ValuationModelType.GARMAN_KOHLHAGEN);
        PnlAttribution saved = pnlAttributionRepository.save(attr);
        return R.ok("归因计算成功", toPnlAttributionResp(saved));
    }

    @Operation(summary = "查询损益归因")
    @GetMapping("/pnl/attribution/{tradeId}")
    public R<List<PnlAttributionResp>> getAttributions(@PathVariable Long tradeId) {
        List<PnlAttribution> attributions = pnlAttributionRepository.findByTradeId(tradeId);
        List<PnlAttributionResp> respList = attributions.stream()
                .map(this::toPnlAttributionResp)
                .toList();
        return R.ok(respList);
    }

    private PnlAttributionResp toPnlAttributionResp(PnlAttribution attr) {
        PnlAttributionResp resp = new PnlAttributionResp();
        resp.setId(attr.getId());
        resp.setAttribNo(attr.getAttribNo());
        resp.setTradeId(attr.getTradeId());
        resp.setTradeNo(attr.getTradeNo());
        resp.setAttribDate(attr.getAttribDate());
        resp.setTotalPnl(attr.getTotalPnl());
        resp.setDeltaPnl(attr.getDeltaPnl());
        resp.setThetaPnl(attr.getThetaPnl());
        resp.setGammaPnl(attr.getGammaPnl());
        resp.setVegaPnl(attr.getVegaPnl());
        resp.setCarryPnl(attr.getCarryPnl());
        resp.setTradePnl(attr.getTradePnl());
        resp.setTariffType(attr.getTariffType());
        resp.setTariffValue(attr.getTariffValue());
        return resp;
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
