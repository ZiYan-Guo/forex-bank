package com.forex.payment.domain.repository;

import com.forex.payment.domain.model.entity.PaymentReconciliation;

import java.time.LocalDate;
import java.util.List;

public interface ReconciliationRepository {

    PaymentReconciliation save(PaymentReconciliation rec);

    List<PaymentReconciliation> findByStatementDate(LocalDate date);
}
