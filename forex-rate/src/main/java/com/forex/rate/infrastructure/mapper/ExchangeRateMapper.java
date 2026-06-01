package com.forex.rate.infrastructure.mapper;

import java.time.LocalDate;
import java.util.List;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import com.forex.common.mybatis.base.BaseMapperExt;
import com.forex.rate.infrastructure.persistence.ExchangeRatePO;

@Mapper
public interface ExchangeRateMapper extends BaseMapperExt<ExchangeRatePO> {

    @Select("SELECT * FROM t_exchange_rate WHERE currency_pair = #{currencyPair} AND deleted = 0 ORDER BY rate_time DESC LIMIT 1")
    ExchangeRatePO selectLatestByCurrencyPair(@Param("currencyPair") String currencyPair);

    @Select("SELECT t.* FROM t_exchange_rate t INNER JOIN (SELECT currency_pair, MAX(rate_time) AS max_time FROM t_exchange_rate WHERE deleted = 0 GROUP BY currency_pair) latest ON t.currency_pair = latest.currency_pair AND t.rate_time = latest.max_time WHERE t.deleted = 0")
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
}
