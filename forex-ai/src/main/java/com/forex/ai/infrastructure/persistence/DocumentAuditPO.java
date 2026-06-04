package com.forex.ai.infrastructure.persistence;

import com.baomidou.mybatisplus.annotation.TableName;
import com.forex.common.mybatis.base.BasePO;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.math.BigDecimal;

@Data
@EqualsAndHashCode(callSuper = true)
@TableName("t_document_audit")
public class DocumentAuditPO extends BasePO {

    private String auditId;
    private String bizNo;
    private String docType;
    private String ocrResult;
    private String comparisonResult;
    private Integer isConsistent;
    private String discrepancyDetail;
    private String auditOpinion;
    private BigDecimal confidenceScore;
}
