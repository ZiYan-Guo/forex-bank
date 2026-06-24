package com.forex.ai.domain.service;

import com.forex.ai.domain.model.aggregate.RiskAiAssessment;
import com.forex.ai.infrastructure.rag.KnowledgeBaseInitializer;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class SmartRagServiceTest {

    @Mock private KnowledgeBaseInitializer knowledgeBaseInitializer;

    @InjectMocks
    private SmartRagService smartRagService;

    @Test
    @DisplayName("Answer query returns formatted answer with references")
    void testAnswerQuery() {
        when(knowledgeBaseInitializer.searchRelevant(anyString(), eq(3)))
                .thenReturn(List.of("外汇管理规定", "跨境支付操作指引", "反洗钱合规要求"));

        String result = smartRagService.answerQuery("外汇汇款流程", "2024年最新政策");

        assertNotNull(result);
        assertTrue(result.contains("Reference"));
        assertTrue(result.contains("外汇管理规定"));
    }

    @Test
    @DisplayName("Generate report handles empty assessments")
    void testGenerateReport_Empty() {
        String result = smartRagService.generateReport(List.of(), "AML");
        assertTrue(result.contains("无评估数据"));
    }

    @Test
    @DisplayName("Generate report includes escalated count")
    void testGenerateReport() {
        RiskAiAssessment r1 = RiskAiAssessment.create(1L, "BIZ001", "AML",
                new BigDecimal("0.85"), "HIGH", "可疑", "复审", "{}");
        RiskAiAssessment r2 = RiskAiAssessment.create(2L, "BIZ002", "AML",
                new BigDecimal("0.20"), "LOW", "正常", "放行", "{}");

        String result = smartRagService.generateReport(List.of(r1, r2), "AML");

        assertTrue(result.contains("1"));
        assertTrue(result.contains("HIGH"));
    }
}
