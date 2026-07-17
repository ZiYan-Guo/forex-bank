package com.forex.rate.infrastructure.mapper;

import java.time.LocalDate;
import java.util.List;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import com.forex.common.mybatis.base.BaseMapperExt;
import com.forex.rate.application.query.RateQuery;
import com.forex.rate.infrastructure.persistence.ExchangeRatePO;

@Mapper
public interface ExchangeRateMapper extends BaseMapperExt<ExchangeRatePO> {

    @Select("SELECT * FROM t_exchange_rate WHERE currency_pair = #{currencyPair} AND status = 1 AND deleted = 0 ORDER BY rate_time DESC LIMIT 1")
    ExchangeRatePO selectLatestByCurrencyPair(@Param("currencyPair") String currencyPair);

    @Select("SELECT t.* FROM t_exchange_rate t INNER JOIN (SELECT currency_pair, MAX(rate_time) AS max_time FROM t_exchange_rate WHERE status = 1 AND deleted = 0 GROUP BY currency_pair) latest ON t.currency_pair = latest.currency_pair AND t.rate_time = latest.max_time WHERE t.status = 1 AND t.deleted = 0")
    List<ExchangeRatePO> selectLatestRates();

    @Select("<script>" +
            "SELECT * FROM t_exchange_rate WHERE deleted = 0 " +
            "<if test='currencyPair != null and currencyPair != \"\"'>AND currency_pair = #{currencyPair}</if> " +
            "AND rate_date BETWEEN #{startDate} AND #{endDate} " +
            "ORDER BY rate_time DESC" +
            "</script>")
    List<ExchangeRatePO> selectByDateRange(@Param("startDate") LocalDate startDate,
                                           @Param("endDate") LocalDate endDate,
                                           @Param("currencyPair") String currencyPair);

    /**
     * 分页查询历史牌价，按时间倒序返回最新记录在前。
     * Pages historical exchange rates in descending rate-time order.
     */
    @Select("<script>" +
            "SELECT * FROM t_exchange_rate WHERE deleted = 0" +
            "<if test='query.currencyPair != null and query.currencyPair != \"\"'> AND currency_pair = #{query.currencyPair}</if>" +
            "<if test='query.baseCurrency != null and query.baseCurrency != \"\"'> AND base_currency = #{query.baseCurrency}</if>" +
            "<if test='query.quoteCurrency != null and query.quoteCurrency != \"\"'> AND quote_currency = #{query.quoteCurrency}</if>" +
            "<if test='query.rateSource != null and query.rateSource != \"\"'> AND rate_source = #{query.rateSource}</if>" +
            "<if test='query.status != null'> AND status = #{query.status}</if>" +
            "<if test='query.rateDate != null'> AND rate_date = #{query.rateDate}</if>" +
            "<if test='query.startDate != null'> AND rate_date &gt;= #{query.startDate}</if>" +
            "<if test='query.endDate != null'> AND rate_date &lt;= #{query.endDate}</if>" +
            " ORDER BY rate_time DESC" +
            "</script>")
    Page<ExchangeRatePO> pageQuery(Page<ExchangeRatePO> page, @Param("query") RateQuery query);
}
