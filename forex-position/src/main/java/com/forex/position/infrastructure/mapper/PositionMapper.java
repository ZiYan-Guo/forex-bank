package com.forex.position.infrastructure.mapper;

import com.forex.common.mybatis.base.BaseMapperExt;
import com.forex.position.infrastructure.persistence.PositionPO;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.time.LocalDate;
import java.util.List;

@Mapper
public interface PositionMapper extends BaseMapperExt<PositionPO> {

    @Select("SELECT * FROM t_position WHERE position_no = #{positionNo} AND deleted = 0")
    PositionPO selectByPositionNo(@Param("positionNo") String positionNo);

    @Select("<script>" +
            "SELECT * FROM t_position WHERE deleted = 0" +
            "<if test='query.currencyPair != null and query.currencyPair != \"\"'>" +
            " AND currency_pair = #{query.currencyPair}" +
            "</if>" +
            "<if test='query.positionType != null and query.positionType != \"\"'>" +
            " AND position_type = #{query.positionType}" +
            "</if>" +
            "<if test='query.positionCurrency != null and query.positionCurrency != \"\"'>" +
            " AND position_currency = #{query.positionCurrency}" +
            "</if>" +
            "<if test='query.positionDate != null'>" +
            " AND position_date = #{query.positionDate}" +
            "</if>" +
            "<if test='query.riskLevel != null and query.riskLevel != \"\"'>" +
            " AND risk_level = #{query.riskLevel}" +
            "</if>" +
            "<if test='query.traderId != null'>" +
            " AND trader_id = #{query.traderId}" +
            "</if>" +
            " ORDER BY create_time DESC" +
            "</script>")
    Page<PositionPO> pageQuery(Page<PositionPO> page, @Param("query") com.forex.position.domain.model.query.PositionQuery query);

    @Select("SELECT * FROM t_position WHERE currency_pair = #{currencyPair} AND position_date = #{date} AND deleted = 0")
    List<PositionPO> selectByCurrencyPairAndDate(@Param("currencyPair") String currencyPair,
                                                  @Param("date") LocalDate date);
}
