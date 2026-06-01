package com.forex.payment.infrastructure.mapper;

import com.forex.payment.infrastructure.persistence.BlacklistHitPO;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.util.List;

@Mapper
public interface BlacklistHitMapper extends BaseMapper<BlacklistHitPO> {

    @Select("SELECT * FROM t_payment_blacklist_hit WHERE payment_id = #{paymentId}")
    List<BlacklistHitPO> selectByPaymentId(@Param("paymentId") Long paymentId);
}
