package com.forex.clearing.infrastructure.mapper;

import com.forex.clearing.infrastructure.persistence.TradeConfirmationPO;
import com.forex.common.mybatis.base.BaseMapperExt;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.util.List;

@Mapper
public interface TradeConfirmationMapper extends BaseMapperExt<TradeConfirmationPO> {

    @Select("SELECT * FROM t_confirmation_match WHERE confirm_id = #{confirmId} AND deleted = 0")
    TradeConfirmationPO selectByConfirmId(@Param("confirmId") String confirmId);

    @Select("SELECT * FROM t_confirmation_match WHERE trade_no = #{tradeNo} AND deleted = 0")
    TradeConfirmationPO selectByTradeNo(@Param("tradeNo") String tradeNo);

    @Select("SELECT * FROM t_confirmation_match WHERE match_status = #{matchStatus} AND deleted = 0")
    List<TradeConfirmationPO> selectByMatchStatus(@Param("matchStatus") String matchStatus);

    @Select("SELECT * FROM t_confirmation_match WHERE deleted = 0 ORDER BY create_time DESC")
    List<TradeConfirmationPO> selectAll();
}
