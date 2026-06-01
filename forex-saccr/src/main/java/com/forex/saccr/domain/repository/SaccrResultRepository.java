package com.forex.saccr.domain.repository;

import com.forex.common.base.dto.PageResp;
import com.forex.saccr.domain.model.aggregate.SaccrResult;
import com.forex.saccr.domain.model.query.SaccrQuery;

import java.util.Optional;

public interface SaccrResultRepository {

    SaccrResult save(SaccrResult result);

    Optional<SaccrResult> findById(Long id);

    Optional<SaccrResult> findByCalcNo(String calcNo);

    PageResp<SaccrResult> pageQuery(SaccrQuery query);
}
