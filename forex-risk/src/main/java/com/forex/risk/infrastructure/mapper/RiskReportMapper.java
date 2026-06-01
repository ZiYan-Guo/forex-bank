package com.forex.risk.infrastructure.mapper;

import com.forex.risk.infrastructure.persistence.RiskReportPO;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

@Mapper
public interface RiskReportMapper extends BaseMapper<RiskReportPO> {

    @Select("SELECT * FROM t_risk_report WHERE report_no = #{reportNo}")
    RiskReportPO selectByReportNo(@Param("reportNo") String reportNo);
}
