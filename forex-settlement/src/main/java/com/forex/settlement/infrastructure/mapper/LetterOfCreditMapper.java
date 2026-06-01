package com.forex.settlement.infrastructure.mapper;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.forex.common.mybatis.base.BaseMapperExt;
import com.forex.settlement.infrastructure.persistence.LetterOfCreditPO;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.util.List;

@Mapper
public interface LetterOfCreditMapper extends BaseMapperExt<LetterOfCreditPO> {

    @Select("SELECT * FROM t_letter_of_credit WHERE lc_no = #{lcNo} AND deleted = 0")
    LetterOfCreditPO selectByLcNo(@Param("lcNo") String lcNo);

    @Select("<script>" +
            "SELECT * FROM t_letter_of_credit WHERE deleted = 0" +
            "<if test='query.lcNo != null and query.lcNo != \"\"'> AND lc_no = #{query.lcNo}</if>" +
            "<if test='query.customerId != null'> AND customer_id = #{query.customerId}</if>" +
            "<if test='query.lcType != null and query.lcType != \"\"'> AND lc_type = #{query.lcType}</if>" +
            "<if test='query.lcStatus != null and query.lcStatus != \"\"'> AND lc_status = #{query.lcStatus}</if>" +
            "<if test='query.startDate != null'> AND issue_date &gt;= #{query.startDate}</if>" +
            "<if test='query.endDate != null'> AND issue_date &lt;= #{query.endDate}</if>" +
            " ORDER BY create_time DESC" +
            "</script>")
    List<LetterOfCreditPO> pageQuery(Page<LetterOfCreditPO> page,
                                      @Param("query") com.forex.settlement.domain.model.query.LcQuery query);
}
