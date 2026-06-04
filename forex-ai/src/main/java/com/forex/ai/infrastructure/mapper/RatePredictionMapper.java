package com.forex.ai.infrastructure.mapper;

import com.forex.common.mybatis.base.BaseMapperExt;
import com.forex.ai.infrastructure.persistence.RatePredictionPO;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.util.List;

@Mapper
public interface RatePredictionMapper extends BaseMapperExt<RatePredictionPO> {

    @Select("SELECT * FROM t_rate_prediction WHERE pred_no = #{predNo} AND deleted = 0")
    RatePredictionPO findByPredNo(@Param("predNo") String predNo);

    @Select("SELECT * FROM t_rate_prediction WHERE currency_pair = #{currencyPair} AND pred_type = #{predType} AND deleted = 0 ORDER BY target_time DESC")
    List<RatePredictionPO> findByCurrencyPairAndPredType(@Param("currencyPair") String currencyPair, @Param("predType") String predType);
}
