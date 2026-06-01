package com.forex.settlement.infrastructure.mapper;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.forex.common.mybatis.base.BaseMapperExt;
import com.forex.settlement.infrastructure.persistence.BankGuaranteePO;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.util.List;

@Mapper
public interface GuaranteeMapper extends BaseMapperExt<BankGuaranteePO> {

    @Select("SELECT * FROM t_bank_guarantee WHERE guarantee_no = #{guaranteeNo} AND deleted = 0")
    BankGuaranteePO selectByGuaranteeNo(@Param("guaranteeNo") String guaranteeNo);

    @Select("<script>" +
            "SELECT * FROM t_bank_guarantee WHERE deleted = 0" +
            "<if test='query.guaranteeNo != null and query.guaranteeNo != \"\"'> AND guarantee_no = #{query.guaranteeNo}</if>" +
            "<if test='query.customerId != null'> AND customer_id = #{query.customerId}</if>" +
            "<if test='query.guaranteeStatus != null and query.guaranteeStatus != \"\"'> AND guarantee_status = #{query.guaranteeStatus}</if>" +
            " ORDER BY create_time DESC" +
            "</script>")
    List<BankGuaranteePO> pageQuery(Page<BankGuaranteePO> page,
                                     @Param("query") com.forex.settlement.domain.model.query.GuaranteeQuery query);
}
