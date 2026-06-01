package com.forex.margin.domain.repository;

import com.forex.margin.domain.model.entity.MarginCall;

import java.util.List;

public interface MarginCallRepository {

    void save(MarginCall marginCall);

    MarginCall findById(Long id);

    List<MarginCall> findByMarginId(Long marginId);
}
