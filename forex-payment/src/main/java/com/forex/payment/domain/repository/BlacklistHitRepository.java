package com.forex.payment.domain.repository;

import com.forex.payment.domain.model.entity.BlacklistHit;

import java.util.List;

public interface BlacklistHitRepository {

    BlacklistHit save(BlacklistHit hit);

    List<BlacklistHit> findByPaymentId(Long paymentId);
}
