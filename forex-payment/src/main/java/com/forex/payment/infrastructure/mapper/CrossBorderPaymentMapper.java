package com.forex.payment.infrastructure.mapper;

import com.forex.common.mybatis.base.BaseMapperExt;
import com.forex.payment.infrastructure.persistence.CrossBorderPaymentPO;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.util.List;

@Mapper
public interface CrossBorderPaymentMapper extends BaseMapperExt<CrossBorderPaymentPO> {

    @Select("SELECT * FROM t_cross_border_payment WHERE payment_no = #{paymentNo} AND deleted = 0")
    CrossBorderPaymentPO selectByPaymentNo(@Param("paymentNo") String paymentNo);

    @Select("SELECT * FROM t_cross_border_payment WHERE customer_id = #{customerId} AND deleted = 0 ORDER BY create_time DESC")
    List<CrossBorderPaymentPO> selectByCustomerId(@Param("customerId") Long customerId);

    @Select("<script>" +
            "SELECT * FROM t_cross_border_payment WHERE deleted = 0" +
            "<if test='paymentNo != null and paymentNo != \"\"'> AND payment_no = #{paymentNo}</if>" +
            "<if test='customerId != null'> AND customer_id = #{customerId}</if>" +
            "<if test='paymentDirection != null and paymentDirection != \"\"'> AND payment_direction = #{paymentDirection}</if>" +
            "<if test='paymentType != null and paymentType != \"\"'> AND payment_type = #{paymentType}</if>" +
            "<if test='paymentStatus != null and paymentStatus != \"\"'> AND payment_status = #{paymentStatus}</if>" +
            "<if test='startDate != null'> AND create_time &gt;= #{startDate}</if>" +
            "<if test='endDate != null'> AND create_time &lt;= #{endDate}</if>" +
            " ORDER BY create_time DESC" +
            "</script>")
    List<CrossBorderPaymentPO> selectByCondition(@Param("paymentNo") String paymentNo,
                                                  @Param("customerId") Long customerId,
                                                  @Param("paymentDirection") String paymentDirection,
                                                  @Param("paymentType") String paymentType,
                                                  @Param("paymentStatus") String paymentStatus,
                                                  @Param("startDate") java.time.LocalDate startDate,
                                                  @Param("endDate") java.time.LocalDate endDate);
}
