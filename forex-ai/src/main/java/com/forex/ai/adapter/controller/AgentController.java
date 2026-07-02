package com.forex.ai.adapter.controller;

import com.forex.ai.adapter.dto.ChatMessageReq;
import com.forex.ai.adapter.dto.ChatMessageResp;
import com.forex.ai.domain.agent.core.AgentOrchestrator;
import com.forex.ai.domain.agent.core.AgentResult;
import com.forex.common.base.result.R;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.UUID;

@Tag(name = "Agent 智能体")
@RestController
@RequestMapping("/api/agent")
@RequiredArgsConstructor
public class AgentController {

    private final AgentOrchestrator agentOrchestrator;

    @Operation(summary = "Agent 对话（多轮多工具）")
    @PostMapping("/chat")
    public R<Map<String, Object>> chat(@Valid @RequestBody ChatMessageReq req) {
        String sessionId = req.getSessionId() != null ? req.getSessionId() : UUID.randomUUID().toString();
        AgentResult result = agentOrchestrator.execute(req.getQuestion(), sessionId);

        Map<String, Object> resp = new java.util.LinkedHashMap<>();
        resp.put("sessionId", sessionId);
        resp.put("answer", result.getAnswer());
        resp.put("success", result.isSuccess());
        resp.put("iterations", result.getIterations());
        resp.put("downloadUrl", result.getDownloadUrl());

        List<Map<String, Object>> actions = new ArrayList<>();
        for (var action : result.getActionHistory()) {
            actions.add(Map.of(
                    "thought", action.getThought() != null ? action.getThought() : "",
                    "tool", action.getTool() != null ? action.getTool() : "",
                    "finalAnswer", action.isFinalAnswer()
            ));
        }
        resp.put("actions", actions);

        return R.ok(resp);
    }
}
