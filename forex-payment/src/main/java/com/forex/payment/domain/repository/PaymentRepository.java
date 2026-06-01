package com.forex.payment.domain.repository;

import com.forex.common.base.dto.PageResp;
import com.forex.payment.domain.model.aggregate.CrossBorderPayment;
import com.forex.payment.domain.model.dto.PaymentQuery;

import java.util.List;
import java.util.Optional;

public interface PaymentRepository {

    CrossBorderPayment save(CrossBorderPayment payment);

    Optional<CrossBorderPayment> findById(Long id);

    Optional<CrossBorderPayment> findByPaymentNo(String paymentNo);

    List<CrossBorderPayment> findByCustomerId(Long customerId);

    PageResp<CrossBorderPayment> pageQuery(PaymentQuery query);
}
