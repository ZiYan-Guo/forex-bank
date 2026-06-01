package com.forex.customer.domain.repository;

import com.forex.customer.domain.model.entity.CreditLimit;

import java.util.List;
import java.util.Optional;

public interface CreditLimitRepository {

    CreditLimit save(CreditLimit limit);

    Optional<CreditLimit> findById(Long id);

    List<CreditLimit> findByCustomerId(Long customerId);

    Optional<CreditLimit> findByCustomerAndType(Long customerId, String limitType, String currency);
}
