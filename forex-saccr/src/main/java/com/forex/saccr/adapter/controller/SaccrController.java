package com.forex.saccr.adapter.controller;

import com.forex.common.base.dto.PageResp;
import com.forex.common.base.result.R;
import com.forex.saccr.adapter.dto.CalculateCmd;
import com.forex.saccr.adapter.dto.SimmCalculateCmd;
import com.forex.saccr.adapter.dto.SaccrResultResp;
import com.forex.saccr.adapter.dto.SimmResultResp;
import com.forex.saccr.application.service.SaccrAppService;
import com.forex.saccr.domain.model.aggregate.SaccrResult;
import com.forex.saccr.domain.model.aggregate.SimmResult;
import com.forex.saccr.domain.model.query.SaccrQuery;
import com.forex.saccr.domain.model.query.SimmQuery;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;

import lombok.RequiredArgsConstructor;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@Tag(name = "SA-CCR计量")
@RestController
@RequestMapping("/api/saccr")
@RequiredArgsConstructor
public class SaccrController {

    private final SaccrAppService saccrAppService;

    @Operation(summary = "计算SA-CCR风险敞口")
    @PostMapping("/calculate")
    public R<SaccrResultResp> calculate(@RequestBody CalculateCmd cmd) {
        SaccrResult result = saccrAppService.calculateSaccr(cmd);
        return R.ok(toSaccrResultResp(result));
    }

    @Operation(summary = "计算ISDA SIMM保证金")
    @PostMapping("/simm/calculate")
    public R<SimmResultResp> calculateSimm(@RequestBody SimmCalculateCmd cmd) {
        SimmResult result = saccrAppService.calculateSimm(cmd);
        return R.ok(toSimmResultResp(result));
    }

    @Operation(summary = "查询SA-CCR计算结果")
    @GetMapping("/result/{calcNo}")
    public R<SaccrResultResp> getResult(@PathVariable String calcNo) {
        SaccrResult result = saccrAppService.getSaccrResult(calcNo);
        return R.ok(toSaccrResultResp(result));
    }

    @Operation(summary = "查询ISDA SIMM计算结果")
    @GetMapping("/simm/result/{calcNo}")
    public R<SimmResultResp> getSimmResult(@PathVariable String calcNo) {
        SimmResult result = saccrAppService.getSimmResult(calcNo);
        return R.ok(toSimmResultResp(result));
    }

    @Operation(summary = "分页查询SA-CCR结果")
    @PostMapping("/result/page")
    public R<PageResp<SaccrResultResp>> pageQuery(@RequestBody SaccrQuery query) {
        PageResp<SaccrResult> page = saccrAppService.pageQuerySaccr(query);
        List<SaccrResultResp> respList = page.getRecords().stream()
                .map(this::toSaccrResultResp)
                .toList();
        return R.ok(PageResp.of(page.getTotal(), respList, page.getPageNum(), page.getPageSize()));
    }

    @Operation(summary = "分页查询SIMM结果")
    @PostMapping("/simm/result/page")
    public R<PageResp<SimmResultResp>> pageQuerySimm(@RequestBody SimmQuery query) {
        PageResp<SimmResult> page = saccrAppService.pageQuerySimm(query);
        List<SimmResultResp> respList = page.getRecords().stream()
                .map(this::toSimmResultResp)
                .toList();
        return R.ok(PageResp.of(page.getTotal(), respList, page.getPageNum(), page.getPageSize()));
    }

    private SaccrResultResp toSaccrResultResp(SaccrResult result) {
        SaccrResultResp resp = new SaccrResultResp();
        resp.setId(result.getId());
        resp.setCalcNo(result.getCalcNo());
        resp.setTradeId(result.getTradeId());
        resp.setTradeNo(result.getTradeNo());
        resp.setCounterPartyId(result.getCounterPartyId());
        resp.setCalcDate(result.getCalcDate());
        resp.setRc(result.getRc());
        resp.setPfe(result.getPfe());
        resp.setExposure(result.getExposure());
        resp.setAlpha(result.getAlpha());
        resp.setCalcMethod(result.getCalcMethod());
        resp.setResultJson(result.getResultJson());
        return resp;
    }

    private SimmResultResp toSimmResultResp(SimmResult result) {
        SimmResultResp resp = new SimmResultResp();
        resp.setId(result.getId());
        resp.setCalcNo(result.getCalcNo());
        resp.setTradeId(result.getTradeId());
        resp.setTradeNo(result.getTradeNo());
        resp.setCalcDate(result.getCalcDate());
        resp.setNotionalAmount(result.getNotionalAmount());
        resp.setDeltaMargin(result.getDeltaMargin());
        resp.setVegaMargin(result.getVegaMargin());
        resp.setCurvatureMargin(result.getCurvatureMargin());
        resp.setTotalMargin(result.getTotalMargin());
        resp.setCalcMethod(result.getCalcMethod());
        resp.setSensitivitiesJson(result.getSensitivitiesJson());
        return resp;
    }
}
