package com.forex.clearing.infrastructure.mapper;

import com.forex.clearing.domain.model.query.ClearingQuery;
import com.forex.clearing.infrastructure.persistence.ClearingInstructionPO;
import com.forex.common.mybatis.base.BaseMapperExt;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.util.List;

@Mapper
public interface ClearingInstructionMapper extends BaseMapperExt<ClearingInstructionPO> {

    @Select("SELECT * FROM t_clearing_instruction WHERE instruction_no = #{instructionNo} AND deleted = 0")
    ClearingInstructionPO selectByInstructionNo(@Param("instructionNo") String instructionNo);

    @Select("SELECT * FROM t_clearing_instruction WHERE biz_no = #{bizNo} AND deleted = 0")
    ClearingInstructionPO selectByBizNo(@Param("bizNo") String bizNo);

    @Select("<script>" +
            "SELECT * FROM t_clearing_instruction WHERE deleted = 0" +
            "<if test='query.instructionNo != null and query.instructionNo != \"\"'> AND instruction_no = #{query.instructionNo}</if>" +
            "<if test='query.clearingChannel != null and query.clearingChannel != \"\"'> AND clearing_channel = #{query.clearingChannel}</if>" +
            "<if test='query.instructionStatus != null and query.instructionStatus != \"\"'> AND instruction_status = #{query.instructionStatus}</if>" +
            "<if test='query.valueDate != null'> AND value_date = #{query.valueDate}</if>" +
            "<if test='query.bizType != null and query.bizType != \"\"'> AND biz_type = #{query.bizType}</if>" +
            "<if test='query.bizNo != null and query.bizNo != \"\"'> AND biz_no = #{query.bizNo}</if>" +
            "<if test='query.settlementType != null and query.settlementType != \"\"'> AND settlement_type = #{query.settlementType}</if>" +
            "<if test='query.startDate != null'> AND create_time &gt;= #{query.startDate}</if>" +
            "<if test='query.endDate != null'> AND create_time &lt;= #{query.endDate}</if>" +
            " ORDER BY create_time DESC" +
            "</script>")
    List<ClearingInstructionPO> pageQuery(Page<?> page, @Param("query") ClearingQuery query);
}
