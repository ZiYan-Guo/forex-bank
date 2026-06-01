package com.forex.exchange.infrastructure.mapper;

import com.forex.common.mybatis.base.BaseMapperExt;
import com.forex.exchange.infrastructure.persistence.ExchangeOrderPO;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.util.List;

@Mapper
public interface ExchangeOrderMapper extends BaseMapperExt<ExchangeOrderPO> {

    @Select("SELECT * FROM t_exchange_order WHERE order_no = #{orderNo} AND deleted = 0")
    ExchangeOrderPO selectByOrderNo(@Param("orderNo") String orderNo);

    @Select("SELECT * FROM t_exchange_order WHERE customer_id = #{customerId} AND deleted = 0 ORDER BY create_time DESC")
    List<ExchangeOrderPO> selectByCustomerId(@Param("customerId") Long customerId);

    @Select("<script>" +
            "SELECT * FROM t_exchange_order WHERE deleted = 0" +
            "<if test='query.orderNo != null and query.orderNo != \"\"'>" +
            " AND order_no = #{query.orderNo}" +
            "</if>" +
            "<if test='query.customerId != null'>" +
            " AND customer_id = #{query.customerId}" +
            "</if>" +
            "<if test='query.orderType != null and query.orderType != \"\"'>" +
            " AND order_type = #{query.orderType}" +
            "</if>" +
            "<if test='query.orderStatus != null and query.orderStatus != \"\"'>" +
            " AND order_status = #{query.orderStatus}" +
            "</if>" +
            "<if test='query.dealType != null and query.dealType != \"\"'>" +
            " AND deal_type = #{query.dealType}" +
            "</if>" +
            "<if test='query.baseCurrency != null and query.baseCurrency != \"\"'>" +
            " AND base_currency = #{query.baseCurrency}" +
            "</if>" +
            "<if test='query.quoteCurrency != null and query.quoteCurrency != \"\"'>" +
            " AND quote_currency = #{query.quoteCurrency}" +
            "</if>" +
            " ORDER BY create_time DESC" +
            "</script>")
    Page<ExchangeOrderPO> pageQuery(Page<ExchangeOrderPO> page, @Param("query") com.forex.exchange.domain.model.query.ExchangeOrderQuery query);
}
