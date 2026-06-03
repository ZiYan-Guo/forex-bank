package com.forex.clearing.infrastructure.mapper;

import com.forex.clearing.infrastructure.persistence.ClsSessionPO;
import com.forex.common.mybatis.base.BaseMapperExt;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.time.LocalDate;

@Mapper
public interface ClsSessionMapper extends BaseMapperExt<ClsSessionPO> {

    @Select("SELECT * FROM t_cls_session WHERE session_id = #{sessionId} AND deleted = 0")
    ClsSessionPO selectBySessionId(@Param("sessionId") String sessionId);

    @Select("SELECT * FROM t_cls_session WHERE settlement_date = #{settlementDate} AND deleted = 0")
    ClsSessionPO selectBySettlementDate(@Param("settlementDate") LocalDate settlementDate);
}
