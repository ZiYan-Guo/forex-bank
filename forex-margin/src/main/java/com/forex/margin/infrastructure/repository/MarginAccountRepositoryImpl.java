package com.forex.margin.infrastructure.repository;

import com.forex.common.base.dto.PageResp;
import com.forex.margin.domain.model.aggregate.MarginAccount;
import com.forex.margin.domain.model.query.MarginQuery;
import com.forex.margin.domain.repository.MarginAccountRepository;
import com.forex.margin.infrastructure.mapper.MarginAccountMapper;
import com.forex.margin.infrastructure.persistence.MarginAccountPO;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;

import lombok.RequiredArgsConstructor;

import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
@RequiredArgsConstructor
public class MarginAccountRepositoryImpl implements MarginAccountRepository {

    private final MarginAccountMapper marginAccountMapper;

    @Override
    public MarginAccount save(MarginAccount marginAccount) {
        MarginAccountPO po = toPO(marginAccount);
        if (marginAccount.getId() == null) {
            marginAccountMapper.insert(po);
        } else {
            marginAccountMapper.updateById(po);
        }
        return toDomain(po);
    }

    @Override
    public Optional<MarginAccount> findById(Long id) {
        MarginAccountPO po = marginAccountMapper.selectById(id);
        return Optional.ofNullable(po).map(this::toDomain);
    }

    @Override
    public Optional<MarginAccount> findByMarginNo(String marginNo) {
        MarginAccountPO po = marginAccountMapper.selectByMarginNo(marginNo);
        return Optional.ofNullable(po).map(this::toDomain);
    }

    @Override
    public List<MarginAccount> findByCustomerId(Long customerId) {
        return marginAccountMapper.selectByCustomerId(customerId).stream()
                .map(this::toDomain)
                .toList();
    }

    @Override
    public List<MarginAccount> listForLedgerSummary() {
        return marginAccountMapper.selectForLedgerSummary().stream()
                .map(this::toDomain)
                .toList();
    }

    @Override
    public PageResp<MarginAccount> pageQuery(MarginQuery query) {
        Page<MarginAccountPO> page = new Page<>(query.getPageNum(), query.getPageSize());
        Page<MarginAccountPO> result = marginAccountMapper.pageQuery(page, query);
        List<MarginAccount> records = result.getRecords().stream()
                .map(this::toDomain)
                .toList();
        return PageResp.of(result.getTotal(), records, query.getPageNum(), query.getPageSize());
    }

    private MarginAccount toDomain(MarginAccountPO po) {
        return MarginAccount.reconstitute(
                po.getId(),
                po.getMarginNo(),
                po.getCustomerId(),
                po.getTradeId(),
                po.getMarginType(),
                po.getMarginCurrency(),
                po.getRequiredAmount(),
                po.getDepositedAmount(),
                po.getShortfallAmount(),
                po.getMarginRate(),
                po.getCallDate(),
                po.getDueDate(),
                po.getStatus(),
                po.getCollateralType(),
                po.getReleaseReason(),
                po.getCollateralValue(),
                po.getWaterLevel()
        );
    }

    private MarginAccountPO toPO(MarginAccount marginAccount) {
        MarginAccountPO po = new MarginAccountPO();
        po.setId(marginAccount.getId());
        po.setMarginNo(marginAccount.getMarginNo());
        po.setCustomerId(marginAccount.getCustomerId());
        po.setTradeId(marginAccount.getTradeId());
        po.setMarginType(marginAccount.getMarginType());
        po.setMarginCurrency(marginAccount.getMarginCurrency());
        po.setRequiredAmount(marginAccount.getRequiredAmount());
        po.setDepositedAmount(marginAccount.getDepositedAmount());
        po.setShortfallAmount(marginAccount.getShortfallAmount());
        po.setMarginRate(marginAccount.getMarginRate());
        po.setCallDate(marginAccount.getCallDate());
        po.setDueDate(marginAccount.getDueDate());
        po.setStatus(marginAccount.getStatus());
        po.setCollateralType(marginAccount.getCollateralType());
        po.setCollateralValue(marginAccount.getCollateralValue());
        po.setWaterLevel(marginAccount.getWaterLevel());
        po.setReleaseReason(marginAccount.getReleaseReason());
        return po;
    }
}
