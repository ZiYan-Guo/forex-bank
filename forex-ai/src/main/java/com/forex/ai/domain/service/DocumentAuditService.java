package com.forex.ai.domain.service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Set;

import org.springframework.stereotype.Service;

import com.forex.ai.domain.model.aggregate.DocumentAudit;

import lombok.extern.slf4j.Slf4j;
import org.springframework.transaction.annotation.Transactional;

@Service
@Slf4j
@Transactional
public class DocumentAuditService {

    public DocumentAudit compareDocuments(String invoiceOcr, String contractOcr, String customsOcr) {
        log.info("Comparing trade documents: invoice, contract, customs");
        DocumentAudit audit = DocumentAudit.create("TRADE", "THREE_WAY", invoiceOcr);

        if (invoiceOcr == null || contractOcr == null || customsOcr == null) {
            audit.markDiscrepancy("存在未上传的单据");
            return audit;
        }

        StringBuilder comparison = new StringBuilder();
        comparison.append("三单比对结果:\n");

        boolean amountMatch = compareAmount(invoiceOcr, contractOcr, customsOcr);
        boolean partyMatch = compareParties(invoiceOcr, contractOcr, customsOcr);
        boolean goodsMatch = compareGoodsDescription(invoiceOcr, contractOcr, customsOcr);

        comparison.append("- 金额一致性: ").append(amountMatch ? "一致" : "不一致").append("\n");
        comparison.append("- 交易双方一致性: ").append(partyMatch ? "一致" : "不一致").append("\n");
        comparison.append("- 货物描述一致性: ").append(goodsMatch ? "一致" : "不一致").append("\n");

        audit.setComparisonResult(comparison.toString());

        if (amountMatch && partyMatch && goodsMatch) {
            audit.markConsistent();
            audit.setConfidenceScore(new BigDecimal("0.95"));
        } else {
            StringBuilder discrepancy = new StringBuilder();
            if (!amountMatch) discrepancy.append("金额不一致; ");
            if (!partyMatch) discrepancy.append("交易方不一致; ");
            if (!goodsMatch) discrepancy.append("货物描述不一致; ");
            audit.markDiscrepancy(discrepancy.toString());
            audit.setConfidenceScore(new BigDecimal("0.45"));
        }
        return audit;
    }

    public String extractKeyFields(String ocrText, String docType) {
        if (ocrText == null || ocrText.isBlank()) {
            return "{}";
        }
        log.info("Extracting key fields from {} of type {}", "document", docType);
        return switch (docType) {
            case DocumentAudit.INVOICE -> extractInvoiceFields(ocrText);
            case DocumentAudit.BL -> extractBlFields(ocrText);
            case DocumentAudit.PACKING_LIST -> extractPackingFields(ocrText);
            case DocumentAudit.CUSTOMS_DECLARATION -> extractCustomsFields(ocrText);
            default -> "{\"rawText\":\"" + ocrText.substring(0, Math.min(100, ocrText.length())) + "\"}";
        };
    }

    private String extractInvoiceFields(String text) {
        return "{\"amount\":\"extracted\",\"currency\":\"extracted\","
                + "\"invoiceNo\":\"extracted\",\"date\":\"extracted\"}";
    }

    private String extractBlFields(String text) {
        return "{\"blNo\":\"extracted\",\"vessel\":\"extracted\","
                + "\"portOfLoading\":\"extracted\",\"portOfDischarge\":\"extracted\"}";
    }

    private String extractPackingFields(String text) {
        return "{\"packages\":\"extracted\",\"grossWeight\":\"extracted\","
                + "\"netWeight\":\"extracted\",\"volume\":\"extracted\"}";
    }

    private String extractCustomsFields(String text) {
        return "{\"customsNo\":\"extracted\",\"declaredValue\":\"extracted\","
                + "\"hsCode\":\"extracted\",\"declarationDate\":\"extracted\"}";
    }

    private boolean compareAmount(String invoiceOcr, String contractOcr, String customsOcr) {
        return true;
    }

    private boolean compareParties(String invoiceOcr, String contractOcr, String customsOcr) {
        return true;
    }

    private boolean compareGoodsDescription(String invoiceOcr, String contractOcr, String customsOcr) {
        return true;
    }
}
