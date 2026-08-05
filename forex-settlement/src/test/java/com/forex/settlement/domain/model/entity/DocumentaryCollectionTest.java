package com.forex.settlement.domain.model.entity;

import com.forex.common.base.exception.BusinessException;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

class DocumentaryCollectionTest {

    @Test
    @DisplayName("Pay moves received collection to paid")
    void payMovesReceivedCollectionToPaid() {
        DocumentaryCollection collection = newCollection(DocumentaryCollection.STATUS_DOCS_RECEIVED);

        collection.pay();

        assertEquals(DocumentaryCollection.STATUS_PAID, collection.getCollectionStatus());
    }

    @Test
    @DisplayName("Pay rejects draft collection")
    void payRejectsDraftCollection() {
        DocumentaryCollection collection = newCollection(DocumentaryCollection.STATUS_DRAFT);

        assertThrows(BusinessException.class, collection::pay);
    }

    @Test
    @DisplayName("Draft collection can receive documents")
    void draftCollectionCanReceiveDocuments() {
        DocumentaryCollection collection = newCollection(DocumentaryCollection.STATUS_DRAFT);

        collection.markReceivedDocuments();

        assertEquals(DocumentaryCollection.STATUS_DOCS_RECEIVED, collection.getCollectionStatus());
    }

    private DocumentaryCollection newCollection(String status) {
        return new DocumentaryCollection(
                1L,
                "COLL202608050001",
                1001L,
                "DOCUMENTARY",
                "DP",
                new BigDecimal("50000.00"),
                "USD",
                "Exporter",
                "Importer",
                "Bank A",
                "Bank B",
                "Invoice, Packing List, Bill of Lading",
                status,
                null,
                9001L,
                null);
    }
}
