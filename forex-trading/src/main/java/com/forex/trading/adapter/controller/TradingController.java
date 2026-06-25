package com.forex.trading.adapter.controller;

import com.forex.common.base.annotation.Idempotent;
import com.forex.common.base.annotation.RedisLock;
import com.forex.common.base.dto.PageResp;
import com.forex.common.base.result.R;
import com.forex.common.security.annotation.RequirePermission;
import com.forex.trading.adapter.dto.CreateTradeReq;
import com.forex.trading.adapter.dto.RollOverReq;
import com.forex.trading.adapter.dto.TradePageQuery;
import com.forex.trading.adapter.dto.TradeResp;
import com.forex.trading.application.command.CreateTradeCmd;
import com.forex.trading.application.service.TradingAppService;
import com.forex.trading.domain.model.aggregate.FxTrade;
import com.forex.trading.domain.model.query.TradeQuery;
import com.forex.trading.domain.model.query.TradeQuery;

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

@Tag(name = "外汇交易")
@RestController
@RequestMapping("/api/trading")
@RequiredArgsConstructor
public class TradingController {

    private final TradingAppService tradingAppService;

    @Operation(summary = "创建即期交易")
    @PostMapping("/spot")
    @RequirePermission("trading:create")
    @Idempotent(key = "#req.customerId + '_spot_' + #req.buyCurrency + #req.sellCurrency")
    public R<TradeResp> createSpot(@Valid @RequestBody CreateTradeReq req) {
        FxTrade trade = tradingAppService.createSpotTrade(toCmd(req));
        return R.ok(toTradeResp(trade));
    }

    @Operation(summary = "创建远期交易")
    @PostMapping("/forward")
    @RequirePermission("trading:create")
    @Idempotent(key = "#req.customerId + '_forward_' + #req.buyCurrency + #req.sellCurrency")
    public R<TradeResp> createForward(@Valid @RequestBody CreateTradeReq req) {
        FxTrade trade = tradingAppService.createForwardTrade(toCmd(req));
        return R.ok(toTradeResp(trade));
    }

    @Operation(summary = "创建掉期交易")
    @PostMapping("/swap")
    @RequirePermission("trading:create")
    @Idempotent(key = "#req.customerId + '_swap_' + #req.buyCurrency + #req.sellCurrency")
    public R<TradeResp> createSwap(@Valid @RequestBody CreateTradeReq req) {
        FxTrade trade = tradingAppService.createSwapTrade(toCmd(req));
        return R.ok(toTradeResp(trade));
    }

    @Operation(summary = "创建期权交易")
    @PostMapping("/option")
    @RequirePermission("trading:create")
    @Idempotent(key = "#req.customerId + '_option_' + #req.buyCurrency + #req.sellCurrency")
    public R<TradeResp> createOption(@Valid @RequestBody CreateTradeReq req) {
        FxTrade trade = tradingAppService.createOptionTrade(toCmd(req));
        return R.ok(toTradeResp(trade));
    }

    @Operation(summary = "查询交易详情")
    @GetMapping("/{tradeNo}")
    public R<TradeResp> getDetail(@PathVariable String tradeNo) {
        FxTrade trade = tradingAppService.getTradeDetail(tradeNo);
        return R.ok(toTradeResp(trade));
    }

    @Operation(summary = "分页查询交易")
    @PostMapping("/page")
    public R<PageResp<TradeResp>> pageQuery(@RequestBody TradePageQuery req) {
        TradeQuery query = toTradeQuery(req);
        PageResp<FxTrade> page = tradingAppService.pageQuery(query);
        PageResp<TradeResp> result = PageResp.of(
                page.getTotal(),
                page.getRecords().stream().map(this::toTradeResp).toList(),
                page.getPageNum(),
                page.getPageSize());
        return R.ok(result);
    }

    @Operation(summary = "确认交易")
    @PostMapping("/confirm/{tradeNo}")
    @RequirePermission("trading:confirm")
    @RedisLock(key = "#tradeNo")
    public R<TradeResp> confirm(@PathVariable String tradeNo) {
        tradingAppService.confirmTrade(tradeNo);
        FxTrade trade = tradingAppService.getTradeDetail(tradeNo);
        return R.ok("交易已确认", toTradeResp(trade));
    }

    @Operation(summary = "执行交易")
    @PostMapping("/execute/{tradeNo}")
    @RequirePermission("trading:execute")
    @RedisLock(key = "#tradeNo")
    public R<TradeResp> execute(@PathVariable String tradeNo) {
        tradingAppService.executeTrade(tradeNo);
        FxTrade trade = tradingAppService.getTradeDetail(tradeNo);
        return R.ok("交易已执行", toTradeResp(trade));
    }

    @Operation(summary = "结算交易")
    @PostMapping("/settle/{tradeNo}")
    @RequirePermission("trading:settle")
    @RedisLock(key = "#tradeNo")
    public R<TradeResp> settle(@PathVariable String tradeNo) {
        tradingAppService.settleTrade(tradeNo);
        FxTrade trade = tradingAppService.getTradeDetail(tradeNo);
        return R.ok("交易已结算", toTradeResp(trade));
    }

    @Operation(summary = "展期交易")
    @PostMapping("/roll-over")
    @RequirePermission("trading:roll-over")
    @RedisLock(key = "#req.tradeNo")
    public R<TradeResp> rollOver(@Valid @RequestBody RollOverReq req) {
        tradingAppService.rollOverTrade(req.getTradeNo(), req.getNewMaturityDate(), req.getNewRate());
        FxTrade trade = tradingAppService.getTradeDetail(req.getTradeNo());
        return R.ok("交易已展期", toTradeResp(trade));
    }

    @Operation(summary = "平仓交易")
    @PostMapping("/close/{tradeNo}")
    @RequirePermission("trading:close")
    @RedisLock(key = "#tradeNo")
    public R<TradeResp> closeOut(@PathVariable String tradeNo) {
        tradingAppService.closeOutTrade(tradeNo);
        FxTrade trade = tradingAppService.getTradeDetail(tradeNo);
        return R.ok("交易已平仓", toTradeResp(trade));
    }

    @Operation(summary = "取消交易")
    @PostMapping("/cancel/{tradeNo}")
    @RequirePermission("trading:cancel")
    @RedisLock(key = "#tradeNo")
    public R<Void> cancel(@PathVariable String tradeNo, @RequestParam String reason) {
        tradingAppService.cancelTrade(tradeNo, reason);
        return R.okMsg("交易已取消");
    }

    private TradeQuery toTradeQuery(TradePageQuery req) {
        TradeQuery query = new TradeQuery();
        query.setPageNum(req.getPageNum());
        query.setPageSize(req.getPageSize());
        query.setTradeNo(req.getTradeNo());
        query.setCustomerId(req.getCustomerId());
        query.setTradeType(req.getTradeType());
        query.setTradeStatus(req.getTradeStatus());
        query.setDealType(req.getDealType());
        query.setStartDate(req.getStartDate());
        query.setEndDate(req.getEndDate());
        return query;
    }

    private CreateTradeCmd toCmd(CreateTradeReq req) {
        CreateTradeCmd cmd = new CreateTradeCmd();
        cmd.setCustomerId(req.getCustomerId());
        cmd.setTradeType(req.getTradeType());
        cmd.setDealType(req.getDealType());
        cmd.setBuyCurrency(req.getBuyCurrency());
        cmd.setSellCurrency(req.getSellCurrency());
        cmd.setBuyAmount(req.getBuyAmount());
        cmd.setSellAmount(req.getSellAmount());
        cmd.setTradeRate(req.getTradeRate());
        cmd.setValueDate(req.getValueDate());
        cmd.setMaturityDate(req.getMaturityDate());
        cmd.setNearValueDate(req.getNearValueDate());
        cmd.setFarValueDate(req.getFarValueDate());
        cmd.setNearRate(req.getNearRate());
        cmd.setFarRate(req.getFarRate());
        cmd.setSwapPoints(req.getSwapPoints());
        cmd.setOptionType(req.getOptionType());
        cmd.setStrikePrice(req.getStrikePrice());
        cmd.setPremiumAmount(req.getPremiumAmount());
        cmd.setPremiumCurrency(req.getPremiumCurrency());
        cmd.setPremiumDate(req.getPremiumDate());
        cmd.setExpiryDate(req.getExpiryDate());
        cmd.setDeliveryType(req.getDeliveryType());
        cmd.setCounterparty(req.getCounterparty());
        cmd.setNostroAccount(req.getNostroAccount());
        cmd.setTradeChannel(req.getTradeChannel());
        cmd.setRemark(req.getRemark());
        return cmd;
    }

    private TradeResp toTradeResp(FxTrade trade) {
        TradeResp resp = new TradeResp();
        resp.setId(trade.getId());
        resp.setTradeNo(trade.getTradeNo());
        resp.setCustomerId(trade.getCustomerId());
        resp.setTradeType(trade.getTradeType());
        resp.setDealType(trade.getDealType());
        resp.setBuyCurrency(trade.getBuyCurrency());
        resp.setSellCurrency(trade.getSellCurrency());
        resp.setBuyAmount(trade.getBuyAmount());
        resp.setSellAmount(trade.getSellAmount());
        resp.setTradeRate(trade.getTradeRate());
        resp.setValueDate(trade.getValueDate());
        resp.setMaturityDate(trade.getMaturityDate());
        resp.setNearValueDate(trade.getNearValueDate());
        resp.setFarValueDate(trade.getFarValueDate());
        resp.setNearRate(trade.getNearRate());
        resp.setFarRate(trade.getFarRate());
        resp.setSwapPoints(trade.getSwapPoints());
        resp.setOptionType(trade.getOptionType());
        resp.setStrikePrice(trade.getStrikePrice());
        resp.setPremiumAmount(trade.getPremiumAmount());
        resp.setPremiumCurrency(trade.getPremiumCurrency());
        resp.setPremiumDate(trade.getPremiumDate());
        resp.setExpiryDate(trade.getExpiryDate());
        resp.setDeliveryType(trade.getDeliveryType());
        resp.setTradeStatus(trade.getTradeStatus());
        resp.setSettlementStatus(trade.getSettlementStatus());
        resp.setNostroAccount(trade.getNostroAccount());
        resp.setCounterparty(trade.getCounterparty());
        resp.setTradeChannel(trade.getTradeChannel());
        resp.setOperatorId(trade.getOperatorId());
        resp.setConfirmTime(trade.getConfirmTime());
        resp.setExecuteTime(trade.getExecuteTime());
        resp.setSettleTime(trade.getSettleTime());
        resp.setRemark(trade.getRemark());
        return resp;
    }
}
