package com.forex.rate.infrastructure.mapper;

import com.forex.rate.infrastructure.persistence.RatePublishLogPO;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.util.List;

/**
 * Rate publish log MyBatis mapper.
 * 汇率发布日志数据访问层。
 */
@Mapper
public interface RatePublishLogMapper extends BaseMapper<RatePublishLogPO> {

    @Select("SELECT * FROM t_rate_publish_log ORDER BY create_time DESC")
    List<RatePublishLogPO> selectRecentLogs();
}
