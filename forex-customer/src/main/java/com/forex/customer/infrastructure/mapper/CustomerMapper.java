package com.forex.customer.infrastructure.mapper;

import com.forex.common.mybatis.base.BaseMapperExt;
import com.forex.customer.domain.model.dto.CustomerQuery;
import com.forex.customer.infrastructure.persistence.CustomerPO;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.util.List;

@Mapper
public interface CustomerMapper extends BaseMapperExt<CustomerPO> {

    @Select("SELECT * FROM t_customer WHERE customer_no = #{customerNo} AND deleted = 0")
    CustomerPO selectByCustomerNo(@Param("customerNo") String customerNo);

    @Select("SELECT * FROM t_customer WHERE cert_type = #{certType} AND cert_no = #{certNo} AND deleted = 0")
    List<CustomerPO> selectByCertNo(@Param("certType") String certType, @Param("certNo") String certNo);

    @Select("<script>" +
            "SELECT * FROM t_customer WHERE deleted = 0" +
            "<if test='query.customerNo != null and query.customerNo != \"\"'>" +
            " AND customer_no = #{query.customerNo}" +
            "</if>" +
            "<if test='query.customerName != null and query.customerName != \"\"'>" +
            " AND customer_name LIKE CONCAT('%', #{query.customerName}, '%')" +
            "</if>" +
            "<if test='query.customerType != null'>" +
            " AND customer_type = #{query.customerType}" +
            "</if>" +
            "<if test='query.certType != null and query.certType != \"\"'>" +
            " AND cert_type = #{query.certType}" +
            "</if>" +
            "<if test='query.certNo != null and query.certNo != \"\"'>" +
            " AND cert_no = #{query.certNo}" +
            "</if>" +
            "<if test='query.countryCode != null and query.countryCode != \"\"'>" +
            " AND country_code = #{query.countryCode}" +
            "</if>" +
            "<if test='query.riskLevel != null'>" +
            " AND risk_level = #{query.riskLevel}" +
            "</if>" +
            "<if test='query.status != null'>" +
            " AND status = #{query.status}" +
            "</if>" +
            "<if test='query.dueDiligenceStatus != null'>" +
            " AND due_diligence_status = #{query.dueDiligenceStatus}" +
            "</if>" +
            "<if test='query.keyword != null and query.keyword != \"\"'>" +
            " AND (customer_name LIKE CONCAT('%', #{query.keyword}, '%') OR customer_no LIKE CONCAT('%', #{query.keyword}, '%') OR contact_person LIKE CONCAT('%', #{query.keyword}, '%'))" +
            "</if>" +
            " ORDER BY create_time DESC" +
            "</script>")
    List<CustomerPO> selectByCondition(@Param("query") CustomerQuery query);

    @Select("<script>" +
            "SELECT * FROM t_customer WHERE deleted = 0" +
            "<if test='query.customerNo != null and query.customerNo != \"\"'>" +
            " AND customer_no = #{query.customerNo}" +
            "</if>" +
            "<if test='query.customerName != null and query.customerName != \"\"'>" +
            " AND customer_name LIKE CONCAT('%', #{query.customerName}, '%')" +
            "</if>" +
            "<if test='query.customerType != null'>" +
            " AND customer_type = #{query.customerType}" +
            "</if>" +
            "<if test='query.riskLevel != null'>" +
            " AND risk_level = #{query.riskLevel}" +
            "</if>" +
            "<if test='query.status != null'>" +
            " AND status = #{query.status}" +
            "</if>" +
            "<if test='query.certNo != null and query.certNo != \"\"'>" +
            " AND cert_no = #{query.certNo}" +
            "</if>" +
            " ORDER BY create_time DESC" +
            "</script>")
    Page<CustomerPO> pageQuery(Page<CustomerPO> page, @Param("query") com.forex.customer.application.query.CustomerQuery query);
}
