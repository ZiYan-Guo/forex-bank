package com.forex.settlement.domain.service;

import com.forex.settlement.domain.model.entity.DocumentaryCollection;
import com.forex.settlement.domain.repository.CollectionRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.concurrent.ThreadLocalRandom;

@Slf4j
@Service
@RequiredArgsConstructor
public class CollectionDomainService {

    private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern("yyyyMMddHHmmss");

    private final CollectionRepository collectionRepository;

    public DocumentaryCollection createCollection(Long customerId, String collectionType,
                                                    String collectionForm, BigDecimal collectionAmount,
                                                    String collectionCurrency, String drawerInfo,
                                                    String draweeInfo, String remittingBank,
                                                    String collectingBank, String documentsList,
                                                    Long operatorId, String remark) {
        String collectionNo = generateCollectionNo();
        DocumentaryCollection col = new DocumentaryCollection(null, collectionNo, customerId,
                collectionType, collectionForm, collectionAmount, collectionCurrency,
                drawerInfo, draweeInfo, remittingBank, collectingBank, documentsList,
                "RECEIVED", null, operatorId, remark);

        DocumentaryCollection saved = collectionRepository.save(col);

        log.info("创建跟单托收: collectionNo={}, amount={} {}", saved.getCollectionNo(),
                saved.getCollectionAmount(), saved.getCollectionCurrency());
        return saved;
    }

    public void markReceivedDocuments(DocumentaryCollection col) {
        collectionRepository.save(col);
        log.info("跟单托收已收单: collectionNo={}", col.getCollectionNo());
    }

    public void presentToDrawee(DocumentaryCollection col) {
        collectionRepository.save(col);
        log.info("跟单托收已向付款人提示: collectionNo={}", col.getCollectionNo());
    }

    public void accept(DocumentaryCollection col) {
        collectionRepository.save(col);
        log.info("跟单托收已承兑: collectionNo={}", col.getCollectionNo());
    }

    public void pay(DocumentaryCollection col) {
        collectionRepository.save(col);
        log.info("跟单托收已付款: collectionNo={}", col.getCollectionNo());
    }

    private String generateCollectionNo() {
        String datePart = LocalDateTime.now().format(DATE_FORMATTER);
        int randomPart = ThreadLocalRandom.current().nextInt(1000, 10000);
        return "COLL" + datePart + randomPart;
    }
}
