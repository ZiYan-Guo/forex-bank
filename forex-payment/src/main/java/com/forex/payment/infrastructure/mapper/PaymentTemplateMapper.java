package com.forex.payment.infrastructure.mapper;

import com.forex.payment.infrastructure.persistence.PaymentTemplatePO;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;

import org.apache.ibatis.annotations.Mapper;

/**
 * Payment template MyBatis mapper.
 * 支付模板数据访问层。
 */
@Mapper
public interface PaymentTemplateMapper extends BaseMapper<PaymentTemplatePO> {
}
