package com.forex.position.adapter.controller;

import com.forex.common.base.dto.PageResp;
import com.forex.common.base.result.R;
import com.forex.position.adapter.dto.PositionResp;
import com.forex.position.application.command.PositionCmd;
import com.forex.position.application.service.ExposureAnalysisService;
import com.forex.position.application.service.PositionAppService;
import com.forex.position.domain.model.aggregate.Position;
import com.forex.position.domain.model.query.PositionQuery;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;

import lombok.RequiredArgsConstructor;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.Map;
import com.forex.common.security.annotation.RequirePermission;

@Tag(name = "敞口管理")
@RestController
@RequestMapping("/api/position")
@RequiredArgsConstructor
public class PositionController {

    private final PositionAppService positionAppService;
    private final ExposureAnalysisService exposureAnalysisService;

    @Operation(summary = "创建头寸")
    @RequirePermission("position:create")
    @PostMapping("/create")
    public R<PositionResp> createPosition(@RequestBody PositionCmd cmd) {
        Position position = positionAppService.createPosition(cmd);
        return R.ok("创建成功", toPositionResp(position));
    }

    @Operation(summary = "更新头寸")
    @RequirePermission("position:update")
    @PutMapping("/update/{id}")
    public R<PositionResp> updatePosition(@PathVariable Long id,
                                           @RequestParam(required = false) BigDecimal longAmt,
                                           @RequestParam(required = false) BigDecimal shortAmt) {
        Position position = positionAppService.updatePosition(id, longAmt, shortAmt);
        return R.ok(toPositionResp(position));
    }

    @Operation(summary = "查询头寸详情")
    @GetMapping("/{positionNo}")
    public R<PositionResp> getPositionDetail(@PathVariable String positionNo) {
        Position position = positionAppService.getPositionDetail(positionNo);
        return R.ok(toPositionResp(position));
    }

    @Operation(summary = "分页查询头寸")
    @RequirePermission("position:page")
    @PostMapping("/page")
    public R<PageResp<PositionResp>> pageQuery(@RequestBody PositionQuery query) {
        PageResp<Position> page = positionAppService.pageQuery(query);
        List<PositionResp> respList = page.getRecords().stream()
                .map(this::toPositionResp)
                .toList();
        PageResp<PositionResp> result = PageResp.of(page.getTotal(), respList, page.getPageNum(), page.getPageSize());
        return R.ok(result);
    }

    @Operation(summary = "汇总头寸")
    @RequirePermission("position:aggregate")
    @PostMapping("/aggregate")
    public R<PositionResp> aggregatePositions(@RequestParam LocalDate date,
                                               @RequestParam String ccyPair) {
        Position position = positionAppService.aggregatePositions(date, ccyPair);
        return R.ok(toPositionResp(position));
    }

    @Operation(summary = "检查头寸限额")
    @GetMapping("/breach/check")
    public R<Void> checkBreach() {
        positionAppService.checkBreach();
        return R.okMsg("限额检查完成");
    }

    @Operation(summary = "多维度敞口分析")
    @RequirePermission("position:multi-dim")
    @PostMapping("/analysis/multi-dim")
    public R<ExposureAnalysisService.ExposureAnalysisResult> multiDimAnalysis(
            @RequestBody Map<String, Object> request) {
        LocalDate date = LocalDate.parse((String) request.get("date"));
        @SuppressWarnings("unchecked")
        List<String> dimensions = (List<String>) request.get("dimensions");
        @SuppressWarnings("unchecked")
        List<String> currencies = (List<String>) request.get("currencies");
        ExposureAnalysisService.ExposureAnalysisResult result = exposureAnalysisService.analyze(
                date,
                dimensions.toArray(new String[0]),
                currencies != null ? currencies.toArray(new String[0]) : null);
        return R.ok(result);
    }

    @Operation(summary = "到期日阶梯分析")
    @RequirePermission("position:maturity-ladder")
    @PostMapping("/analysis/maturity-ladder")
    public R<ExposureAnalysisService.MaturityLadder> maturityLadder(
            @RequestBody Map<String, Object> request) {
        LocalDate date = LocalDate.parse((String) request.get("date"));
        ExposureAnalysisService.MaturityLadder result = exposureAnalysisService.analyzeMaturityLadder(date);
        return R.ok(result);
    }

    @Operation(summary = "敞口热力图")
    @RequirePermission("position:heatmap")
    @PostMapping("/analysis/heatmap")
    public R<ExposureAnalysisService.HeatmapData> heatmap(
            @RequestBody Map<String, Object> request) {
        LocalDate date = LocalDate.parse((String) request.get("date"));
        ExposureAnalysisService.HeatmapData result = exposureAnalysisService.generateHeatmap(date);
        return R.ok(result);
    }

    private PositionResp toPositionResp(Position position) {
        PositionResp resp = new PositionResp();
        resp.setId(position.getId());
        resp.setPositionNo(position.getPositionNo());
        resp.setCurrencyPair(position.getCurrencyPair());
        resp.setPositionType(position.getPositionType());
        resp.setPositionCurrency(position.getPositionCurrency());
        resp.setLongAmount(position.getLongAmount());
        resp.setShortAmount(position.getShortAmount());
        resp.setNetPosition(position.getNetPosition());
        resp.setPositionLimit(position.getPositionLimit());
        resp.setLimitUsagePct(position.getLimitUsagePct());
        resp.setPositionDate(position.getPositionDate());
        resp.setTraderId(position.getTraderId());
        resp.setBranchCode(position.getBranchCode());
        resp.setRiskLevel(position.getRiskLevel());
        resp.setHedgingAction(position.getHedgingAction());
        return resp;
    }
}
