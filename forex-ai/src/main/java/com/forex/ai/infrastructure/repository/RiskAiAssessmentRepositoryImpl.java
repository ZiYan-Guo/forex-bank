package com.forex.ai.infrastructure.repository;

import com.forex.ai.domain.model.aggregate.RiskAiAssessment;
import com.forex.ai.domain.repository.RiskAiAssessmentRepository;
import com.forex.ai.infrastructure.mapper.RiskAiAssessmentMapper;
import com.forex.ai.infrastructure.persistence.RiskAiAssessmentPO;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
@RequiredArgsConstructor
public class RiskAiAssessmentRepositoryImpl implements RiskAiAssessmentRepository {

    private final RiskAiAssessmentMapper riskAiAssessmentMapper;

    @Override
    public Optional<RiskAiAssessment> findByAssessmentId(String assessmentId) {
        RiskAiAssessmentPO po = riskAiAssessmentMapper.findByAssessmentId(assessmentId);
        return Optional.ofNullable(po).map(this::toDomain);
    }

    @Override
    public List<RiskAiAssessment> findByCustomerId(Long customerId) {
        return riskAiAssessmentMapper.findByCustomerId(customerId).stream()
                .map(this::toDomain)
                .toList();
    }

    @Override
    public void save(RiskAiAssessment assessment) {
        RiskAiAssessmentPO po = toPO(assessment);
        if (po.getId() == null) {
            riskAiAssessmentMapper.insert(po);
        } else {
            riskAiAssessmentMapper.updateById(po);
        }
    }

    private RiskAiAssessment toDomain(RiskAiAssessmentPO po) {
        return RiskAiAssessment.reconstitute(
                po.getId(), po.getAssessmentId(), po.getCustomerId(), po.getBizNo(),
                po.getRiskType(), po.getRiskScore(), po.getRiskLevel(),
                po.getAiAnalysis(), po.getRecommendation(), po.getDataPointsJson(),
                po.getCreateTime(), po.getUpdateTime(), po.getVersion());
    }

    private RiskAiAssessmentPO toPO(RiskAiAssessment assessment) {
        RiskAiAssessmentPO po = new RiskAiAssessmentPO();
        po.setId(assessment.getId());
        po.setAssessmentId(assessment.getAssessmentId());
        po.setCustomerId(assessment.getCustomerId());
        po.setBizNo(assessment.getBizNo());
        po.setRiskType(assessment.getRiskType());
        po.setRiskScore(assessment.getRiskScore());
        po.setRiskLevel(assessment.getRiskLevel());
        po.setAiAnalysis(assessment.getAiAnalysis());
        po.setRecommendation(assessment.getRecommendation());
        po.setDataPointsJson(assessment.getDataPointsJson());
        return po;
    }
}
