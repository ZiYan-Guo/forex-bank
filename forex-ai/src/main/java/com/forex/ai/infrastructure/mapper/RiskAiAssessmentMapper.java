package com.forex.ai.infrastructure.mapper;

import com.forex.common.mybatis.base.BaseMapperExt;
import com.forex.ai.infrastructure.persistence.RiskAiAssessmentPO;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.util.List;

@Mapper
public interface RiskAiAssessmentMapper extends BaseMapperExt<RiskAiAssessmentPO> {

    @Select("SELECT * FROM t_risk_ai_assessment WHERE assessment_id = #{assessmentId} AND deleted = 0")
    RiskAiAssessmentPO findByAssessmentId(@Param("assessmentId") String assessmentId);

    @Select("SELECT * FROM t_risk_ai_assessment WHERE customer_id = #{customerId} AND deleted = 0 ORDER BY create_time DESC")
    List<RiskAiAssessmentPO> findByCustomerId(@Param("customerId") Long customerId);
}
