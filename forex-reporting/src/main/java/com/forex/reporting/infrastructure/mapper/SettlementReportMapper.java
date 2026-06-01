package com.forex.reporting.infrastructure.mapper;

import com.forex.common.mybatis.base.BaseMapperExt;
import com.forex.reporting.infrastructure.persistence.SettlementReportPO;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

@Mapper
public interface SettlementReportMapper extends BaseMapperExt<SettlementReportPO> {

    @Select("SELECT * FROM t_forex_settlement_report WHERE report_no = #{reportNo} AND deleted = 0")
    SettlementReportPO selectByReportNo(@Param("reportNo") String reportNo);

    @Select("SELECT * FROM t_forex_settlement_report WHERE exchange_order_no = #{exchangeOrderNo} AND deleted = 0")
    SettlementReportPO selectByExchangeOrderNo(@Param("exchangeOrderNo") String exchangeOrderNo);
}
