package com.forex.clearing.infrastructure.repository;

import com.forex.clearing.domain.model.aggregate.ClsSession;
import com.forex.clearing.domain.repository.ClsSessionRepository;
import com.forex.clearing.infrastructure.mapper.ClsSessionMapper;
import com.forex.clearing.infrastructure.persistence.ClsSessionPO;

import lombok.RequiredArgsConstructor;

import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.Optional;

@Repository
@RequiredArgsConstructor
public class ClsSessionRepositoryImpl implements ClsSessionRepository {

    private final ClsSessionMapper mapper;

    @Override
    public ClsSession save(ClsSession session) {
        ClsSessionPO po = toPO(session);
        if (session.getId() == null) {
            mapper.insert(po);
        } else {
            mapper.updateById(po);
        }
        return toDomain(po);
    }

    @Override
    public Optional<ClsSession> findById(Long id) {
        ClsSessionPO po = mapper.selectById(id);
        return Optional.ofNullable(po).map(this::toDomain);
    }

    @Override
    public Optional<ClsSession> findBySessionId(String sessionId) {
        ClsSessionPO po = mapper.selectBySessionId(sessionId);
        return Optional.ofNullable(po).map(this::toDomain);
    }

    @Override
    public Optional<ClsSession> findBySettlementDate(LocalDate settlementDate) {
        ClsSessionPO po = mapper.selectBySettlementDate(settlementDate);
        return Optional.ofNullable(po).map(this::toDomain);
    }

    private ClsSession toDomain(ClsSessionPO po) {
        return ClsSession.reconstitute(
                po.getId(),
                po.getSessionId(),
                po.getSettlementDate(),
                po.getPayInWindowStart(),
                po.getPayInWindowEnd(),
                po.getSessionStatus(),
                po.getTotalPayInSum(),
                po.getTotalPayOutSum(),
                po.getNetPosition(),
                po.getPositionJson()
        );
    }

    private ClsSessionPO toPO(ClsSession session) {
        ClsSessionPO po = new ClsSessionPO();
        po.setId(session.getId());
        po.setSessionId(session.getSessionId());
        po.setSettlementDate(session.getSettlementDate());
        po.setPayInWindowStart(session.getPayInWindowStart());
        po.setPayInWindowEnd(session.getPayInWindowEnd());
        po.setSessionStatus(session.getSessionStatus());
        po.setTotalPayInSum(session.getTotalPayInSum());
        po.setTotalPayOutSum(session.getTotalPayOutSum());
        po.setNetPosition(session.getNetPosition());
        po.setPositionJson(session.getPositionJson());
        po.setVersion(session.getVersion());
        return po;
    }
}
