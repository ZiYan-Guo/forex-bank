package com.forex.risk.infrastructure.mapper;

import com.forex.risk.infrastructure.persistence.SamplingRulePO;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.util.List;

@Mapper
public interface SamplingRuleMapper extends BaseMapper<SamplingRulePO> {

    @Select("SELECT * FROM t_sampling_rule WHERE rule_code = #{ruleCode} AND deleted = 0")
    SamplingRulePO selectByRuleCode(@Param("ruleCode") String ruleCode);

    @Select("SELECT * FROM t_sampling_rule WHERE status = 'ACTIVE' AND deleted = 0 AND (effective_date IS NULL OR effective_date <= CURDATE()) AND (expire_date IS NULL OR expire_date >= CURDATE())")
    List<SamplingRulePO> selectAllActive();
}
