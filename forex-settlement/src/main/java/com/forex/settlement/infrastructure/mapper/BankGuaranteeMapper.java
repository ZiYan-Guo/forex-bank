package com.forex.settlement.infrastructure.mapper;

import com.forex.settlement.infrastructure.persistence.BankGuaranteePO;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

/**
 * Bank guarantee MyBatis mapper.
 * 银行保函数据访问层。
 */
@Mapper
public interface BankGuaranteeMapper extends BaseMapper<BankGuaranteePO> {

    @Select("SELECT * FROM t_bank_guarantee WHERE guarantee_no = #{guaranteeNo}")
    BankGuaranteePO selectByGuaranteeNo(@Param("guaranteeNo") String guaranteeNo);
}
