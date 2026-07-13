package com.forex.risk.infrastructure.mapper;

import com.forex.risk.infrastructure.persistence.SamplingTaskPO;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.util.List;

/**
 * Sampling task MyBatis mapper.
 * 抽查任务数据访问层。
 */
@Mapper
public interface SamplingTaskMapper extends BaseMapper<SamplingTaskPO> {

    @Select("SELECT * FROM t_sampling_task WHERE task_id = #{taskId} AND deleted = 0")
    SamplingTaskPO selectByTaskId(@Param("taskId") String taskId);

    @Select("SELECT * FROM t_sampling_task WHERE deleted = 0 ORDER BY create_time DESC, id DESC")
    List<SamplingTaskPO> selectAllTasks();
}
