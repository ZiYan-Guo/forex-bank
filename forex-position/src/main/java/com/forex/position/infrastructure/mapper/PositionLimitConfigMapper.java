package com.forex.position.infrastructure.mapper;

import com.forex.common.mybatis.base.BaseMapperExt;
import com.forex.position.infrastructure.persistence.PositionLimitConfigPO;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.util.List;

@Mapper
public interface PositionLimitConfigMapper extends BaseMapperExt<PositionLimitConfigPO> {

    @Select("SELECT * FROM t_position_limit_config WHERE currency = #{currency} AND limit_type = #{limitType} AND is_enabled = 1 AND deleted = 0")
    List<PositionLimitConfigPO> selectByCurrencyAndType(@Param("currency") String currency,
                                                         @Param("limitType") String limitType);

    @Select("SELECT * FROM t_position_limit_config WHERE currency = #{currency} AND deleted = 0")
    List<PositionLimitConfigPO> selectByCurrency(@Param("currency") String currency);

    @Select("SELECT * FROM t_position_limit_config WHERE is_enabled = 1 AND deleted = 0")
    List<PositionLimitConfigPO> selectAllEnabled();
}
