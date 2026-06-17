package com.forex.margin.infrastructure.mapper;

import com.forex.margin.infrastructure.persistence.MarginCallPO;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.util.List;

@Mapper
public interface MarginCallMapper extends BaseMapper<MarginCallPO> {

    @Select("SELECT * FROM t_margin_call WHERE margin_id = #{marginId}")
    List<MarginCallPO> selectByMarginId(@Param("marginId") Long marginId);

    @Select("SELECT * FROM t_margin_call WHERE id = #{id}")
    MarginCallPO selectByCallId(@Param("id") Long id);
}
