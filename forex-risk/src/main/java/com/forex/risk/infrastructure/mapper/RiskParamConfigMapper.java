package com.forex.risk.infrastructure.mapper;

import com.forex.common.mybatis.base.BaseMapperExt;
import com.forex.risk.infrastructure.persistence.RiskParamConfigPO;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.util.List;

@Mapper
public interface RiskParamConfigMapper extends BaseMapperExt<RiskParamConfigPO> {

    @Select("SELECT * FROM t_risk_param_config WHERE param_key = #{paramKey} AND is_enabled = 1")
    RiskParamConfigPO findByParamKey(@Param("paramKey") String paramKey);

    @Select("SELECT * FROM t_risk_param_config WHERE param_type = #{paramType} AND is_enabled = 1")
    List<RiskParamConfigPO> findByParamType(@Param("paramType") String paramType);
}
