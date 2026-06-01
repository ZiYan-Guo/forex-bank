package com.forex.account.infrastructure.mapper;

import com.forex.account.infrastructure.persistence.ForexAccountPO;
import com.forex.common.mybatis.base.BaseMapperExt;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.util.List;

@Mapper
public interface ForexAccountMapper extends BaseMapperExt<ForexAccountPO> {

    @Select("SELECT * FROM t_forex_account WHERE account_no = #{accountNo} AND deleted = 0")
    ForexAccountPO selectByAccountNo(@Param("accountNo") String accountNo);

    @Select("SELECT * FROM t_forex_account WHERE customer_id = #{customerId} AND deleted = 0")
    List<ForexAccountPO> selectByCustomerId(@Param("customerId") Long customerId);
}
