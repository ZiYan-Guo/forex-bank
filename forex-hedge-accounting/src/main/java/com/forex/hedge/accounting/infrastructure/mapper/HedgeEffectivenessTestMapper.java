package com.forex.hedge.accounting.infrastructure.mapper;

import com.forex.hedge.accounting.infrastructure.persistence.HedgeEffectivenessTestPO;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.util.List;

/**
 * Hedge effectiveness test MyBatis mapper.
 * 套期有效性测试数据访问层。
 */
@Mapper
public interface HedgeEffectivenessTestMapper extends BaseMapper<HedgeEffectivenessTestPO> {

    @Select("SELECT * FROM t_hedge_effectiveness_test WHERE relation_id = #{relationId}")
    List<HedgeEffectivenessTestPO> selectByRelationId(@Param("relationId") String relationId);
}
