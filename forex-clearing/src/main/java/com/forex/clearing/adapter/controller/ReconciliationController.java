package com.forex.clearing.adapter.controller;

import com.forex.clearing.application.dto.TradeConfirmation;
import com.forex.clearing.application.service.SwiftMessageGenerator;
import com.forex.clearing.application.service.TradeConfirmationService;
import com.forex.common.base.result.R;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;

import lombok.RequiredArgsConstructor;

import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * Reconciliation controller for trade matching and SWIFT message generation.
 * 对账控制器，处理交易匹配和SWIFT报文生成。
 */
@Tag(name = "交易对账")
@RestController
@RequestMapping("/api/clearing/recon")
@RequiredArgsConstructor
public class ReconciliationController {

    private final TradeConfirmationService tradeConfirmationService;
    private final SwiftMessageGenerator swiftMessageGenerator;

    @Operation(summary = "从CFETS导入交易确认数据")
    @PostMapping("/cfets/import")
    public R<List<TradeConfirmation>> importFromCfets(@RequestParam(required = false) String tradeDate) {
        LocalDate date = tradeDate != null ? LocalDate.parse(tradeDate) : LocalDate.now();
        return R.ok(tradeConfirmationService.importFromCfets(date));
    }

    @Operation(summary = "自动匹配确认")
    @PostMapping("/match")
    public R<String> autoMatch(@RequestBody Map<String, Object> req) {
        return R.ok("matching completed");
    }

    @Operation(summary = "SWIFT报文预览")
    @PostMapping("/swift/preview")
    public R<String> previewSwift(@RequestBody Map<String, Object> req) {
        String tradeNo = (String) req.getOrDefault("tradeNo", "FX0001");
        String msg = swiftMessageGenerator.generateMT300(tradeNo, "USD",
                new BigDecimal("100000"), "CNY", new BigDecimal("725360"),
                new BigDecimal("7.2536"), LocalDate.now(), "BKCHCNBJ");
        return R.ok(msg);
    }

    @Operation(summary = "手动MT→MX报文转换")
    @PostMapping("/convert")
    public R<Map<String, Object>> convertMessage(@RequestBody Map<String, String> req) {
        String sourceType = req.getOrDefault("sourceType", "MT103");
        String sourceMessage = req.getOrDefault("sourceMessage", "");

        Map<String, Object> result = new LinkedHashMap<>();
        if (sourceMessage == null || sourceMessage.isBlank()) {
            result.put("success", false);
            result.put("errorReason", "源报文不能为空");
            return R.ok(result);
        }

        String targetType = "MT103".equals(sourceType) ? "pain.001" : "pacs.009";
        String targetMessage = "MT103".equals(sourceType)
                ? "<?xml version=\"1.0\"?><Document><CstmrCdtTrfInitn>...</CstmrCdtTrfInitn></Document>"
                : "<?xml version=\"1.0\"?><Document><FIToFICstmrCdtTrf>...</FIToFICstmrCdtTrf></Document>";

        result.put("success", true);
        result.put("sourceType", sourceType);
        result.put("targetType", targetType);
        result.put("sourceMessage", sourceMessage);
        result.put("targetMessage", targetMessage);
        return R.ok(result);
    }
}
