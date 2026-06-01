package com.forex.settlement.domain.repository;

import com.forex.common.base.dto.PageResp;
import com.forex.settlement.domain.model.aggregate.LetterOfCredit;
import com.forex.settlement.domain.model.query.LcQuery;

import java.util.List;
import java.util.Optional;

public interface LcRepository {

    LetterOfCredit save(LetterOfCredit lc);

    Optional<LetterOfCredit> findById(Long id);

    Optional<LetterOfCredit> findByLcNo(String lcNo);

    List<LetterOfCredit> findByCustomerId(Long customerId);

    PageResp<LetterOfCredit> pageQuery(LcQuery query);
}
