package com.forex.cashpool.infrastructure.mapper;

import com.forex.cashpool.infrastructure.persistence.PoolMemberPO;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.util.List;

/**
 * Pool member MyBatis mapper.
 * 资金池成员数据访问层。
 */
@Mapper
public interface PoolMemberMapper extends BaseMapper<PoolMemberPO> {

    @Select("SELECT * FROM t_pool_member WHERE pool_id = #{poolId} AND deleted = 0")
    List<PoolMemberPO> selectByPoolId(@Param("poolId") String poolId);
}
