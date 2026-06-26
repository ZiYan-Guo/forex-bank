package com.forex.ai.domain.model.aggregate;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

import com.forex.common.base.domain.BaseAggregate;

import lombok.Getter;
import com.forex.common.base.exception.BusinessException;
import com.forex.common.base.result.ResultCode;

@Getter
public class DocumentAudit extends BaseAggregate {

    private static final long serialVersionUID = 1L;

    public static final String INVOICE = "INVOICE";
    public static final String BL = "BL";
    public static final String PACKING_LIST = "PACKING_LIST";
    public static final String CUSTOMS_DECLARATION = "CUSTOMS_DECLARATION";

    private Long id;
    private String auditId;
    private String bizNo;
    private String docType;
    private String ocrResult;
    private String comparisonResult;
    private Boolean isConsistent;
    private String discrepancyDetail;
    private String auditOpinion;
    private BigDecimal confidenceScore;

    private DocumentAudit() {
        super();
    }

    public static DocumentAudit create(String bizNo, String docType, String ocrResult) {
        DocumentAudit audit = new DocumentAudit();
        audit.auditId = "DA-" + UUID.randomUUID().toString().substring(0, 8).toUpperCase();
        audit.bizNo = bizNo;
        audit.docType = docType;
        audit.ocrResult = ocrResult;
        audit.isConsistent = null;
        audit.confidenceScore = BigDecimal.ZERO;
        audit.validate();
        return audit;
    }

    public static DocumentAudit reconstitute(Long id, String auditId, String bizNo, String docType,
                                               String ocrResult, String comparisonResult,
                                               Boolean isConsistent, String discrepancyDetail,
                                               String auditOpinion, BigDecimal confidenceScore,
                                               LocalDateTime createdAt, LocalDateTime updatedAt,
                                               Integer version) {
        DocumentAudit audit = new DocumentAudit();
        audit.id = id;
        audit.auditId = auditId;
        audit.bizNo = bizNo;
        audit.docType = docType;
        audit.ocrResult = ocrResult;
        audit.comparisonResult = comparisonResult;
        audit.isConsistent = isConsistent;
        audit.discrepancyDetail = discrepancyDetail;
        audit.auditOpinion = auditOpinion;
        audit.confidenceScore = confidenceScore;
        return audit;
    }

    public void markConsistent() {
        this.isConsistent = true;
        this.discrepancyDetail = null;
        this.auditOpinion = "单据一致，通过审核";
        markUpdated();
    }

    public void markDiscrepancy(String detail) {
        if (detail == null || detail.isBlank()) {
            throw new BusinessException(ResultCode.VALIDATE_FAIL, "discrepancy detail must not be blank");
        }
        this.isConsistent = false;
        this.discrepancyDetail = detail;
        this.auditOpinion = "单据不一致：" + detail;
        markUpdated();
    }

    public void setComparisonResult(String comparisonResult) {
        this.comparisonResult = comparisonResult;
        markUpdated();
    }

    public void setConfidenceScore(BigDecimal confidenceScore) {
        this.confidenceScore = confidenceScore;
        markUpdated();
    }

    public boolean isAudited() {
        return isConsistent != null;
    }

    @Override
    protected void validate() {
        if (bizNo == null || bizNo.isBlank()) {
            throw new BusinessException(ResultCode.VALIDATE_FAIL, "bizNo must not be blank");
        }
        if (docType == null || docType.isBlank()) {
            throw new BusinessException(ResultCode.VALIDATE_FAIL, "docType must not be blank");
        }
    }
}
