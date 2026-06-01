package com.forex.settlement.domain.repository;

import com.forex.common.base.dto.PageResp;
import com.forex.settlement.domain.model.entity.DocumentaryCollection;
import com.forex.settlement.domain.model.query.CollectionQuery;

import java.util.Optional;

public interface CollectionRepository {

    DocumentaryCollection save(DocumentaryCollection collection);

    Optional<DocumentaryCollection> findById(Long id);

    Optional<DocumentaryCollection> findByCollectionNo(String collectionNo);

    PageResp<DocumentaryCollection> pageQuery(CollectionQuery query);
}
