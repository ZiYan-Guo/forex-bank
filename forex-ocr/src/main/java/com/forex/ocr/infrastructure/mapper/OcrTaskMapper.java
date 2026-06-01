package com.forex.ocr.infrastructure.mapper;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.forex.common.mybatis.base.BaseMapperExt;
import com.forex.ocr.application.query.OcrQuery;
import com.forex.ocr.infrastructure.persistence.OcrTaskPO;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.util.List;

@Mapper
public interface OcrTaskMapper extends BaseMapperExt<OcrTaskPO> {

    @Select("SELECT * FROM t_ocr_task WHERE task_id = #{taskId} AND deleted = 0")
    OcrTaskPO selectByTaskId(@Param("taskId") String taskId);

    @Select("<script>" +
            "SELECT * FROM t_ocr_task WHERE deleted = 0" +
            "<if test='query.docType != null and query.docType != \"\"'>" +
            " AND doc_type = #{query.docType}" +
            "</if>" +
            "<if test='query.status != null and query.status != \"\"'>" +
            " AND status = #{query.status}" +
            "</if>" +
            " ORDER BY create_time DESC" +
            "</script>")
    List<OcrTaskPO> selectByCondition(@Param("query") OcrQuery query);

    @Select("<script>" +
            "SELECT * FROM t_ocr_task WHERE deleted = 0" +
            "<if test='query.docType != null and query.docType != \"\"'>" +
            " AND doc_type = #{query.docType}" +
            "</if>" +
            "<if test='query.status != null and query.status != \"\"'>" +
            " AND status = #{query.status}" +
            "</if>" +
            " ORDER BY create_time DESC" +
            "</script>")
    Page<OcrTaskPO> pageQuery(Page<OcrTaskPO> page, @Param("query") OcrQuery query);
}
