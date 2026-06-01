package com.forex.account.infrastructure.mapper;

import com.forex.account.infrastructure.persistence.AccountTransactionPO;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.util.List;

@Mapper
public interface AccountTransactionMapper extends BaseMapper<AccountTransactionPO> {

    @Select("SELECT * FROM t_account_transaction WHERE account_id = #{accountId} ORDER BY transaction_time DESC LIMIT #{offset}, #{limit}")
    List<AccountTransactionPO> selectByAccountId(@Param("accountId") Long accountId,
                                                  @Param("offset") int offset,
                                                  @Param("limit") int limit);

    @Select("SELECT * FROM t_account_transaction WHERE transaction_no = #{transactionNo}")
    AccountTransactionPO selectByTransactionNo(@Param("transactionNo") String transactionNo);
}
