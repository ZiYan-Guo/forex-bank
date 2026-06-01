package com.forex.settlement.domain.repository;

import com.forex.common.base.dto.PageResp;
import com.forex.settlement.domain.model.entity.BankGuarantee;
import com.forex.settlement.domain.model.query.GuaranteeQuery;

import java.util.List;
import java.util.Optional;

public interface GuaranteeRepository {

    BankGuarantee save(BankGuarantee guarantee);

    Optional<BankGuarantee> findById(Long id);

    Optional<BankGuarantee> findByGuaranteeNo(String guaranteeNo);

    List<BankGuarantee> findByCustomerId(Long customerId);

    PageResp<BankGuarantee> pageQuery(GuaranteeQuery query);
}
