package com.forex.schedule.infrastructure.mapper;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.forex.common.mybatis.base.BaseMapperExt;
import com.forex.schedule.infrastructure.persistence.ScheduleJobPO;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.util.List;

@Mapper
public interface ScheduleJobMapper extends BaseMapperExt<ScheduleJobPO> {

    @Select("SELECT * FROM t_schedule_job WHERE job_handler = #{jobHandler} AND deleted = 0")
    ScheduleJobPO selectByJobHandler(@Param("jobHandler") String jobHandler);

    @Select("SELECT * FROM t_schedule_job WHERE status = 'ENABLED' AND deleted = 0")
    List<ScheduleJobPO> selectAllEnabled();

    @Select("<script>" +
            "SELECT * FROM t_schedule_job WHERE deleted = 0" +
            "<if test='query.jobName != null and query.jobName != \"\"'>" +
            " AND job_name LIKE CONCAT('%', #{query.jobName}, '%')" +
            "</if>" +
            "<if test='query.jobGroup != null and query.jobGroup != \"\"'>" +
            " AND job_group = #{query.jobGroup}" +
            "</if>" +
            "<if test='query.status != null and query.status != \"\"'>" +
            " AND status = #{query.status}" +
            "</if>" +
            " ORDER BY create_time DESC" +
            "</script>")
    Page<ScheduleJobPO> pageQuery(Page<ScheduleJobPO> page,
                                   @Param("query") com.forex.schedule.application.query.JobQuery query);
}
