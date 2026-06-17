package com.forex.workflow.infrastructure.mapper;

import com.forex.workflow.infrastructure.persistence.ApprovalRecordPO;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.util.List;

/**
 * Approval record MyBatis mapper.
 * 审批记录数据访问层。
 */
@Mapper
public interface ApprovalRecordMapper extends BaseMapper<ApprovalRecordPO> {

    @Select("SELECT * FROM t_approval_record WHERE task_id = #{taskId} ORDER BY approve_time ASC")
    List<ApprovalRecordPO> selectByTaskId(@Param("taskId") Long taskId);
}
