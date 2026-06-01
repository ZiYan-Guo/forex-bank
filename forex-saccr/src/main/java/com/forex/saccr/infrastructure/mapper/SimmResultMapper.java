package com.forex.saccr.infrastructure.mapper;

import com.forex.saccr.infrastructure.persistence.SimmResultPO;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

@Mapper
public interface SimmResultMapper extends BaseMapper<SimmResultPO> {

    @Select("SELECT * FROM t_simm_result WHERE calc_no = #{calcNo}")
    SimmResultPO selectByCalcNo(@Param("calcNo") String calcNo);

    @Select("<script>" +
            "SELECT * FROM t_simm_result WHERE 1=1" +
            "<if test='query.tradeId != null'>" +
            " AND trade_id = #{query.tradeId}" +
            "</if>" +
            "<if test='query.tradeNo != null and query.tradeNo != \"\"'>" +
            " AND trade_no = #{query.tradeNo}" +
            "</if>" +
            "<if test='query.calcMethod != null and query.calcMethod != \"\"'>" +
            " AND calc_method = #{query.calcMethod}" +
            "</if>" +
            " ORDER BY create_time DESC" +
            "</script>")
    Page<SimmResultPO> pageQuery(Page<SimmResultPO> page, @Param("query") com.forex.saccr.domain.model.query.SimmQuery query);
}
