package com.forex.hedge.accounting.infrastructure.mapper;

import com.forex.hedge.accounting.infrastructure.persistence.HedgeRelationshipPO;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

/**
 * Hedge relationship MyBatis mapper.
 * 套期关系数据访问层。
 */
@Mapper
public interface HedgeRelationshipMapper extends BaseMapper<HedgeRelationshipPO> {

    @Select("SELECT * FROM t_hedge_relationship WHERE relation_id = #{relationId} AND deleted = 0")
    HedgeRelationshipPO selectByRelationId(@Param("relationId") String relationId);

    @Select("SELECT * FROM t_hedge_relationship WHERE customer_id = #{customerId} AND deleted = 0 ORDER BY create_time DESC")
    java.util.List<HedgeRelationshipPO> selectByCustomerId(@Param("customerId") Long customerId);
}
