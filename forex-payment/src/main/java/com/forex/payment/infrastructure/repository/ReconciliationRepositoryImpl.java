package com.forex.payment.infrastructure.repository;

import com.forex.payment.domain.model.entity.PaymentReconciliation;
import com.forex.payment.domain.repository.ReconciliationRepository;
import com.forex.payment.infrastructure.mapper.PaymentReconciliationMapper;
import com.forex.payment.infrastructure.persistence.PaymentReconciliationPO;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;

/**
 * Payment reconciliation repository implementation.
 * 支付对账仓储实现。
 */
@Slf4j
@Repository
@RequiredArgsConstructor
public class ReconciliationRepositoryImpl implements ReconciliationRepository {

    private final PaymentReconciliationMapper paymentReconciliationMapper;

    @Override
    public PaymentReconciliation save(PaymentReconciliation rec) {
        PaymentReconciliationPO po = toPO(rec);
        if (rec.getId() == null) {
            paymentReconciliationMapper.insert(po);
            log.info("Reconciliation record created: transactionRef={}, status={}", po.getTransactionRef(), po.getReconciliationStatus());
        } else {
            paymentReconciliationMapper.updateById(po);
            log.info("Reconciliation record updated: id={}, status={}", po.getId(), po.getReconciliationStatus());
        }
        return toDomain(po);
    }

    @Override
    public List<PaymentReconciliation> findByStatementDate(LocalDate date) {
        List<PaymentReconciliationPO> poList = paymentReconciliationMapper.selectByStatementDate(date);
        return poList.stream().map(this::toDomain).toList();
    }

    private PaymentReconciliation toDomain(PaymentReconciliationPO po) {
        return new PaymentReconciliation(
                po.getId(),
                po.getPaymentId(),
                po.getNostroAccount(),
                po.getCurrency(),
                po.getTransactionRef(),
                po.getStatementDate(),
                po.getNostroAmount(),
                po.getNostroDirection(),
                po.getSystemAmount(),
                po.getSystemDirection(),
                po.getReconciliationStatus(),
                po.getMatchTime(),
                po.getDifference()
        );
    }

    private PaymentReconciliationPO toPO(PaymentReconciliation rec) {
        PaymentReconciliationPO po = new PaymentReconciliationPO();
        po.setId(rec.getId());
        po.setPaymentId(rec.getPaymentId());
        po.setNostroAccount(rec.getNostroAccount());
        po.setCurrency(rec.getCurrency());
        po.setTransactionRef(rec.getTransactionRef());
        po.setStatementDate(rec.getStatementDate());
        po.setNostroAmount(rec.getNostroAmount());
        po.setNostroDirection(rec.getNostroDirection());
        po.setSystemAmount(rec.getSystemAmount());
        po.setSystemDirection(rec.getSystemDirection());
        po.setReconciliationStatus(rec.getReconciliationStatus());
        po.setMatchTime(rec.getMatchTime());
        po.setDifference(rec.getDifference());
        return po;
    }
}
