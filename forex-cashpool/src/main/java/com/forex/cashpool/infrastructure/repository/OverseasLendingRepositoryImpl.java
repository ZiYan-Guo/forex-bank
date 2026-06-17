package com.forex.cashpool.infrastructure.repository;

import com.forex.cashpool.domain.model.aggregate.OverseasLending;
import com.forex.cashpool.domain.repository.OverseasLendingRepository;
import com.forex.cashpool.infrastructure.mapper.OverseasLendingMapper;
import com.forex.cashpool.infrastructure.persistence.OverseasLendingPO;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

/**
 * Overseas lending repository implementation.
 * 境外放款仓储实现。
 */
@Slf4j
@Repository
@RequiredArgsConstructor
public class OverseasLendingRepositoryImpl implements OverseasLendingRepository {

    private final OverseasLendingMapper overseasLendingMapper;

    @Override
    public OverseasLending save(OverseasLending lending) {
        OverseasLendingPO po = toPO(lending);
        if (lending.getId() == null) {
            overseasLendingMapper.insert(po);
            log.info("Overseas lending created: contractNo={}, amount={} {}", po.getContractNo(), po.getLoanAmount(), po.getLoanCurrency());
        } else {
            overseasLendingMapper.updateById(po);
            log.info("Overseas lending updated: id={}, contractNo={}, status={}", po.getId(), po.getContractNo(), po.getLoanStatus());
        }
        return toDomain(po);
    }

    @Override
    public Optional<OverseasLending> findById(Long id) {
        OverseasLendingPO po = overseasLendingMapper.selectById(id);
        return Optional.ofNullable(po).map(this::toDomain);
    }

    @Override
    public Optional<OverseasLending> findByContractNo(String contractNo) {
        OverseasLendingPO po = overseasLendingMapper.selectByContractNo(contractNo);
        return Optional.ofNullable(po).map(this::toDomain);
    }

    @Override
    public List<OverseasLending> findByCustomerId(Long customerId) {
        return overseasLendingMapper.selectByCustomerId(customerId).stream()
                .map(this::toDomain)
                .toList();
    }

    private OverseasLending toDomain(OverseasLendingPO po) {
        return OverseasLending.reconstitute(
                po.getId(), po.getContractNo(), po.getCustomerId(),
                po.getLoanAmount(), po.getLoanCurrency(), po.getInterestRate(),
                po.getStartDate(), po.getEndDate(), po.getRepaymentMethod(),
                po.getLoanStatus(), po.getOutstandingPrincipal(),
                po.getTotalInterest(), po.getPoolId());
    }

    private OverseasLendingPO toPO(OverseasLending lending) {
        OverseasLendingPO po = new OverseasLendingPO();
        po.setId(lending.getId());
        po.setContractNo(lending.getContractNo());
        po.setCustomerId(lending.getCustomerId());
        po.setLoanAmount(lending.getLoanAmount());
        po.setLoanCurrency(lending.getLoanCurrency());
        po.setInterestRate(lending.getInterestRate());
        po.setStartDate(lending.getStartDate());
        po.setEndDate(lending.getEndDate());
        po.setRepaymentMethod(lending.getRepaymentMethod());
        po.setLoanStatus(lending.getLoanStatus());
        po.setOutstandingPrincipal(lending.getOutstandingPrincipal());
        po.setTotalInterest(lending.getTotalInterest());
        po.setPoolId(lending.getPoolId());
        po.setVersion(lending.getVersion());
        return po;
    }
}
