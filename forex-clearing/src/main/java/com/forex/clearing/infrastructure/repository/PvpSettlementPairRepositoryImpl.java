package com.forex.clearing.infrastructure.repository;

import com.forex.clearing.domain.model.aggregate.PvpSettlementPair;
import com.forex.clearing.domain.repository.PvpSettlementRepository;
import com.forex.clearing.infrastructure.mapper.PvpSettlementPairMapper;
import com.forex.clearing.infrastructure.persistence.PvpSettlementPairPO;

import lombok.RequiredArgsConstructor;

import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
@RequiredArgsConstructor
public class PvpSettlementPairRepositoryImpl implements PvpSettlementRepository {

    private final PvpSettlementPairMapper mapper;

    @Override
    public PvpSettlementPair save(PvpSettlementPair pair) {
        PvpSettlementPairPO po = toPO(pair);
        if (pair.getId() == null) {
            mapper.insert(po);
        } else {
            mapper.updateById(po);
        }
        return toDomain(po);
    }

    @Override
    public Optional<PvpSettlementPair> findById(Long id) {
        PvpSettlementPairPO po = mapper.selectById(id);
        return Optional.ofNullable(po).map(this::toDomain);
    }

    @Override
    public Optional<PvpSettlementPair> findByPairId(String pairId) {
        PvpSettlementPairPO po = mapper.selectByPairId(pairId);
        return Optional.ofNullable(po).map(this::toDomain);
    }

    private PvpSettlementPair toDomain(PvpSettlementPairPO po) {
        return PvpSettlementPair.reconstitute(
                po.getId(),
                po.getPairId(),
                po.getPayInstructionId(),
                po.getPayInstructionNo(),
                po.getReceiveInstructionId(),
                po.getReceiveInstructionNo(),
                po.getPayCurrency(),
                po.getPayAmount(),
                po.getReceiveCurrency(),
                po.getReceiveAmount(),
                po.getSettlementDate(),
                po.getStatus(),
                po.getFailureReason(),
                po.getSettledAt()
        );
    }

    private PvpSettlementPairPO toPO(PvpSettlementPair pair) {
        PvpSettlementPairPO po = new PvpSettlementPairPO();
        po.setId(pair.getId());
        po.setPairId(pair.getPairId());
        po.setPayInstructionId(pair.getPayInstructionId());
        po.setPayInstructionNo(pair.getPayInstructionNo());
        po.setReceiveInstructionId(pair.getReceiveInstructionId());
        po.setReceiveInstructionNo(pair.getReceiveInstructionNo());
        po.setPayCurrency(pair.getPayCurrency());
        po.setPayAmount(pair.getPayAmount());
        po.setReceiveCurrency(pair.getReceiveCurrency());
        po.setReceiveAmount(pair.getReceiveAmount());
        po.setSettlementDate(pair.getSettlementDate());
        po.setStatus(pair.getStatus());
        po.setFailureReason(pair.getFailureReason());
        po.setSettledAt(pair.getSettledAt());
        po.setVersion(pair.getVersion());
        return po;
    }
}
