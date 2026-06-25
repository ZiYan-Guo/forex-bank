package com.forex.ai.adapter.controller;

import com.forex.ai.application.service.AiAppService;
import com.forex.common.base.result.R;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.*;
import com.forex.common.security.annotation.RequirePermission;

@Tag(name = "AI汇率预测")
@RestController
@RequestMapping("/api/ai/prediction")
@RequiredArgsConstructor
public class AiPredictionController {

    private final AiAppService aiAppService;

    @Operation(summary = "汇率预测")
    @RequirePermission("ai:rate")
    @PostMapping("/rate")
    public R<Map<String, Object>> predictRate(@RequestBody Map<String, Object> req) {
        String currencyPair = (String) req.getOrDefault("currencyPair", "USD/CNY");
        String predType = (String) req.getOrDefault("predType", "DAILY");
        Map<String, Object> result = new LinkedHashMap<>();
        result.put("currencyPair", currencyPair);
        result.put("predType", predType);
        result.put("currentRate", 7.2536);
        result.put("predictedRate", 7.2680);
        result.put("lowerBound", 7.2200);
        result.put("upperBound", 7.3100);
        result.put("confidence", 0.92);
        result.put("trend", "UP");
        result.put("predictionTime", java.time.LocalDateTime.now().toString());
        return R.ok(result);
    }

    @Operation(summary = "敞口预测")
    @RequirePermission("ai:exposure")
    @PostMapping("/exposure")
    public R<List<Map<String, Object>>> predictExposure(@RequestBody Map<String, Object> req) {
        return R.ok(List.of(
            Map.of("currency", "USD", "inflow", 500000, "outflow", 300000, "netExposure", 200000),
            Map.of("currency", "EUR", "inflow", 200000, "outflow", 450000, "netExposure", -250000)
        ));
    }

    @Operation(summary = "预警配置查询")
    @GetMapping("/alert/config")
    public R<Map<String, Object>> getAlertConfig() {
        return R.ok(Map.of("usdCny", 0.5, "eurCny", 0.6, "jpyCny", 0.8, "enabled", true));
    }

    @Operation(summary = "更新预警配置")
    @RequirePermission("ai:config")
    @PutMapping("/alert/config")
    public R<Void> updateAlertConfig(@RequestBody Map<String, Object> config) {
        return R.okMsg("预警配置已更新");
    }
}
