package com.forex.account.infrastructure.repository;

import com.forex.account.domain.model.aggregate.ForexAccount;
import com.forex.account.domain.repository.AccountRepository;
import com.forex.account.infrastructure.mapper.ForexAccountMapper;
import com.forex.account.infrastructure.persistence.ForexAccountPO;
import com.forex.common.base.dto.PageReq;
import com.forex.common.base.dto.PageResp;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;

import lombok.RequiredArgsConstructor;

import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
@RequiredArgsConstructor
public class AccountRepositoryImpl implements AccountRepository {

    private final ForexAccountMapper forexAccountMapper;

    @Override
    public ForexAccount save(ForexAccount account) {
        ForexAccountPO po = toPO(account);
        if (account.getId() == null) {
            forexAccountMapper.insert(po);
        } else {
            forexAccountMapper.updateById(po);
        }
        return toDomain(po);
    }

    @Override
    public Optional<ForexAccount> findById(Long id) {
        ForexAccountPO po = forexAccountMapper.selectById(id);
        return Optional.ofNullable(po).map(this::toDomain);
    }

    @Override
    public Optional<ForexAccount> findByAccountNo(String accountNo) {
        ForexAccountPO po = forexAccountMapper.selectByAccountNo(accountNo);
        return Optional.ofNullable(po).map(this::toDomain);
    }

    @Override
    public List<ForexAccount> findByCustomerId(Long customerId) {
        List<ForexAccountPO> poList = forexAccountMapper.selectByCustomerId(customerId);
        return poList.stream().map(this::toDomain).toList();
    }

    @Override
    public PageResp<ForexAccount> pageQuery(PageReq pageReq) {
        LambdaQueryWrapper<ForexAccountPO> wrapper = new LambdaQueryWrapper<>();
        wrapper.orderByDesc(ForexAccountPO::getCreateTime);

        Page<ForexAccountPO> page = forexAccountMapper.selectPage(
                new Page<>(pageReq.getPageNum(), pageReq.getPageSize()), wrapper);

        List<ForexAccount> records = page.getRecords().stream().map(this::toDomain).toList();
        return PageResp.of(page.getTotal(), records, (int) page.getCurrent(), (int) page.getSize());
    }

    private ForexAccount toDomain(ForexAccountPO po) {
        return ForexAccount.reconstitute(
                po.getId(),
                po.getAccountNo(),
                po.getCustomerId(),
                po.getAccountType(),
                po.getCurrency(),
                po.getAccountName(),
                po.getBalance(),
                po.getFrozenAmount(),
                po.getAvailableBalance(),
                po.getOpenDate(),
                po.getOpenBranch(),
                po.getAccountStatus(),
                po.getInterestRate(),
                po.getIsInterestBearing(),
                po.getCentralBankReportStatus(),
                po.getLastReportTime()
        );
    }

    private ForexAccountPO toPO(ForexAccount account) {
        ForexAccountPO po = new ForexAccountPO();
        po.setId(account.getId());
        po.setAccountNo(account.getAccountNo());
        po.setCustomerId(account.getCustomerId());
        po.setAccountType(account.getAccountType());
        po.setCurrency(account.getCurrency());
        po.setAccountName(account.getAccountName());
        po.setBalance(account.getBalance());
        po.setFrozenAmount(account.getFrozenAmount());
        po.setAvailableBalance(account.getAvailableBalance());
        po.setOpenDate(account.getOpenDate());
        po.setOpenBranch(account.getOpenBranch());
        po.setAccountStatus(account.getAccountStatus());
        po.setInterestRate(account.getInterestRate());
        po.setIsInterestBearing(account.getIsInterestBearing());
        po.setCentralBankReportStatus(account.getCentralBankReportStatus());
        po.setLastReportTime(account.getLastReportTime());
        po.setVersion(account.getVersion());
        return po;
    }
}
