package com.forex.trading.infrastructure.mapper;

import com.forex.common.mybatis.base.BaseMapperExt;
import com.forex.trading.domain.model.query.TradeQuery;
import com.forex.trading.infrastructure.persistence.FxTradePO;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

@Mapper
public interface FxTradeMapper extends BaseMapperExt<FxTradePO> {

    @Select("SELECT * FROM t_fx_trade WHERE trade_no = #{tradeNo} AND deleted = 0")
    FxTradePO selectByTradeNo(@Param("tradeNo") String tradeNo);

    @Select("<script>" +
            "SELECT * FROM t_fx_trade WHERE deleted = 0 " +
            "<if test='query.tradeNo != null and query.tradeNo != \"\"'>AND trade_no = #{query.tradeNo}</if> " +
            "<if test='query.customerId != null'>AND customer_id = #{query.customerId}</if> " +
            "<if test='query.tradeType != null and query.tradeType != \"\"'>AND trade_type = #{query.tradeType}</if> " +
            "<if test='query.tradeStatus != null and query.tradeStatus != \"\"'>AND trade_status = #{query.tradeStatus}</if> " +
            "<if test='query.dealType != null and query.dealType != \"\"'>AND deal_type = #{query.dealType}</if> " +
            "<if test='query.startDate != null'>AND create_time &gt;= #{query.startDate}</if> " +
            "<if test='query.endDate != null'>AND create_time &lt;= #{query.endDate}</if> " +
            "ORDER BY create_time DESC" +
            "</script>")
    IPage<FxTradePO> pageQuery(Page<FxTradePO> page, @Param("query") TradeQuery query);
}
