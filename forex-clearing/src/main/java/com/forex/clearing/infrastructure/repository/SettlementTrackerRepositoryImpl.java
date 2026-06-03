package com.forex.clearing.infrastructure.repository;

import com.forex.clearing.domain.model.aggregate.SettlementTracker;
import com.forex.clearing.domain.repository.SettlementTrackerRepository;
import com.forex.clearing.infrastructure.mapper.SettlementTrackerMapper;
import com.forex.clearing.infrastructure.persistence.SettlementTrackerPO;

import lombok.RequiredArgsConstructor;

import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
@RequiredArgsConstructor
public class SettlementTrackerRepositoryImpl implements SettlementTrackerRepository {

    private final SettlementTrackerMapper mapper;

    @Override
    public SettlementTracker save(SettlementTracker tracker) {
        SettlementTrackerPO po = toPO(tracker);
        if (tracker.getId() == null) {
            mapper.insert(po);
        } else {
            mapper.updateById(po);
        }
        return toDomain(po);
    }

    @Override
    public Optional<SettlementTracker> findById(Long id) {
        SettlementTrackerPO po = mapper.selectById(id);
        return Optional.ofNullable(po).map(this::toDomain);
    }

    @Override
    public Optional<SettlementTracker> findByTrackingId(String trackingId) {
        SettlementTrackerPO po = mapper.selectByTrackingId(trackingId);
        return Optional.ofNullable(po).map(this::toDomain);
    }

    @Override
    public List<SettlementTracker> findAll() {
        return mapper.selectAll().stream()
                .map(this::toDomain)
                .toList();
    }

    private SettlementTracker toDomain(SettlementTrackerPO po) {
        return SettlementTracker.reconstitute(
                po.getId(),
                po.getTrackingId(),
                po.getPaymentNo(),
                po.getInstructionNo(),
                po.getCurrentStatus(),
                po.getStatusChangedAt(),
                po.getChannel(),
                po.getGpiStatus(),
                po.getExceptionReason(),
                po.getExceptionDetail()
        );
    }

    private SettlementTrackerPO toPO(SettlementTracker tracker) {
        SettlementTrackerPO po = new SettlementTrackerPO();
        po.setId(tracker.getId());
        po.setTrackingId(tracker.getTrackingId());
        po.setPaymentNo(tracker.getPaymentNo());
        po.setInstructionNo(tracker.getInstructionNo());
        po.setCurrentStatus(tracker.getCurrentStatus());
        po.setStatusChangedAt(tracker.getStatusChangedAt());
        po.setChannel(tracker.getChannel());
        po.setGpiStatus(tracker.getGpiStatus());
        po.setExceptionReason(tracker.getExceptionReason());
        po.setExceptionDetail(tracker.getExceptionDetail());
        return po;
    }
}
