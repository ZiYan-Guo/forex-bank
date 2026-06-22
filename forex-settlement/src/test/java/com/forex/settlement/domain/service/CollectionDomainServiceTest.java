package com.forex.settlement.domain.service;

import com.forex.settlement.domain.model.entity.DocumentaryCollection;
import com.forex.settlement.domain.repository.CollectionRepository;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class CollectionDomainServiceTest {

    @Mock private CollectionRepository collectionRepository;

    private CollectionDomainService collectionDomainService;

    @BeforeEach
    void setUp() {
        collectionDomainService = new CollectionDomainService(collectionRepository);
    }

    @Test
    @DisplayName("Create collection generates collection number and saves")
    void testCreateCollection() {
        when(collectionRepository.save(any())).thenAnswer(i -> i.getArgument(0));

        DocumentaryCollection result = collectionDomainService.createCollection(
                1001L, "DP", "DOCUMENTS_AGAINST_PAYMENT",
                new BigDecimal("300000.00"), "USD",
                "DRAWER TRADING CO", "DRAWEE IMPORT CO",
                "REMMITTING BANK", "COLLECTING BANK",
                "Invoice, B/L, Packing List", 1001L, "Test collection");

        assertNotNull(result);
        assertTrue(result.getCollectionNo().startsWith("COLL"));
        assertEquals("RECEIVED", result.getCollectionStatus());
        assertEquals(new BigDecimal("300000.00"), result.getCollectionAmount());
        verify(collectionRepository).save(any());
    }

    @Test
    @DisplayName("Mark received documents saves the collection")
    void testMarkReceivedDocuments() {
        DocumentaryCollection col = createCollection();
        when(collectionRepository.save(any())).thenReturn(col);

        collectionDomainService.markReceivedDocuments(col);

        verify(collectionRepository).save(col);
    }

    @Test
    @DisplayName("Present to drawee saves the collection")
    void testPresentToDrawee() {
        DocumentaryCollection col = createCollection();
        when(collectionRepository.save(any())).thenReturn(col);

        collectionDomainService.presentToDrawee(col);

        verify(collectionRepository).save(col);
    }

    @Test
    @DisplayName("Accept saves the collection")
    void testAccept() {
        DocumentaryCollection col = createCollection();
        when(collectionRepository.save(any())).thenReturn(col);

        collectionDomainService.accept(col);

        verify(collectionRepository).save(col);
    }

    @Test
    @DisplayName("Pay saves the collection")
    void testPay() {
        DocumentaryCollection col = createCollection();
        when(collectionRepository.save(any())).thenReturn(col);

        collectionDomainService.pay(col);

        verify(collectionRepository).save(col);
    }

    @Test
    @DisplayName("Create collection with DA type")
    void testCreateCollection_DA() {
        when(collectionRepository.save(any())).thenAnswer(i -> i.getArgument(0));

        DocumentaryCollection result = collectionDomainService.createCollection(
                1001L, "DA", "DOCUMENTS_AGAINST_ACCEPTANCE",
                new BigDecimal("150000.00"), "EUR",
                "EXPORTER SRL", "IMPORTER SPA",
                "BANK A", "BANK B",
                "Invoice, Certificate", 1001L, "DA collection");

        assertTrue(result.getCollectionNo().startsWith("COLL"));
        assertEquals("DA", result.getCollectionType());
        assertEquals("EUR", result.getCollectionCurrency());
    }

    private DocumentaryCollection createCollection() {
        return new DocumentaryCollection(null, "COLL20260601001", 1001L,
                "DP", "DOCUMENTS_AGAINST_PAYMENT",
                new BigDecimal("100000.00"), "USD",
                "DRAWER CO", "DRAWEE CO",
                "REMIT BANK", "COLLECT BANK",
                "Invoice", "RECEIVED", null, 1001L, null);
    }
}
