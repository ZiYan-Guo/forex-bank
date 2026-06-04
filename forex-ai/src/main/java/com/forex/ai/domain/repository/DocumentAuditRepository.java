package com.forex.ai.domain.repository;

import com.forex.ai.domain.model.aggregate.DocumentAudit;

import java.util.Optional;

public interface DocumentAuditRepository {

    Optional<DocumentAudit> findByAuditId(String auditId);

    Optional<DocumentAudit> findByBizNo(String bizNo);

    void save(DocumentAudit audit);
}
