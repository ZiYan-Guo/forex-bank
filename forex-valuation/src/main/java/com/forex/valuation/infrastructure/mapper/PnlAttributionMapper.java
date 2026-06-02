package com.forex.valuation.infrastructure.mapper;

import com.forex.common.mybatis.base.BaseMapperExt;
import com.forex.valuation.infrastructure.persistence.PnlAttributionPO;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.util.List;

@Mapper
public interface PnlAttributionMapper extends BaseMapperExt<PnlAttributionPO> {

    @Select("SELECT * FROM t_pnl_attribution WHERE trade_id = #{tradeId} AND deleted = 0 ORDER BY attrib_date DESC")
    List<PnlAttributionPO> selectByTradeId(@Param("tradeId") Long tradeId);
}
