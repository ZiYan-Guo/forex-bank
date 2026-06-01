package com.forex.account.domain.repository;

import com.forex.account.domain.model.entity.AccountTransaction;
import com.forex.common.base.dto.PageReq;

import java.util.List;
import java.util.Optional;

public interface TransactionRepository {

    AccountTransaction save(AccountTransaction tx);

    List<AccountTransaction> findByAccountId(Long accountId, PageReq pageReq);

    Optional<AccountTransaction> findByTransactionNo(String txNo);

    long countByAccountId(Long accountId);
}
