package com.forex.ai.adapter.controller;

import com.forex.ai.adapter.dto.ChatMessageReq;
import com.forex.ai.adapter.dto.ChatMessageResp;
import com.forex.ai.adapter.dto.TradingRecommendResp;
import com.forex.ai.application.service.AiAppService;
import com.forex.common.base.result.R;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;
import com.forex.common.security.annotation.RequirePermission;

@Tag(name = "AI智能客服")
@RestController
@RequestMapping("/api/ai/chat")
@RequiredArgsConstructor
public class AiChatController {

    private final AiAppService aiAppService;

    @Operation(summary = "RAG智能问答")
    @RequirePermission("ai:query")
    @PostMapping("/query")
    public R<Map<String, Object>> chatQuery(@Valid @RequestBody ChatMessageReq req) {
        String sessionId = req.getSessionId();
        if (sessionId == null || sessionId.isEmpty()) {
            sessionId = aiAppService.createSession("SUPPORT");
        }
        Map<String, Object> resp = aiAppService.chatQuery(sessionId, req.getQuestion());
        return R.ok(resp);
    }

    @Operation(summary = "套保推荐")
    @RequirePermission("ai:recommend")
    @PostMapping("/recommend")
    public R<Map<String, Object>> recommendHedging(@RequestBody Map<String, Object> req) {
        String customerId = (String) req.getOrDefault("customerId", "001");
        String businessType = (String) req.getOrDefault("businessType", "IMPORT");
        String riskPreference = (String) req.getOrDefault("riskPreference", "MODERATE");
        return R.ok(aiAppService.recommendHedging(customerId, businessType, riskPreference));
    }

    @Operation(summary = "自然语言交易")
    @RequirePermission("ai:nl-trade")
    @PostMapping("/nl-trade")
    public R<Map<String, Object>> nlTrade(@RequestBody Map<String, Object> req) {
        String input = (String) req.get("input");
        return R.ok(aiAppService.processNlTrade(input));
    }

    @Operation(summary = "智能报告生成")
    @RequirePermission("ai:generate")
    @PostMapping("/report/generate")
    public R<Map<String, Object>> generateReport(@RequestBody Map<String, Object> req) {
        String customerId = (String) req.getOrDefault("customerId", "001");
        String period = (String) req.getOrDefault("period", "MONTHLY");
        String reportType = (String) req.getOrDefault("reportType", "TRADING");
        return R.ok(aiAppService.generateReport(customerId, period, reportType));
    }

    @Operation(summary = "创建会话")
    @RequirePermission("ai:create")
    @PostMapping("/session/create")
    public R<Map<String, String>> createSession(@RequestBody Map<String, String> req) {
        String sessionType = req.getOrDefault("sessionType", "SUPPORT");
        String sessionId = aiAppService.createSession(sessionType);
        return R.ok(Map.of("sessionId", sessionId));
    }

    @Operation(summary = "获取消息历史")
    @GetMapping("/session/{sessionId}/messages")
    public R<List<Map<String, Object>>> getMessages(@PathVariable String sessionId) {
        return R.ok(aiAppService.getSessionMessages(sessionId));
    }
}
