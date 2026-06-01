package com.forex.payment.infrastructure.repository;

import com.forex.payment.domain.model.entity.BlacklistHit;
import com.forex.payment.domain.repository.BlacklistHitRepository;
import com.forex.payment.infrastructure.mapper.BlacklistHitMapper;
import com.forex.payment.infrastructure.persistence.BlacklistHitPO;

import lombok.RequiredArgsConstructor;

import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
@RequiredArgsConstructor
public class BlacklistHitRepositoryImpl implements BlacklistHitRepository {

    private final BlacklistHitMapper blacklistHitMapper;

    @Override
    public BlacklistHit save(BlacklistHit hit) {
        BlacklistHitPO po = toPO(hit);
        if (hit.getId() == null) {
            blacklistHitMapper.insert(po);
        } else {
            blacklistHitMapper.updateById(po);
        }
        return toDomain(po);
    }

    @Override
    public List<BlacklistHit> findByPaymentId(Long paymentId) {
        List<BlacklistHitPO> poList = blacklistHitMapper.selectByPaymentId(paymentId);
        return poList.stream().map(this::toDomain).toList();
    }

    private BlacklistHit toDomain(BlacklistHitPO po) {
        return new BlacklistHit(
                po.getId(),
                po.getPaymentId(),
                po.getPaymentNo(),
                po.getHitType(),
                po.getHitListName(),
                po.getHitField(),
                po.getHitValue(),
                po.getMatchScore(),
                po.getCheckTime(),
                po.getCheckResult(),
                po.getReviewerId(),
                po.getReviewTime(),
                po.getReviewComment()
        );
    }

    private BlacklistHitPO toPO(BlacklistHit hit) {
        BlacklistHitPO po = new BlacklistHitPO();
        po.setId(hit.getId());
        po.setPaymentId(hit.getPaymentId());
        po.setPaymentNo(hit.getPaymentNo());
        po.setHitType(hit.getHitType());
        po.setHitListName(hit.getHitListName());
        po.setHitField(hit.getHitField());
        po.setHitValue(hit.getHitValue());
        po.setMatchScore(hit.getMatchScore());
        po.setCheckTime(hit.getCheckTime());
        po.setCheckResult(hit.getCheckResult());
        po.setReviewerId(hit.getReviewerId());
        po.setReviewTime(hit.getReviewTime());
        po.setReviewComment(hit.getReviewComment());
        return po;
    }
}
