package com.forex.account.domain.repository;

import com.forex.account.domain.model.aggregate.ForexAccount;
import com.forex.common.base.dto.PageReq;
import com.forex.common.base.dto.PageResp;

import java.util.List;
import java.util.Optional;

public interface AccountRepository {

    ForexAccount save(ForexAccount account);

    Optional<ForexAccount> findById(Long id);

    Optional<ForexAccount> findByAccountNo(String accountNo);

    List<ForexAccount> findByCustomerId(Long customerId);

    PageResp<ForexAccount> pageQuery(PageReq pageReq);
}
