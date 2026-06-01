package com.forex.valuation.infrastructure.mapper;

import com.forex.common.mybatis.base.BaseMapperExt;
import com.forex.valuation.infrastructure.persistence.ValuationResultPO;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.util.List;

@Mapper
public interface ValuationResultMapper extends BaseMapperExt<ValuationResultPO> {

    @Select("SELECT * FROM t_valuation_result WHERE trade_id = #{tradeId} AND deleted = 0 ORDER BY create_time DESC")
    List<ValuationResultPO> selectByTradeId(@Param("tradeId") Long tradeId);

    @Select("<script>" +
            "SELECT * FROM t_valuation_result WHERE deleted = 0" +
            "<if test='query.tradeId != null'>" +
            " AND trade_id = #{query.tradeId}" +
            "</if>" +
            "<if test='query.tradeType != null and query.tradeType != \"\"'>" +
            " AND trade_type = #{query.tradeType}" +
            "</if>" +
            "<if test='query.currencyPair != null and query.currencyPair != \"\"'>" +
            " AND currency_pair = #{query.currencyPair}" +
            "</if>" +
            "<if test='query.startDate != null'>" +
            " AND valuation_date &gt;= #{query.startDate}" +
            "</if>" +
            "<if test='query.endDate != null'>" +
            " AND valuation_date &lt;= #{query.endDate}" +
            "</if>" +
            " ORDER BY create_time DESC" +
            "</script>")
    Page<ValuationResultPO> pageQuery(Page<ValuationResultPO> page, @Param("query") com.forex.valuation.domain.model.query.ValuationQuery query);
}
