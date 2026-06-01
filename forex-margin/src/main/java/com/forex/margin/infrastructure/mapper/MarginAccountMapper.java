package com.forex.margin.infrastructure.mapper;

import com.forex.common.mybatis.base.BaseMapperExt;
import com.forex.margin.infrastructure.persistence.MarginAccountPO;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.util.List;

@Mapper
public interface MarginAccountMapper extends BaseMapperExt<MarginAccountPO> {

    @Select("SELECT * FROM t_margin_account WHERE margin_no = #{marginNo} AND deleted = 0")
    MarginAccountPO selectByMarginNo(@Param("marginNo") String marginNo);

    @Select("SELECT * FROM t_margin_account WHERE customer_id = #{customerId} AND deleted = 0 ORDER BY create_time DESC")
    List<MarginAccountPO> selectByCustomerId(@Param("customerId") Long customerId);

    @Select("<script>" +
            "SELECT * FROM t_margin_account WHERE deleted = 0" +
            "<if test='query.customerId != null'>" +
            " AND customer_id = #{query.customerId}" +
            "</if>" +
            "<if test='query.tradeId != null'>" +
            " AND trade_id = #{query.tradeId}" +
            "</if>" +
            "<if test='query.marginNo != null and query.marginNo != \"\"'>" +
            " AND margin_no = #{query.marginNo}" +
            "</if>" +
            "<if test='query.marginType != null and query.marginType != \"\"'>" +
            " AND margin_type = #{query.marginType}" +
            "</if>" +
            "<if test='query.status != null and query.status != \"\"'>" +
            " AND status = #{query.status}" +
            "</if>" +
            " ORDER BY create_time DESC" +
            "</script>")
    Page<MarginAccountPO> pageQuery(Page<MarginAccountPO> page, @Param("query") com.forex.margin.domain.model.query.MarginQuery query);
}
