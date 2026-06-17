package com.forex.cashpool.infrastructure.mapper;

import com.forex.cashpool.infrastructure.persistence.OverseasLendingPO;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

/**
 * Overseas lending MyBatis mapper.
 * 境外放款数据访问层。
 */
@Mapper
public interface OverseasLendingMapper extends BaseMapper<OverseasLendingPO> {

    @Select("SELECT * FROM t_overseas_lending WHERE contract_no = #{contractNo} AND deleted = 0")
    OverseasLendingPO selectByContractNo(@Param("contractNo") String contractNo);

    @Select("SELECT * FROM t_overseas_lending WHERE customer_id = #{customerId} AND deleted = 0 ORDER BY create_time DESC")
    java.util.List<OverseasLendingPO> selectByCustomerId(@Param("customerId") Long customerId);
}
