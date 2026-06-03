package com.forex.clearing.infrastructure.mapper;

import com.forex.clearing.infrastructure.persistence.SettlementBatchPO;
import com.forex.common.mybatis.base.BaseMapperExt;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

@Mapper
public interface SettlementBatchMapper extends BaseMapperExt<SettlementBatchPO> {

    @Select("SELECT * FROM t_settlement_batch WHERE batch_no = #{batchNo} AND deleted = 0")
    SettlementBatchPO selectByBatchNo(@Param("batchNo") String batchNo);
}
