package com.forex.schedule.infrastructure.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.forex.schedule.infrastructure.persistence.JobLogPO;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.util.List;

@Mapper
public interface JobLogMapper extends BaseMapper<JobLogPO> {

    @Select("SELECT * FROM t_job_log WHERE job_id = #{jobId} ORDER BY create_time DESC")
    List<JobLogPO> selectByJobId(@Param("jobId") Long jobId);

    @Select("SELECT * FROM t_job_log ORDER BY create_time DESC")
    Page<JobLogPO> pageQuery(Page<JobLogPO> page);
}
