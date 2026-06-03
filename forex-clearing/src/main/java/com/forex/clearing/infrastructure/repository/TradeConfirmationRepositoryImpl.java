package com.forex.clearing.infrastructure.repository;

import com.forex.clearing.domain.model.aggregate.TradeConfirmation;
import com.forex.clearing.domain.repository.TradeConfirmationRepository;
import com.forex.clearing.infrastructure.mapper.TradeConfirmationMapper;
import com.forex.clearing.infrastructure.persistence.TradeConfirmationPO;

import lombok.RequiredArgsConstructor;

import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
@RequiredArgsConstructor
public class TradeConfirmationRepositoryImpl implements TradeConfirmationRepository {

    private final TradeConfirmationMapper mapper;

    @Override
    public TradeConfirmation save(TradeConfirmation cfm) {
        TradeConfirmationPO po = toPO(cfm);
        if (cfm.getId() == null) {
            mapper.insert(po);
        } else {
            mapper.updateById(po);
        }
        return toDomain(po);
    }

    @Override
    public Optional<TradeConfirmation> findById(Long id) {
        TradeConfirmationPO po = mapper.selectById(id);
        return Optional.ofNullable(po).map(this::toDomain);
    }

    @Override
    public Optional<TradeConfirmation> findByConfirmId(String confirmId) {
        TradeConfirmationPO po = mapper.selectByConfirmId(confirmId);
        return Optional.ofNullable(po).map(this::toDomain);
    }

    @Override
    public Optional<TradeConfirmation> findByTradeNo(String tradeNo) {
        TradeConfirmationPO po = mapper.selectByTradeNo(tradeNo);
        return Optional.ofNullable(po).map(this::toDomain);
    }

    @Override
    public List<TradeConfirmation> findByMatchStatus(String matchStatus) {
        return mapper.selectByMatchStatus(matchStatus).stream()
                .map(this::toDomain)
                .toList();
    }

    @Override
    public List<TradeConfirmation> findAll() {
        return mapper.selectAll().stream()
                .map(this::toDomain)
                .toList();
    }

    private TradeConfirmation toDomain(TradeConfirmationPO po) {
        return TradeConfirmation.reconstitute(
                po.getId(),
                po.getConfirmId(),
                po.getTradeNo(),
                po.getTradeType(),
                po.getConfirmFlag(),
                po.getCurrencyPair(),
                po.getDirection(),
                po.getAmount(),
                po.getRate(),
                po.getValueDate(),
                po.getCounterparty(),
                po.getMatchStatus(),
                po.getExternalRef(),
                po.getDiscrepancyDetail(),
                po.getRetryCount(),
                po.getNextRetryAt(),
                po.getResolutionAction(),
                po.getResolutionComment()
        );
    }

    private TradeConfirmationPO toPO(TradeConfirmation cfm) {
        TradeConfirmationPO po = new TradeConfirmationPO();
        po.setId(cfm.getId());
        po.setConfirmId(cfm.getConfirmId());
        po.setTradeNo(cfm.getTradeNo());
        po.setTradeType(cfm.getTradeType());
        po.setConfirmFlag(cfm.getConfirmFlag() != null ? cfm.getConfirmFlag().name() : null);
        po.setCurrencyPair(cfm.getCurrencyPair());
        po.setDirection(cfm.getDirection());
        po.setAmount(cfm.getAmount());
        po.setRate(cfm.getRate());
        po.setValueDate(cfm.getValueDate());
        po.setCounterparty(cfm.getCounterparty());
        po.setMatchStatus(cfm.getMatchStatus());
        po.setExternalRef(cfm.getExternalRef());
        po.setDiscrepancyDetail(cfm.getDiscrepancyDetail());
        po.setRetryCount(cfm.getRetryCount());
        po.setNextRetryAt(cfm.getNextRetryAt());
        po.setResolutionAction(cfm.getResolutionAction());
        po.setResolutionComment(cfm.getResolutionComment());
        po.setVersion(cfm.getVersion());
        return po;
    }
}
