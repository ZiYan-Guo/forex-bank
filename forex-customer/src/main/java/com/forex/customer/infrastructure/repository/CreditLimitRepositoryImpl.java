package com.forex.customer.infrastructure.repository;

import com.forex.customer.domain.model.entity.CreditLimit;
import com.forex.customer.domain.repository.CreditLimitRepository;
import com.forex.customer.infrastructure.mapper.CreditLimitMapper;
import com.forex.customer.infrastructure.persistence.CreditLimitPO;

import lombok.RequiredArgsConstructor;

import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
@RequiredArgsConstructor
public class CreditLimitRepositoryImpl implements CreditLimitRepository {

    private final CreditLimitMapper creditLimitMapper;

    @Override
    public CreditLimit save(CreditLimit limit) {
        CreditLimitPO po = toPO(limit);
        if (limit.getId() == null) {
            creditLimitMapper.insert(po);
        } else {
            creditLimitMapper.updateById(po);
        }
        return toDomain(po);
    }

    @Override
    public Optional<CreditLimit> findById(Long id) {
        CreditLimitPO po = creditLimitMapper.selectById(id);
        return Optional.ofNullable(po).map(this::toDomain);
    }

    @Override
    public List<CreditLimit> findByCustomerId(Long customerId) {
        return creditLimitMapper.selectByCustomerId(customerId).stream()
                .map(this::toDomain)
                .toList();
    }

    @Override
    public Optional<CreditLimit> findByCustomerAndType(Long customerId, String limitType, String currency) {
        CreditLimitPO po = creditLimitMapper.selectByCustomerAndType(customerId, limitType, currency);
        return Optional.ofNullable(po).map(this::toDomain);
    }

    private CreditLimit toDomain(CreditLimitPO po) {
        return new CreditLimit(
                po.getId(),
                po.getCustomerId(),
                po.getLimitType(),
                po.getCurrency(),
                po.getTotalLimit(),
                po.getUsedLimit(),
                po.getAvailableLimit(),
                po.getEffectiveDate(),
                po.getExpireDate(),
                po.getStatus()
        );
    }

    private CreditLimitPO toPO(CreditLimit limit) {
        CreditLimitPO po = new CreditLimitPO();
        po.setId(limit.getId());
        po.setCustomerId(limit.getCustomerId());
        po.setLimitType(limit.getLimitType());
        po.setCurrency(limit.getCurrency());
        po.setTotalLimit(limit.getTotalLimit());
        po.setUsedLimit(limit.getUsedLimit());
        po.setAvailableLimit(limit.getAvailableLimit());
        po.setEffectiveDate(limit.getEffectiveDate());
        po.setExpireDate(limit.getExpireDate());
        po.setStatus(limit.getStatus());
        return po;
    }
}
