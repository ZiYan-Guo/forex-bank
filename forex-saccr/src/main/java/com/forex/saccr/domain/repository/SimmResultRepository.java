package com.forex.saccr.domain.repository;

import com.forex.common.base.dto.PageResp;
import com.forex.saccr.domain.model.aggregate.SimmResult;
import com.forex.saccr.domain.model.query.SimmQuery;

import java.util.Optional;

public interface SimmResultRepository {

    SimmResult save(SimmResult result);

    Optional<SimmResult> findById(Long id);

    Optional<SimmResult> findByCalcNo(String calcNo);

    PageResp<SimmResult> pageQuery(SimmQuery query);
}
