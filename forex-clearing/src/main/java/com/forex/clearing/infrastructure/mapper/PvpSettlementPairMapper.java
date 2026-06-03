package com.forex.clearing.infrastructure.mapper;

import com.forex.clearing.infrastructure.persistence.PvpSettlementPairPO;
import com.forex.common.mybatis.base.BaseMapperExt;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

@Mapper
public interface PvpSettlementPairMapper extends BaseMapperExt<PvpSettlementPairPO> {

    @Select("SELECT * FROM t_pvp_settlement_pair WHERE pair_id = #{pairId} AND deleted = 0")
    PvpSettlementPairPO selectByPairId(@Param("pairId") String pairId);
}
