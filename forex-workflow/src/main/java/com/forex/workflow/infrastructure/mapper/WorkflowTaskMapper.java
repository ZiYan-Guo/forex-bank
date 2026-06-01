package com.forex.workflow.infrastructure.mapper;

import com.forex.common.mybatis.base.BaseMapperExt;
import com.forex.workflow.application.query.WorkflowQuery;
import com.forex.workflow.infrastructure.persistence.WorkflowTaskPO;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

@Mapper
public interface WorkflowTaskMapper extends BaseMapperExt<WorkflowTaskPO> {

    @Select("SELECT * FROM t_workflow_task WHERE task_id = #{taskId} AND deleted = 0")
    WorkflowTaskPO selectByTaskId(@Param("taskId") String taskId);

    @Select("SELECT * FROM t_workflow_task WHERE biz_no = #{bizNo} AND deleted = 0")
    WorkflowTaskPO selectByBizNo(@Param("bizNo") String bizNo);

    @Select("<script>" +
            "SELECT * FROM t_workflow_task WHERE deleted = 0" +
            "<if test='query.bizType != null and query.bizType != \"\"'>" +
            " AND biz_type = #{query.bizType}" +
            "</if>" +
            "<if test='query.status != null and query.status != \"\"'>" +
            " AND status = #{query.status}" +
            "</if>" +
            "<if test='query.assignee != null and query.assignee != \"\"'>" +
            " AND assignee = #{query.assignee}" +
            "</if>" +
            " ORDER BY create_time DESC" +
            "</script>")
    Page<WorkflowTaskPO> pageQuery(Page<WorkflowTaskPO> page, @Param("query") WorkflowQuery query);
}
