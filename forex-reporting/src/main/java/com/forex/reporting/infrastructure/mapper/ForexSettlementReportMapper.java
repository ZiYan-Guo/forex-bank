package com.forex.reporting.infrastructure.mapper;

import com.forex.reporting.infrastructure.persistence.ForexSettlementReportPO;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;

import org.apache.ibatis.annotations.Mapper;

/**
 * Forex settlement report MyBatis mapper.
 * 外汇结算报告数据访问层。
 */
@Mapper
public interface ForexSettlementReportMapper extends BaseMapper<ForexSettlementReportPO> {
}
