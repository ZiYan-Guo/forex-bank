package com.forex.trading.infrastructure.mapper;

import com.forex.trading.infrastructure.persistence.TradeLifecyclePO;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.util.List;

@Mapper
public interface TradeLifecycleMapper extends BaseMapper<TradeLifecyclePO> {

    @Select("SELECT * FROM t_trade_lifecycle WHERE trade_id = #{tradeId} ORDER BY event_time DESC")
    List<TradeLifecyclePO> selectByTradeId(@Param("tradeId") Long tradeId);
}
