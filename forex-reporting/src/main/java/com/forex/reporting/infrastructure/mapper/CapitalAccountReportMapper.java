package com.forex.reporting.infrastructure.mapper;

import com.forex.reporting.infrastructure.persistence.CapitalAccountReportPO;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;

import org.apache.ibatis.annotations.Mapper;

/**
 * Capital account report MyBatis mapper.
 * 资本项目账户报告数据访问层。
 */
@Mapper
public interface CapitalAccountReportMapper extends BaseMapper<CapitalAccountReportPO> {
}
