package com.forex.reporting.infrastructure.mapper;

import com.forex.common.mybatis.base.BaseMapperExt;
import com.forex.reporting.infrastructure.persistence.CapitalReportPO;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

@Mapper
public interface CapitalReportMapper extends BaseMapperExt<CapitalReportPO> {

    @Select("SELECT * FROM t_capital_account_report WHERE report_no = #{reportNo} AND deleted = 0")
    CapitalReportPO selectByReportNo(@Param("reportNo") String reportNo);
}
