package com.forex.risk.infrastructure.mapper;

import com.forex.risk.infrastructure.persistence.RiskMonitorLogPO;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.util.List;

@Mapper
public interface RiskMonitorLogMapper extends BaseMapper<RiskMonitorLogPO> {

    @Select("SELECT * FROM t_risk_monitor_log WHERE log_no = #{logNo}")
    RiskMonitorLogPO selectByLogNo(@Param("logNo") String logNo);

    @Select("SELECT * FROM t_risk_monitor_log WHERE biz_no = #{bizNo}")
    List<RiskMonitorLogPO> selectByBizNo(@Param("bizNo") String bizNo);

    @Select("<script>" +
            "SELECT * FROM t_risk_monitor_log WHERE 1=1" +
            "<if test='query.customerId != null'>" +
            " AND customer_id = #{query.customerId}" +
            "</if>" +
            "<if test='query.bizType != null and query.bizType != \"\"'>" +
            " AND biz_type = #{query.bizType}" +
            "</if>" +
            "<if test='query.riskCategory != null and query.riskCategory != \"\"'>" +
            " AND risk_category = #{query.riskCategory}" +
            "</if>" +
            "<if test='query.riskLevel != null and query.riskLevel != \"\"'>" +
            " AND risk_level = #{query.riskLevel}" +
            "</if>" +
            "<if test='query.checkResult != null and query.checkResult != \"\"'>" +
            " AND check_result = #{query.checkResult}" +
            "</if>" +
            "<if test='query.startDate != null'>" +
            " AND create_time &gt;= #{query.startDate}" +
            "</if>" +
            "<if test='query.endDate != null'>" +
            " AND create_time &lt;= #{query.endDate}" +
            "</if>" +
            " ORDER BY create_time DESC" +
            "</script>")
    Page<RiskMonitorLogPO> pageQuery(Page<RiskMonitorLogPO> page, @Param("query") com.forex.risk.domain.model.query.RiskQuery query);
}
