package com.forex.account.infrastructure.repository;

import com.forex.account.domain.model.entity.AccountTransaction;
import com.forex.account.domain.repository.TransactionRepository;
import com.forex.account.infrastructure.mapper.AccountTransactionMapper;
import com.forex.account.infrastructure.persistence.AccountTransactionPO;
import com.forex.common.base.dto.PageReq;

import lombok.RequiredArgsConstructor;

import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
@RequiredArgsConstructor
public class TransactionRepositoryImpl implements TransactionRepository {

    private final AccountTransactionMapper accountTransactionMapper;

    @Override
    public AccountTransaction save(AccountTransaction tx) {
        AccountTransactionPO po = toPO(tx);
        if (tx.getId() == null) {
            accountTransactionMapper.insert(po);
        } else {
            accountTransactionMapper.updateById(po);
        }
        return toDomain(po);
    }

    @Override
    public List<AccountTransaction> findByAccountId(Long accountId, PageReq pageReq) {
        List<AccountTransactionPO> poList = accountTransactionMapper.selectByAccountId(
                accountId, pageReq.getOffset(), pageReq.getPageSize());
        return poList.stream().map(this::toDomain).toList();
    }

    @Override
    public Optional<AccountTransaction> findByTransactionNo(String txNo) {
        AccountTransactionPO po = accountTransactionMapper.selectByTransactionNo(txNo);
        return Optional.ofNullable(po).map(this::toDomain);
    }

    @Override
    public long countByAccountId(Long accountId) {
        List<AccountTransactionPO> all = accountTransactionMapper.selectByAccountId(accountId, 0, Integer.MAX_VALUE);
        return all.size();
    }

    private AccountTransaction toDomain(AccountTransactionPO po) {
        return new AccountTransaction(
                po.getId(),
                po.getTransactionNo(),
                po.getAccountId(),
                po.getAccountNo(),
                po.getTransactionType(),
                po.getAmount(),
                po.getCurrency(),
                po.getBalanceBefore(),
                po.getBalanceAfter(),
                po.getRelatedBizNo(),
                po.getRelatedBizType(),
                po.getTransactionTime(),
                po.getSummary()
        );
    }

    private AccountTransactionPO toPO(AccountTransaction tx) {
        AccountTransactionPO po = new AccountTransactionPO();
        po.setId(tx.getId());
        po.setTransactionNo(tx.getTransactionNo());
        po.setAccountId(tx.getAccountId());
        po.setAccountNo(tx.getAccountNo());
        po.setTransactionType(tx.getTransactionType());
        po.setAmount(tx.getAmount());
        po.setCurrency(tx.getCurrency());
        po.setBalanceBefore(tx.getBalanceBefore());
        po.setBalanceAfter(tx.getBalanceAfter());
        po.setRelatedBizNo(tx.getRelatedBizNo());
        po.setRelatedBizType(tx.getRelatedBizType());
        po.setTransactionTime(tx.getTransactionTime());
        po.setSummary(tx.getSummary());
        return po;
    }
}
