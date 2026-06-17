package com.forex.bookkeeping.infrastructure.mapper;

import com.forex.bookkeeping.infrastructure.persistence.MonthEndClosingPO;
import com.forex.common.mybatis.base.BaseMapperExt;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.util.List;

@Mapper
public interface MonthEndClosingMapper extends BaseMapperExt<MonthEndClosingPO> {

    @Select("SELECT * FROM t_month_end_closing WHERE closing_id = #{closingId} AND deleted = 0")
    MonthEndClosingPO selectByClosingId(@Param("closingId") String closingId);

    @Select("SELECT * FROM t_month_end_closing WHERE fiscal_period = #{fiscalPeriod} AND deleted = 0 ORDER BY create_time DESC")
    List<MonthEndClosingPO> selectByFiscalPeriod(@Param("fiscalPeriod") String fiscalPeriod);

    @Select("SELECT * FROM t_month_end_closing WHERE closing_status = #{status} AND deleted = 0")
    List<MonthEndClosingPO> selectByStatus(@Param("status") String status);
}
