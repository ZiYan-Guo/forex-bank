package com.forex.bookkeeping.domain.repository;

import com.forex.bookkeeping.domain.model.aggregate.MonthEndClosing;

import java.util.List;
import java.util.Optional;

public interface MonthEndClosingRepository {

    MonthEndClosing save(MonthEndClosing closing);

    Optional<MonthEndClosing> findById(Long id);

    Optional<MonthEndClosing> findByClosingId(String closingId);

    List<MonthEndClosing> findByFiscalPeriod(String fiscalPeriod);

    List<MonthEndClosing> findByStatus(String status);
}
