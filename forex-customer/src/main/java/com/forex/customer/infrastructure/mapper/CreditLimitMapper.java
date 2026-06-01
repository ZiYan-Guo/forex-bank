package com.forex.customer.infrastructure.mapper;

import com.forex.common.mybatis.base.BaseMapperExt;
import com.forex.customer.infrastructure.persistence.CreditLimitPO;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.util.List;

@Mapper
public interface CreditLimitMapper extends BaseMapperExt<CreditLimitPO> {

    @Select("SELECT * FROM t_customer_credit_limit WHERE customer_id = #{customerId} AND limit_type = #{limitType} AND currency = #{currency} AND deleted = 0")
    CreditLimitPO selectByCustomerAndType(@Param("customerId") Long customerId, @Param("limitType") String limitType, @Param("currency") String currency);

    @Select("SELECT * FROM t_customer_credit_limit WHERE customer_id = #{customerId} AND deleted = 0")
    List<CreditLimitPO> selectByCustomerId(@Param("customerId") Long customerId);
}
