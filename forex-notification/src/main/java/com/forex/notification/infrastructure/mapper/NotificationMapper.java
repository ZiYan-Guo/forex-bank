package com.forex.notification.infrastructure.mapper;

import com.forex.common.mybatis.base.BaseMapperExt;
import com.forex.notification.application.query.NotifyQuery;
import com.forex.notification.infrastructure.persistence.NotificationPO;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

@Mapper
public interface NotificationMapper extends BaseMapperExt<NotificationPO> {

    @Select("SELECT * FROM t_notification WHERE biz_no = #{bizNo} AND deleted = 0")
    NotificationPO selectByBizNo(@Param("bizNo") String bizNo);

    @Select("<script>" +
            "SELECT * FROM t_notification WHERE deleted = 0" +
            "<if test='query.notifyType != null and query.notifyType != \"\"'>" +
            " AND notify_type = #{query.notifyType}" +
            "</if>" +
            "<if test='query.status != null and query.status != \"\"'>" +
            " AND status = #{query.status}" +
            "</if>" +
            "<if test='query.bizType != null and query.bizType != \"\"'>" +
            " AND biz_type = #{query.bizType}" +
            "</if>" +
            " ORDER BY create_time DESC" +
            "</script>")
    Page<NotificationPO> pageQuery(Page<NotificationPO> page, @Param("query") NotifyQuery query);
}
