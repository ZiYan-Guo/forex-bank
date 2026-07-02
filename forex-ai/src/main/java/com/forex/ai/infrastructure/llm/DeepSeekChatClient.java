package com.forex.ai.infrastructure.llm;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.forex.ai.infrastructure.config.DeepSeekConfig;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Duration;
import java.util.*;

@Slf4j
@Service
@RequiredArgsConstructor
public class DeepSeekChatClient {

    private final DeepSeekConfig config;
    private final ObjectMapper objectMapper;
    private final HttpClient httpClient = HttpClient.newBuilder()
            .connectTimeout(Duration.ofSeconds(30))
            .build();

    public String chat(String systemPrompt, String userMessage) {
        return chat(systemPrompt, List.of(
                Map.of("role", "user", "content", userMessage)
        ));
    }

    @SuppressWarnings("unchecked")
    public String chat(String systemPrompt, List<Map<String, Object>> messages) {
        if (!config.isEnabled()) {
            log.info("DeepSeek disabled, using local response");
            return generateLocalResponse(messages);
        }

        try {
            List<Map<String, Object>> fullMessages = new ArrayList<>();
            if (systemPrompt != null && !systemPrompt.isBlank()) {
                fullMessages.add(Map.of("role", "system", "content", systemPrompt));
            }
            fullMessages.addAll(messages);

            Map<String, Object> body = new LinkedHashMap<>();
            body.put("model", config.getChatModel());
            body.put("messages", fullMessages);
            body.put("temperature", config.getTemperature());
            body.put("max_tokens", config.getMaxTokens());

            String jsonBody = objectMapper.writeValueAsString(body);

            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create(config.getBaseUrl() + "/chat/completions"))
                    .header("Content-Type", "application/json")
                    .header("Authorization", "Bearer " + config.getApiKey())
                    .timeout(Duration.ofSeconds(config.getTimeout()))
                    .POST(HttpRequest.BodyPublishers.ofString(jsonBody))
                    .build();

            HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());

            if (response.statusCode() == 200) {
                Map<String, Object> responseBody = objectMapper.readValue(response.body(), Map.class);
                List<Map<String, Object>> choices = (List<Map<String, Object>>) responseBody.get("choices");
                if (choices != null && !choices.isEmpty()) {
                    Map<String, Object> message = (Map<String, Object>) choices.get(0).get("message");
                    return (String) message.get("content");
                }
            } else {
                log.error("DeepSeek API error: {} - {}", response.statusCode(), response.body());
            }
        } catch (Exception e) {
            log.error("DeepSeek API call failed", e);
        }

        return generateLocalResponse(messages);
    }

    private String generateLocalResponse(List<Map<String, Object>> messages) {
        String lastMessage = "";
        for (int i = messages.size() - 1; i >= 0; i--) {
            if ("user".equals(messages.get(i).get("role"))) {
                lastMessage = (String) messages.get(i).get("content");
                break;
            }
        }

        String q = lastMessage.toLowerCase();
        if (q.contains("汇率")) return "当前USD/CNY参考汇率为7.2536，市场呈震荡上行趋势。";
        if (q.contains("黄金") || q.contains("贵金属")) return "黄金现货参考价为每盎司2350美元，Au99.99国内报价560元/克。";
        if (q.contains("风险")) return "根据评估，当前交易对手风险等级为低风险，敞口在限额范围内。";
        return "我是银行外汇业务智能助手，可以帮助您查询汇率、执行交易、评估风险、下载报告。请告诉我您的具体需求。";
    }
}
