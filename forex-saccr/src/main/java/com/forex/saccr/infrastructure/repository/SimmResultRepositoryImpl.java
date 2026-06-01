package com.forex.saccr.infrastructure.repository;

import com.forex.common.base.dto.PageResp;
import com.forex.saccr.domain.model.aggregate.SimmResult;
import com.forex.saccr.domain.model.query.SimmQuery;
import com.forex.saccr.domain.repository.SimmResultRepository;
import com.forex.saccr.infrastructure.mapper.SimmResultMapper;
import com.forex.saccr.infrastructure.persistence.SimmResultPO;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;

import lombok.RequiredArgsConstructor;

import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
@RequiredArgsConstructor
public class SimmResultRepositoryImpl implements SimmResultRepository {

    private final SimmResultMapper simmResultMapper;

    @Override
    public SimmResult save(SimmResult result) {
        SimmResultPO po = toPO(result);
        if (result.getId() == null) {
            simmResultMapper.insert(po);
        } else {
            simmResultMapper.updateById(po);
        }
        return toDomain(po);
    }

    @Override
    public Optional<SimmResult> findById(Long id) {
        SimmResultPO po = simmResultMapper.selectById(id);
        return Optional.ofNullable(po).map(this::toDomain);
    }

    @Override
    public Optional<SimmResult> findByCalcNo(String calcNo) {
        SimmResultPO po = simmResultMapper.selectByCalcNo(calcNo);
        return Optional.ofNullable(po).map(this::toDomain);
    }

    @Override
    public PageResp<SimmResult> pageQuery(SimmQuery query) {
        Page<SimmResultPO> page = new Page<>(query.getPageNum(), query.getPageSize());
        Page<SimmResultPO> result = simmResultMapper.pageQuery(page, query);
        List<SimmResult> records = result.getRecords().stream()
                .map(this::toDomain)
                .toList();
        return PageResp.of(result.getTotal(), records, query.getPageNum(), query.getPageSize());
    }

    private SimmResult toDomain(SimmResultPO po) {
        return SimmResult.reconstitute(
                po.getId(), po.getCalcNo(), po.getTradeId(),
                po.getTradeNo(), po.getCalcDate(),
                po.getNotionalAmount(), po.getDeltaMargin(),
                po.getVegaMargin(), po.getCurvatureMargin(),
                po.getTotalMargin(), po.getCalcMethod(),
                po.getSensitivitiesJson());
    }

    private SimmResultPO toPO(SimmResult result) {
        SimmResultPO po = new SimmResultPO();
        po.setId(result.getId());
        po.setCalcNo(result.getCalcNo());
        po.setTradeId(result.getTradeId());
        po.setTradeNo(result.getTradeNo());
        po.setCalcDate(result.getCalcDate());
        po.setNotionalAmount(result.getNotionalAmount());
        po.setDeltaMargin(result.getDeltaMargin());
        po.setVegaMargin(result.getVegaMargin());
        po.setCurvatureMargin(result.getCurvatureMargin());
        po.setTotalMargin(result.getTotalMargin());
        po.setCalcMethod(result.getCalcMethod());
        po.setSensitivitiesJson(result.getSensitivitiesJson());
        return po;
    }
}
