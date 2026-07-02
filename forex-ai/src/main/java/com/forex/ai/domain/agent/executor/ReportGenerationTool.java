package com.forex.ai.domain.agent.executor;

import com.forex.ai.application.service.AiAppService;
import com.forex.ai.domain.agent.core.AgentTool;
import com.forex.ai.domain.agent.registry.ToolRegistry;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.UUID;

@Component
@RequiredArgsConstructor
public class ReportGenerationTool implements AgentTool {

    private final ToolRegistry toolRegistry;
    private final AiAppService aiAppService;

    @PostConstruct
    public void init() {
        toolRegistry.register(this);
    }

    @Override
    public String name() {
        return "generate_report";
    }

    @Override
    public String description() {
        return "生成业务报告并支持下载。可生成交易报告、风险报告、监管报表等。参数: query (报告描述), format (PDF/EXCEL/CSV), reportType (TRADE/RISK/REGULATORY)";
    }

    @Override
    public Map<String, Object> execute(Map<String, Object> parameters) {
        String query = (String) parameters.getOrDefault("query", "综合报告");
        String format = (String) parameters.getOrDefault("format", "PDF");
        String reportType = (String) parameters.getOrDefault("reportType", "TRADE");

        Map<String, Object> reportResult = aiAppService.generateReport(
                "agent-user", "current", reportType);

        Map<String, Object> result = new LinkedHashMap<>();
        result.put("reportId", UUID.randomUUID().toString());
        result.put("reportType", reportType);
        result.put("format", format);
        result.put("title", "业务报告 - " + reportType);
        result.put("downloadUrl", reportResult.get("reportUrl"));
        result.put("generatedAt", System.currentTimeMillis());
        result.put("summary", "报告包含" + reportType + "相关指标和数据分析。");
        return result;
    }
}
