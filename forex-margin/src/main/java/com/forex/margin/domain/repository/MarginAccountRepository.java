package com.forex.margin.domain.repository;

import com.forex.common.base.dto.PageResp;
import com.forex.margin.domain.model.aggregate.MarginAccount;
import com.forex.margin.domain.model.query.MarginQuery;

import java.util.List;
import java.util.Optional;

public interface MarginAccountRepository {

    MarginAccount save(MarginAccount marginAccount);

    Optional<MarginAccount> findById(Long id);

    Optional<MarginAccount> findByMarginNo(String marginNo);

    List<MarginAccount> findByCustomerId(Long customerId);

    PageResp<MarginAccount> pageQuery(MarginQuery query);
}
