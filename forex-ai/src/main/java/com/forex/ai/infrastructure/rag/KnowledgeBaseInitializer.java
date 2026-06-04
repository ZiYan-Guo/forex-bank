package com.forex.ai.infrastructure.rag;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.stereotype.Component;

import lombok.extern.slf4j.Slf4j;

@Component
@Slf4j
public class KnowledgeBaseInitializer implements ApplicationRunner {

    private final Map<String, String> vectorStore = new ConcurrentHashMap<>();

    @Override
    public void run(ApplicationArguments args) {
        log.info("Initializing AI knowledge base...");
        vectorStore.put("forex_policy",
                "根据《银行外汇展业管理办法》，银行应建立事前、事中、事后全流程展业管理框架，"
                        + "对外汇业务进行客户识别、业务审核、风险监控和持续尽职调查。");
        vectorStore.put("settlement_rule",
                "国际收支申报应在业务发生后5个工作日内完成，跨境人民币结算应在业务发生后"
                        + "次月前10个工作日内完成申报。");
        vectorStore.put("lc_process",
                "信用证开立需要：1. 申请人提交开证申请书 2. 银行审核 3. 开立MT700报文 "
                        + "4. 通知行通知受益人 5. 受益人交单 6. 银行审单付款。");
        vectorStore.put("aml_policy",
                "大额交易报告标准：单笔或当日累计等值5万美元以上的跨境交易需上报。"
                        + "可疑交易需在5个工作日内向中国反洗钱监测分析中心报告。");
        vectorStore.put("forex_risk",
                "外汇风险敞口管理包括：即期头寸管理、远期头寸管理、期权头寸管理。"
                        + "银行应计提外汇风险资本，采用标准法或内部模型法。");
        vectorStore.put("compliance_check",
                "外汇业务合规审查要点：1. 贸易背景真实性审核 2. 国际收支申报完整性 "
                        + "3. 反洗钱筛查 4. 制裁名单筛查 5. 外汇额度管理。");
        log.info("Knowledge base initialized with {} documents", vectorStore.size());
    }

    public List<String> searchRelevant(String query, int topK) {
        return vectorStore.values().stream()
                .filter(doc -> containsKeyword(doc, query))
                .limit(topK)
                .toList();
    }

    public List<String> getAllDocuments() {
        return new ArrayList<>(vectorStore.values());
    }

    public String getDocument(String key) {
        return vectorStore.get(key);
    }

    private boolean containsKeyword(String doc, String query) {
        for (String word : query.split("\\s+")) {
            if (word.length() > 1 && doc.contains(word)) {
                return true;
            }
        }
        return false;
    }
}
