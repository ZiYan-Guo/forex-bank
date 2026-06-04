package com.forex.ai.domain.repository;

import com.forex.ai.domain.model.aggregate.RiskAiAssessment;

import java.util.List;
import java.util.Optional;

public interface RiskAiAssessmentRepository {

    Optional<RiskAiAssessment> findByAssessmentId(String assessmentId);

    List<RiskAiAssessment> findByCustomerId(Long customerId);

    void save(RiskAiAssessment assessment);
}
