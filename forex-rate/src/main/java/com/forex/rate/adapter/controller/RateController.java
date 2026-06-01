package com.forex.rate.adapter.controller;

import java.math.BigDecimal;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.forex.common.base.annotation.RateLimit;
import com.forex.common.base.result.R;
import com.forex.common.security.annotation.RequireRole;
import com.forex.rate.adapter.dto.ConversionResp;
import com.forex.rate.adapter.dto.RateQueryReq;
import com.forex.rate.adapter.dto.RateResp;
import com.forex.rate.application.command.RateSaveCmd;
import com.forex.rate.application.service.RateAppService;
import com.forex.rate.domain.model.aggregate.ExchangeRate;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@Tag(name = "汇率管理")
@RestController
@RequestMapping("/api/rate")
@RequiredArgsConstructor
public class RateController {

    private final RateAppService rateAppService;

    @Operation(summary = "查询最新汇率")
    @GetMapping("/latest/{currencyPair}")
    public R<RateResp> getLatestRate(@PathVariable String currencyPair) {
        ExchangeRate rate = rateAppService.getRate(currencyPair);
        return R.ok(toRateResp(rate));
    }

    @Operation(summary = "查询所有最新汇率")
    @GetMapping("/latest/all")
    public R<List<RateResp>> getAllRates() {
        List<RateResp> rates = rateAppService.getAllRates().values().stream()
                .map(this::toRateResp)
                .collect(Collectors.toList());
        return R.ok(rates);
    }

    @Operation(summary = "货币转换")
    @RateLimit(key = "rate:convert", limit = 50, windowSeconds = 1)
    @PostMapping("/convert")
    public R<ConversionResp> convertCurrency(@Valid @RequestBody RateQueryReq req) {
        BigDecimal converted = rateAppService.convertCurrency(req.getCurrencyPair(), req.getTargetCurrency(), req.getAmount());
        ExchangeRate rate = rateAppService.getRate(req.getCurrencyPair());

        ConversionResp resp = new ConversionResp();
        resp.setFromCurrency(req.getCurrencyPair());
        resp.setToCurrency(req.getTargetCurrency());
        resp.setOriginalAmount(req.getAmount());
        resp.setConvertedAmount(converted);
        resp.setExchangeRate(rate.getMidRate());
        resp.setRateTime(rate.getRateTime());
        return R.ok(resp);
    }

    @Operation(summary = "保存汇率")
    @RequireRole("TRADER")
    @PostMapping("/save")
    public R<RateResp> saveRate(@Valid @RequestBody RateSaveCmd cmd) {
        ExchangeRate rate = rateAppService.saveRate(cmd);
        return R.ok(toRateResp(rate));
    }

    @Operation(summary = "发布汇率到渠道")
    @RequireRole("TRADER")
    @PostMapping("/publish/{rateId}")
    public R<Void> publishRate(@PathVariable Long rateId) {
        rateAppService.publishToChannels(rateId);
        return R.ok();
    }

    private RateResp toRateResp(ExchangeRate rate) {
        RateResp resp = new RateResp();
        resp.setCurrencyPair(rate.getCurrencyPair());
        resp.setBidRate(rate.getBidRate());
        resp.setAskRate(rate.getAskRate());
        resp.setMidRate(rate.getMidRate());
        resp.setRateTime(rate.getRateTime());
        resp.setRateSource(rate.getRateSource());
        return resp;
    }
}
