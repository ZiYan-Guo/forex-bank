package com.forex.ai.infrastructure.repository;

import com.forex.ai.domain.model.aggregate.DocumentAudit;
import com.forex.ai.domain.repository.DocumentAuditRepository;
import com.forex.ai.infrastructure.mapper.DocumentAuditMapper;
import com.forex.ai.infrastructure.persistence.DocumentAuditPO;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
@RequiredArgsConstructor
public class DocumentAuditRepositoryImpl implements DocumentAuditRepository {

    private final DocumentAuditMapper documentAuditMapper;

    @Override
    public Optional<DocumentAudit> findByAuditId(String auditId) {
        DocumentAuditPO po = documentAuditMapper.findByAuditId(auditId);
        return Optional.ofNullable(po).map(this::toDomain);
    }

    @Override
    public Optional<DocumentAudit> findByBizNo(String bizNo) {
        DocumentAuditPO po = documentAuditMapper.findByBizNo(bizNo);
        return Optional.ofNullable(po).map(this::toDomain);
    }

    @Override
    public void save(DocumentAudit audit) {
        DocumentAuditPO po = toPO(audit);
        if (po.getId() == null) {
            documentAuditMapper.insert(po);
        } else {
            documentAuditMapper.updateById(po);
        }
    }

    private DocumentAudit toDomain(DocumentAuditPO po) {
        return DocumentAudit.reconstitute(
                po.getId(), po.getAuditId(), po.getBizNo(), po.getDocType(),
                po.getOcrResult(), po.getComparisonResult(),
                po.getIsConsistent() != null && po.getIsConsistent() == 1,
                po.getDiscrepancyDetail(), po.getAuditOpinion(), po.getConfidenceScore(),
                po.getCreateTime(), po.getUpdateTime(), po.getVersion());
    }

    private DocumentAuditPO toPO(DocumentAudit audit) {
        DocumentAuditPO po = new DocumentAuditPO();
        po.setId(audit.getId());
        po.setAuditId(audit.getAuditId());
        po.setBizNo(audit.getBizNo());
        po.setDocType(audit.getDocType());
        po.setOcrResult(audit.getOcrResult());
        po.setComparisonResult(audit.getComparisonResult());
        po.setIsConsistent(audit.getIsConsistent() != null ? (audit.getIsConsistent() ? 1 : 0) : null);
        po.setDiscrepancyDetail(audit.getDiscrepancyDetail());
        po.setAuditOpinion(audit.getAuditOpinion());
        po.setConfidenceScore(audit.getConfidenceScore());
        return po;
    }
}
