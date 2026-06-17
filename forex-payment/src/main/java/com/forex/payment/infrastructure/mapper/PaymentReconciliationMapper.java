package com.forex.payment.infrastructure.mapper;

import com.forex.payment.infrastructure.persistence.PaymentReconciliationPO;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.time.LocalDate;
import java.util.List;

/**
 * Payment reconciliation MyBatis mapper.
 * 支付对账数据访问层。
 */
@Mapper
public interface PaymentReconciliationMapper extends BaseMapper<PaymentReconciliationPO> {

    @Select("SELECT * FROM t_payment_reconciliation WHERE statement_date = #{statementDate}")
    List<PaymentReconciliationPO> selectByStatementDate(@Param("statementDate") LocalDate statementDate);
}
