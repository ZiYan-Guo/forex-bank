package com.forex.rate.infrastructure.mapper;

import com.forex.rate.infrastructure.persistence.RatePublishConfigPO;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.util.List;

/**
 * Rate publish config MyBatis mapper.
 * 汇率发布配置数据访问层。
 */
@Mapper
public interface RatePublishConfigMapper extends BaseMapper<RatePublishConfigPO> {

    @Select("SELECT * FROM t_rate_publish_config WHERE is_enabled = 1")
    List<RatePublishConfigPO> selectAllEnabled();
}
