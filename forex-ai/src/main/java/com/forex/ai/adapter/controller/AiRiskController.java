package com.forex.ai.adapter.controller;

import com.forex.ai.adapter.dto.RiskAlertResp;
import com.forex.ai.application.service.AiAppService;
import com.forex.common.base.result.R;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@Tag(name = "AI智能风控")
@RestController
@RequestMapping("/api/ai/risk")
@RequiredArgsConstructor
public class AiRiskController {

    private final AiAppService aiAppService;

    @Operation(summary = "AI反洗钱评估")
    @PostMapping("/aml/evaluate")
    public R<Map<String, Object>> amlEvaluate(@RequestBody Map<String, Object> req) {
        String customerId = req.get("customerId") != null ? req.get("customerId").toString() : "001";
        String transactionData = req.get("transactionData") != null ? req.get("transactionData").toString() : "";
        return R.ok(aiAppService.amlEvaluate(customerId, transactionData));
    }

    @Operation(summary = "黑名单模糊匹配")
    @PostMapping("/blacklist/fuzzy")
    public R<Map<String, Object>> fuzzyMatchBlacklist(@RequestBody Map<String, Object> req) {
        String name = (String) req.getOrDefault("name", "");
        String idType = (String) req.getOrDefault("idType", "NAME");
        return R.ok(aiAppService.fuzzyMatchBlacklist(name, idType));
    }

    @Operation(summary = "关联网络分析")
    @GetMapping("/network/{customerId}")
    public R<Map<String, Object>> networkAnalysis(@PathVariable String customerId) {
        return R.ok(aiAppService.networkAnalysis(customerId));
    }
}
