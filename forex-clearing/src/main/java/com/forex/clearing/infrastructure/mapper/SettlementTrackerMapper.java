package com.forex.clearing.infrastructure.mapper;

import com.forex.clearing.infrastructure.persistence.SettlementTrackerPO;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.util.List;

@Mapper
public interface SettlementTrackerMapper extends BaseMapper<SettlementTrackerPO> {

    @Select("SELECT * FROM t_settlement_tracker WHERE tracking_id = #{trackingId}")
    SettlementTrackerPO selectByTrackingId(@Param("trackingId") String trackingId);

    @Select("SELECT * FROM t_settlement_tracker ORDER BY create_time DESC")
    List<SettlementTrackerPO> selectAll();
}
