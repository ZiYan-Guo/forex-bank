package com.forex.settlement.infrastructure.repository;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.forex.common.base.dto.PageResp;
import com.forex.settlement.domain.model.entity.DocumentaryCollection;
import com.forex.settlement.domain.model.query.CollectionQuery;
import com.forex.settlement.domain.repository.CollectionRepository;
import com.forex.settlement.infrastructure.mapper.CollectionMapper;
import com.forex.settlement.infrastructure.persistence.DocumentaryCollectionPO;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
@RequiredArgsConstructor
public class CollectionRepositoryImpl implements CollectionRepository {

    private final CollectionMapper collectionMapper;

    @Override
    public DocumentaryCollection save(DocumentaryCollection collection) {
        DocumentaryCollectionPO po = toPO(collection);
        if (collection.getId() == null) {
            collectionMapper.insert(po);
        } else {
            collectionMapper.updateById(po);
        }
        return toDomain(po);
    }

    @Override
    public Optional<DocumentaryCollection> findById(Long id) {
        DocumentaryCollectionPO po = collectionMapper.selectById(id);
        return Optional.ofNullable(po).map(this::toDomain);
    }

    @Override
    public Optional<DocumentaryCollection> findByCollectionNo(String collectionNo) {
        DocumentaryCollectionPO po = collectionMapper.selectByCollectionNo(collectionNo);
        return Optional.ofNullable(po).map(this::toDomain);
    }

    @Override
    public PageResp<DocumentaryCollection> pageQuery(CollectionQuery query) {
        Page<DocumentaryCollectionPO> page = new Page<>(query.getPageNum(), query.getPageSize());
        collectionMapper.pageQuery(page, query);
        List<DocumentaryCollection> records = page.getRecords().stream().map(this::toDomain).toList();
        return PageResp.of(page.getTotal(), records, (int) page.getCurrent(), (int) page.getSize());
    }

    private DocumentaryCollection toDomain(DocumentaryCollectionPO po) {
        return new DocumentaryCollection(
                po.getId(),
                po.getCollectionNo(),
                po.getCustomerId(),
                po.getCollectionType(),
                po.getCollectionForm(),
                po.getCollectionAmount(),
                po.getCollectionCurrency(),
                po.getDrawerInfo(),
                po.getDraweeInfo(),
                po.getRemittingBank(),
                po.getCollectingBank(),
                po.getDocumentsList(),
                po.getCollectionStatus(),
                po.getSwiftRef(),
                po.getOperatorId(),
                po.getRemark()
        );
    }

    private DocumentaryCollectionPO toPO(DocumentaryCollection collection) {
        DocumentaryCollectionPO po = new DocumentaryCollectionPO();
        po.setId(collection.getId());
        po.setCollectionNo(collection.getCollectionNo());
        po.setCustomerId(collection.getCustomerId());
        po.setCollectionType(collection.getCollectionType());
        po.setCollectionForm(collection.getCollectionForm());
        po.setCollectionAmount(collection.getCollectionAmount());
        po.setCollectionCurrency(collection.getCollectionCurrency());
        po.setDrawerInfo(collection.getDrawerInfo());
        po.setDraweeInfo(collection.getDraweeInfo());
        po.setRemittingBank(collection.getRemittingBank());
        po.setCollectingBank(collection.getCollectingBank());
        po.setDocumentsList(collection.getDocumentsList());
        po.setCollectionStatus(collection.getCollectionStatus());
        po.setSwiftRef(collection.getSwiftRef());
        po.setOperatorId(collection.getOperatorId());
        po.setRemark(collection.getRemark());
        return po;
    }
}
