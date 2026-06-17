package com.forex.cashpool.infrastructure.mapper;

import com.forex.cashpool.infrastructure.persistence.CashPoolPO;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

/**
 * Cash pool MyBatis mapper.
 * 资金池数据访问层。
 */
@Mapper
public interface CashPoolMapper extends BaseMapper<CashPoolPO> {

    @Select("SELECT * FROM t_cash_pool WHERE pool_id = #{poolId} AND deleted = 0")
    CashPoolPO selectByPoolId(@Param("poolId") String poolId);
}
