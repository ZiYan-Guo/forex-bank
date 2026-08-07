package com.forex.margin.adapter.controller;

import com.forex.common.base.annotation.Idempotent;
import com.forex.common.base.annotation.RedisLock;
import com.forex.common.base.dto.PageResp;
import com.forex.common.base.result.R;
import com.forex.margin.adapter.dto.CollateralLedgerSummaryResp;
import com.forex.margin.adapter.dto.CollateralValuationReq;
import com.forex.margin.adapter.dto.CollateralValuationResp;
import com.forex.margin.adapter.dto.InitialMarginCalcReq;
import com.forex.margin.adapter.dto.InitialMarginCalcResp;
import com.forex.margin.adapter.dto.MarginResp;
import com.forex.margin.adapter.dto.VmMarginCalcReq;
import com.forex.margin.adapter.dto.VmMarginCalcResp;
import com.forex.margin.application.command.CreateMarginCmd;
import com.forex.margin.application.service.MarginCalculationService;
import com.forex.margin.application.service.MarginAppService;
import com.forex.margin.domain.model.aggregate.MarginAccount;
import com.forex.margin.adapter.dto.MarginPageQuery;
import com.forex.margin.domain.model.query.MarginQuery;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;

import jakarta.validation.Valid;

import lombok.RequiredArgsConstructor;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.math.BigDecimal;
import java.util.List;
import com.forex.common.security.annotation.RequirePermission;

@Tag(name = "保证金管理")
@RestController
@RequestMapping("/api/margin")
@RequiredArgsConstructor
public class MarginController {

    private final MarginAppService marginAppService;
    private final MarginCalculationService marginCalculationService;

    @Operation(summary = "创建初始保证金")
    @RequirePermission("margin:create")
    @PostMapping("/create")
    @Idempotent(key = "#cmd.customerId + '_margin_create'")
    public R<MarginResp> createInitialMargin(@Valid @RequestBody CreateMarginCmd cmd) {
        MarginAccount account = marginAppService.createInitialMargin(
                cmd.getCustomerId(), cmd.getTradeId(), cmd.getNotionalAmount(), cmd.getMarginRate());
        return R.ok("创建成功", toMarginResp(account));
    }

    @Operation(summary = "追加保证金")
    @RequirePermission("margin:call")
    @PostMapping("/call")
    @RedisLock(key = "'margin:call:'+#marginNo")
    @Idempotent(key = "#marginNo + '_call'")
    public R<MarginResp> callMargin(@RequestParam String marginNo, @RequestParam BigDecimal amount) {
        MarginAccount account = marginAppService.callMargin(marginNo, amount);
        return R.ok(toMarginResp(account));
    }

    @Operation(summary = "释放保证金")
    @RequirePermission("margin:release")
    @PostMapping("/release")
    @RedisLock(key = "'margin:release:'+#marginNo")
    @Idempotent(key = "#marginNo + '_release'")
    public R<MarginResp> releaseMargin(@RequestParam String marginNo,
                                        @RequestParam BigDecimal amount,
                                        @RequestParam String reason) {
        MarginAccount account = marginAppService.releaseMargin(marginNo, amount, reason);
        return R.ok(toMarginResp(account));
    }

    @Operation(summary = "存入保证金")
    @RequirePermission("margin:deposit")
    @PostMapping("/deposit")
    @RedisLock(key = "'margin:deposit:'+#marginNo")
    @Idempotent(key = "#marginNo + '_deposit'")
    public R<MarginResp> depositMargin(@RequestParam String marginNo, @RequestParam BigDecimal amount) {
        MarginAccount account = marginAppService.depositMargin(marginNo, amount);
        return R.ok(toMarginResp(account));
    }

    @Operation(summary = "查询保证金详情")
    @GetMapping("/{marginNo}")
    public R<MarginResp> getMarginDetail(@PathVariable String marginNo) {
        MarginAccount account = marginAppService.getMarginDetail(marginNo);
        return R.ok(toMarginResp(account));
    }

    @Operation(summary = "分页查询保证金")
    @RequirePermission("margin:page")
    @PostMapping("/page")
    public R<PageResp<MarginResp>> pageQuery(@RequestBody MarginPageQuery req) {
        MarginQuery query = new MarginQuery();
        query.setPageNum(req.getPageNum());
        query.setPageSize(req.getPageSize());
        query.setCustomerId(req.getCustomerId());
        query.setTradeId(req.getTradeId());
        query.setMarginNo(req.getMarginNo());
        query.setMarginType(req.getMarginType());
        query.setStatus(req.getStatus());
        PageResp<MarginAccount> page = marginAppService.pageQuery(query);
        List<MarginResp> respList = page.getRecords().stream()
                .map(this::toMarginResp)
                .toList();
        PageResp<MarginResp> result = PageResp.of(page.getTotal(), respList, page.getPageNum(), page.getPageSize());
        return R.ok(result);
    }

    @Operation(summary = "变动保证金计量")
    @RequirePermission("margin:calculate")
    @PostMapping("/calculate/vm")
    public R<VmMarginCalcResp> calculateVariationMargin(@Valid @RequestBody VmMarginCalcReq req) {
        return R.ok(marginCalculationService.calculateVariationMargin(req));
    }

    @Operation(summary = "初始保证金标准法计量")
    @RequirePermission("margin:calculate")
    @PostMapping("/calculate/im-standard")
    public R<InitialMarginCalcResp> calculateStandardInitialMargin(@Valid @RequestBody InitialMarginCalcReq req) {
        return R.ok(marginCalculationService.calculateStandardInitialMargin(req));
    }

    @Operation(summary = "押品估值")
    @RequirePermission("margin:collateral")
    @PostMapping("/collateral/valuation")
    public R<CollateralValuationResp> valueCollateral(@Valid @RequestBody CollateralValuationReq req) {
        return R.ok(marginCalculationService.valueCollateral(req));
    }

    @Operation(summary = "押品台账汇总")
    @RequirePermission("margin:collateral")
    @GetMapping("/collateral/ledger-summary")
    public R<CollateralLedgerSummaryResp> summarizeCollateralLedger() {
        return R.ok(marginAppService.summarizeCollateralLedger());
    }

    private MarginResp toMarginResp(MarginAccount account) {
        MarginResp resp = new MarginResp();
        resp.setId(account.getId());
        resp.setMarginNo(account.getMarginNo());
        resp.setCustomerId(account.getCustomerId());
        resp.setTradeId(account.getTradeId());
        resp.setMarginType(account.getMarginType());
        resp.setMarginCurrency(account.getMarginCurrency());
        resp.setRequiredAmount(account.getRequiredAmount());
        resp.setDepositedAmount(account.getDepositedAmount());
        resp.setShortfallAmount(account.getShortfallAmount());
        resp.setMarginRate(account.getMarginRate());
        resp.setCallDate(account.getCallDate());
        resp.setDueDate(account.getDueDate());
        resp.setStatus(account.getStatus());
        resp.setCollateralType(account.getCollateralType());
        resp.setCollateralValue(account.getCollateralValue());
        resp.setWaterLevel(account.getWaterLevel());
        resp.setReleaseReason(account.getReleaseReason());
        return resp;
    }
}
