package com.forex.exchange.infrastructure.mapper;

import com.forex.exchange.infrastructure.persistence.ExchangeQuotePO;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

@Mapper
public interface ExchangeQuoteMapper extends BaseMapper<ExchangeQuotePO> {

    @Select("SELECT * FROM t_exchange_quote WHERE customer_id = #{customerId}" +
            " AND base_currency = #{baseCcy} AND quote_currency = #{quoteCcy}" +
            " ORDER BY quote_time DESC LIMIT 1")
    ExchangeQuotePO selectLatestQuote(@Param("customerId") Long customerId,
                                       @Param("baseCcy") String baseCcy,
                                       @Param("quoteCcy") String quoteCcy);
}
