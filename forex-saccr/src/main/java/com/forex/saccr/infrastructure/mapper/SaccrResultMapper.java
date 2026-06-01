package com.forex.saccr.infrastructure.mapper;

import com.forex.saccr.infrastructure.persistence.SaccrResultPO;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

@Mapper
public interface SaccrResultMapper extends BaseMapper<SaccrResultPO> {

    @Select("SELECT * FROM t_saccr_result WHERE calc_no = #{calcNo}")
    SaccrResultPO selectByCalcNo(@Param("calcNo") String calcNo);

    @Select("<script>" +
            "SELECT * FROM t_saccr_result WHERE 1=1" +
            "<if test='query.tradeId != null'>" +
            " AND trade_id = #{query.tradeId}" +
            "</if>" +
            "<if test='query.tradeNo != null and query.tradeNo != \"\"'>" +
            " AND trade_no = #{query.tradeNo}" +
            "</if>" +
            "<if test='query.counterPartyId != null and query.counterPartyId != \"\"'>" +
            " AND counter_party_id = #{query.counterPartyId}" +
            "</if>" +
            "<if test='query.calcMethod != null and query.calcMethod != \"\"'>" +
            " AND calc_method = #{query.calcMethod}" +
            "</if>" +
            " ORDER BY create_time DESC" +
            "</script>")
    Page<SaccrResultPO> pageQuery(Page<SaccrResultPO> page, @Param("query") com.forex.saccr.domain.model.query.SaccrQuery query);
}
