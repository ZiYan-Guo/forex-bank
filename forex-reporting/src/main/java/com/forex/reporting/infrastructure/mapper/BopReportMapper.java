package com.forex.reporting.infrastructure.mapper;

import com.forex.common.mybatis.base.BaseMapperExt;
import com.forex.reporting.infrastructure.persistence.BopReportPO;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.util.List;

@Mapper
public interface BopReportMapper extends BaseMapperExt<BopReportPO> {

    @Select("SELECT * FROM t_balance_of_payment WHERE report_no = #{reportNo} AND deleted = 0")
    BopReportPO selectByReportNo(@Param("reportNo") String reportNo);

    @Select("SELECT * FROM t_balance_of_payment WHERE transaction_no = #{bizNo} AND deleted = 0")
    List<BopReportPO> selectByBizNo(@Param("bizNo") String bizNo);

    @Select("<script>" +
            "SELECT * FROM t_balance_of_payment WHERE deleted = 0" +
            "<if test='query.customerId != null'>" +
            " AND customer_id = #{query.customerId}" +
            "</if>" +
            "<if test='query.reportType != null and query.reportType != \"\"'>" +
            " AND report_type = #{query.reportType}" +
            "</if>" +
            "<if test='query.reportStatus != null and query.reportStatus != \"\"'>" +
            " AND report_status = #{query.reportStatus}" +
            "</if>" +
            "<if test='query.transactionDate != null'>" +
            " AND transaction_date = #{query.transactionDate}" +
            "</if>" +
            "<if test='query.transactionNo != null and query.transactionNo != \"\"'>" +
            " AND transaction_no = #{query.transactionNo}" +
            "</if>" +
            " ORDER BY create_time DESC" +
            "</script>")
    Page<BopReportPO> pageQuery(Page<BopReportPO> page, @Param("query") com.forex.reporting.domain.model.query.ReportQuery query);
}
