package com.forex.ai.domain.service;

import java.util.List;

import org.springframework.stereotype.Service;

import com.forex.ai.domain.model.aggregate.RiskAiAssessment;
import com.forex.ai.infrastructure.rag.KnowledgeBaseInitializer;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
@RequiredArgsConstructor
public class SmartRagService {

    private final KnowledgeBaseInitializer knowledgeBaseInitializer;

    public String answerQuery(String userQuestion, String context) {
        log.info("RAG answering question: {}", userQuestion);
        List<String> relevantDocs = knowledgeBaseInitializer.searchRelevant(userQuestion, 3);

        StringBuilder answer = new StringBuilder();
        answer.append("Based on the knowledge base:\n\n");
        for (int i = 0; i < relevantDocs.size(); i++) {
            answer.append("**Reference ").append(i + 1).append(":** ")
                    .append(relevantDocs.get(i)).append("\n\n");
        }
        if (context != null && !context.isBlank()) {
            answer.append("**Context:** ").append(context).append("\n\n");
        }
        answer.append("**Answer:** ").append(generateAnswer(userQuestion, relevantDocs));
        return answer.toString();
    }

    public String generateReport(List<RiskAiAssessment> assessments, String reportType) {
        if (assessments == null || assessments.isEmpty()) {
            return "无评估数据，无法生成报告。";
        }
        StringBuilder report = new StringBuilder();
        report.append("## ").append(reportType).append(" Report\n\n");
        report.append("评估总数: ").append(assessments.size()).append("\n\n");

        long highCount = assessments.stream().filter(RiskAiAssessment::isEscalated).count();
        report.append("需关注项: ").append(highCount).append("\n\n");

        for (RiskAiAssessment assessment : assessments) {
            report.append("- [").append(assessment.getRiskLevel()).append("] ")
                    .append(assessment.getBizNo()).append(": ")
                    .append(assessment.getAiAnalysis()).append("\n");
        }
        return report.toString();
    }

    private String generateAnswer(String question, List<String> relevantDocs) {
        if (question.contains("外汇") || question.contains("forex")) {
            return "根据外汇管理规定，需严格遵守客户尽职调查和交易监控要求。";
        }
        if (question.contains("反洗钱") || question.contains("AML")) {
            return "根据反洗钱规定，大额和可疑交易需在规定时限内上报。";
        }
        return "根据相关政策和规定，建议参照最近的外汇业务操作指引执行。";
    }
}
