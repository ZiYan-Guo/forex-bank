package com.forex.settlement.infrastructure.repository;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.forex.common.base.dto.PageResp;
import com.forex.settlement.domain.model.entity.BankGuarantee;
import com.forex.settlement.domain.model.query.GuaranteeQuery;
import com.forex.settlement.domain.repository.GuaranteeRepository;
import com.forex.settlement.infrastructure.mapper.GuaranteeMapper;
import com.forex.settlement.infrastructure.persistence.BankGuaranteePO;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
@RequiredArgsConstructor
public class GuaranteeRepositoryImpl implements GuaranteeRepository {

    private final GuaranteeMapper guaranteeMapper;

    @Override
    public BankGuarantee save(BankGuarantee guarantee) {
        BankGuaranteePO po = toPO(guarantee);
        if (guarantee.getId() == null) {
            guaranteeMapper.insert(po);
        } else {
            guaranteeMapper.updateById(po);
        }
        return toDomain(po);
    }

    @Override
    public Optional<BankGuarantee> findById(Long id) {
        BankGuaranteePO po = guaranteeMapper.selectById(id);
        return Optional.ofNullable(po).map(this::toDomain);
    }

    @Override
    public Optional<BankGuarantee> findByGuaranteeNo(String guaranteeNo) {
        BankGuaranteePO po = guaranteeMapper.selectByGuaranteeNo(guaranteeNo);
        return Optional.ofNullable(po).map(this::toDomain);
    }

    @Override
    public List<BankGuarantee> findByCustomerId(Long customerId) {
        List<BankGuaranteePO> poList = guaranteeMapper.selectList(
                new com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper<BankGuaranteePO>()
                        .eq(BankGuaranteePO::getCustomerId, customerId)
                        .orderByDesc(BankGuaranteePO::getCreateTime));
        return poList.stream().map(this::toDomain).toList();
    }

    @Override
    public PageResp<BankGuarantee> pageQuery(GuaranteeQuery query) {
        Page<BankGuaranteePO> page = new Page<>(query.getPageNum(), query.getPageSize());
        guaranteeMapper.pageQuery(page, query);
        List<BankGuarantee> records = page.getRecords().stream().map(this::toDomain).toList();
        return PageResp.of(page.getTotal(), records, (int) page.getCurrent(), (int) page.getSize());
    }

    private BankGuarantee toDomain(BankGuaranteePO po) {
        return new BankGuarantee(
                po.getId(),
                po.getGuaranteeNo(),
                po.getCustomerId(),
                po.getGuaranteeType(),
                po.getGuaranteeAmount(),
                po.getGuaranteeCurrency(),
                po.getBeneficiaryInfo(),
                po.getIssueDate(),
                po.getEffectiveDate(),
                po.getExpiryDate(),
                po.getClaimExpiryDate(),
                po.getCounterGuaranteeNo(),
                po.getGuaranteeFormat(),
                po.getGuaranteeStatus(),
                po.getFeeAmount(),
                po.getCommissionRate(),
                po.getOperatorId(),
                po.getSwiftRef(),
                po.getRemark()
        );
    }

    private BankGuaranteePO toPO(BankGuarantee guarantee) {
        BankGuaranteePO po = new BankGuaranteePO();
        po.setId(guarantee.getId());
        po.setGuaranteeNo(guarantee.getGuaranteeNo());
        po.setCustomerId(guarantee.getCustomerId());
        po.setGuaranteeType(guarantee.getGuaranteeType());
        po.setGuaranteeAmount(guarantee.getGuaranteeAmount());
        po.setGuaranteeCurrency(guarantee.getGuaranteeCurrency());
        po.setBeneficiaryInfo(guarantee.getBeneficiaryInfo());
        po.setIssueDate(guarantee.getIssueDate());
        po.setEffectiveDate(guarantee.getEffectiveDate());
        po.setExpiryDate(guarantee.getExpiryDate());
        po.setClaimExpiryDate(guarantee.getClaimExpiryDate());
        po.setCounterGuaranteeNo(guarantee.getCounterGuaranteeNo());
        po.setGuaranteeFormat(guarantee.getGuaranteeFormat());
        po.setGuaranteeStatus(guarantee.getGuaranteeStatus());
        po.setFeeAmount(guarantee.getFeeAmount());
        po.setCommissionRate(guarantee.getCommissionRate());
        po.setOperatorId(guarantee.getOperatorId());
        po.setSwiftRef(guarantee.getSwiftRef());
        po.setRemark(guarantee.getRemark());
        return po;
    }
}
