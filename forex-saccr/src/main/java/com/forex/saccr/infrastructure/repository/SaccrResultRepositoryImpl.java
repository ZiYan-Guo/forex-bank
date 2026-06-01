package com.forex.saccr.infrastructure.repository;

import com.forex.common.base.dto.PageResp;
import com.forex.saccr.domain.model.aggregate.SaccrResult;
import com.forex.saccr.domain.model.query.SaccrQuery;
import com.forex.saccr.domain.repository.SaccrResultRepository;
import com.forex.saccr.infrastructure.mapper.SaccrResultMapper;
import com.forex.saccr.infrastructure.persistence.SaccrResultPO;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;

import lombok.RequiredArgsConstructor;

import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
@RequiredArgsConstructor
public class SaccrResultRepositoryImpl implements SaccrResultRepository {

    private final SaccrResultMapper saccrResultMapper;

    @Override
    public SaccrResult save(SaccrResult result) {
        SaccrResultPO po = toPO(result);
        if (result.getId() == null) {
            saccrResultMapper.insert(po);
        } else {
            saccrResultMapper.updateById(po);
        }
        return toDomain(po);
    }

    @Override
    public Optional<SaccrResult> findById(Long id) {
        SaccrResultPO po = saccrResultMapper.selectById(id);
        return Optional.ofNullable(po).map(this::toDomain);
    }

    @Override
    public Optional<SaccrResult> findByCalcNo(String calcNo) {
        SaccrResultPO po = saccrResultMapper.selectByCalcNo(calcNo);
        return Optional.ofNullable(po).map(this::toDomain);
    }

    @Override
    public PageResp<SaccrResult> pageQuery(SaccrQuery query) {
        Page<SaccrResultPO> page = new Page<>(query.getPageNum(), query.getPageSize());
        Page<SaccrResultPO> result = saccrResultMapper.pageQuery(page, query);
        List<SaccrResult> records = result.getRecords().stream()
                .map(this::toDomain)
                .toList();
        return PageResp.of(result.getTotal(), records, query.getPageNum(), query.getPageSize());
    }

    private SaccrResult toDomain(SaccrResultPO po) {
        return SaccrResult.reconstitute(
                po.getId(), po.getCalcNo(), po.getTradeId(),
                po.getTradeNo(), po.getCounterPartyId(),
                po.getCalcDate(), po.getRc(), po.getPfe(),
                po.getExposure(), po.getAlpha(), po.getCalcMethod(),
                po.getResultJson());
    }

    private SaccrResultPO toPO(SaccrResult result) {
        SaccrResultPO po = new SaccrResultPO();
        po.setId(result.getId());
        po.setCalcNo(result.getCalcNo());
        po.setTradeId(result.getTradeId());
        po.setTradeNo(result.getTradeNo());
        po.setCounterPartyId(result.getCounterPartyId());
        po.setCalcDate(result.getCalcDate());
        po.setRc(result.getRc());
        po.setPfe(result.getPfe());
        po.setExposure(result.getExposure());
        po.setAlpha(result.getAlpha());
        po.setCalcMethod(result.getCalcMethod());
        po.setResultJson(result.getResultJson());
        return po;
    }
}
