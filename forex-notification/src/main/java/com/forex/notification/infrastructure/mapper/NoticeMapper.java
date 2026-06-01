package com.forex.notification.infrastructure.mapper;

import com.forex.common.mybatis.base.BaseMapperExt;
import com.forex.notification.application.query.NoticeQuery;
import com.forex.notification.infrastructure.persistence.NoticePO;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

@Mapper
public interface NoticeMapper extends BaseMapperExt<NoticePO> {

    @Select("<script>" +
            "SELECT * FROM t_notice WHERE deleted = 0" +
            "<if test='query.noticeType != null and query.noticeType != \"\"'>" +
            " AND notice_type = #{query.noticeType}" +
            "</if>" +
            "<if test='query.publishStatus != null and query.publishStatus != \"\"'>" +
            " AND publish_status = #{query.publishStatus}" +
            "</if>" +
            " ORDER BY create_time DESC" +
            "</script>")
    Page<NoticePO> pageQuery(Page<NoticePO> page, @Param("query") NoticeQuery query);
}
