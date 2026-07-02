package com.forex.ai.domain.service;

import com.forex.ai.infrastructure.llm.DeepSeekChatClient;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.*;

@Slf4j
@Service
@RequiredArgsConstructor
public class SmartRagService {

    private final DeepSeekChatClient chatClient;

    public Map<String, Object> search(String query) {
        Map<String, Object> result = new LinkedHashMap<>();

        String systemPrompt = """
            你是一个外汇银行业务专家助手。你的知识涵盖：
            - 外汇管理条例
            - 跨境支付与清算规则
            - 信用证、托收、保函等国际结算业务流程
            - 反洗钱合规要求
            - 外汇风险管理
            - 贵金属交易规则
            请基于以上知识回答用户问题。
            """;

        String answer = chatClient.chat(systemPrompt, query);
        result.put("answer", answer);
        result.put("query", query);
        return result;
    }
}
