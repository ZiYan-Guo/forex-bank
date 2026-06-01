package com.forex.position.infrastructure.repository;

import com.forex.common.base.dto.PageResp;
import com.forex.position.domain.model.aggregate.Position;
import com.forex.position.domain.model.query.PositionQuery;
import com.forex.position.domain.repository.PositionRepository;
import com.forex.position.infrastructure.mapper.PositionMapper;
import com.forex.position.infrastructure.persistence.PositionPO;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;

import lombok.RequiredArgsConstructor;

import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
@RequiredArgsConstructor
public class PositionRepositoryImpl implements PositionRepository {

    private final PositionMapper positionMapper;

    @Override
    public Position save(Position position) {
        PositionPO po = toPO(position);
        if (position.getId() == null) {
            positionMapper.insert(po);
        } else {
            positionMapper.updateById(po);
        }
        return toDomain(po);
    }

    @Override
    public Optional<Position> findById(Long id) {
        PositionPO po = positionMapper.selectById(id);
        return Optional.ofNullable(po).map(this::toDomain);
    }

    @Override
    public Optional<Position> findByPositionNo(String positionNo) {
        PositionPO po = positionMapper.selectByPositionNo(positionNo);
        return Optional.ofNullable(po).map(this::toDomain);
    }

    @Override
    public PageResp<Position> pageQuery(PositionQuery query) {
        Page<PositionPO> page = new Page<>(query.getPageNum(), query.getPageSize());
        Page<PositionPO> result = positionMapper.pageQuery(page, query);
        List<Position> records = result.getRecords().stream()
                .map(this::toDomain)
                .toList();
        return PageResp.of(result.getTotal(), records, query.getPageNum(), query.getPageSize());
    }

    private Position toDomain(PositionPO po) {
        return Position.reconstitute(
                po.getId(),
                po.getPositionNo(),
                po.getCurrencyPair(),
                po.getPositionType(),
                po.getPositionCurrency(),
                po.getLongAmount(),
                po.getShortAmount(),
                po.getNetPosition(),
                po.getPositionLimit(),
                po.getLimitUsagePct(),
                po.getPositionDate(),
                po.getTraderId(),
                po.getBranchCode(),
                po.getRiskLevel(),
                po.getHedgingAction()
        );
    }

    private PositionPO toPO(Position position) {
        PositionPO po = new PositionPO();
        po.setId(position.getId());
        po.setPositionNo(position.getPositionNo());
        po.setCurrencyPair(position.getCurrencyPair());
        po.setPositionType(position.getPositionType());
        po.setPositionCurrency(position.getPositionCurrency());
        po.setLongAmount(position.getLongAmount());
        po.setShortAmount(position.getShortAmount());
        po.setNetPosition(position.getNetPosition());
        po.setPositionLimit(position.getPositionLimit());
        po.setLimitUsagePct(position.getLimitUsagePct());
        po.setPositionDate(position.getPositionDate());
        po.setTraderId(position.getTraderId());
        po.setBranchCode(position.getBranchCode());
        po.setRiskLevel(position.getRiskLevel());
        po.setHedgingAction(position.getHedgingAction());
        return po;
    }
}
