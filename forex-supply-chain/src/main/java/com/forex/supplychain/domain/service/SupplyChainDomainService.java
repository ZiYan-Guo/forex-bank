package com.forex.supplychain.domain.service;

import com.forex.supplychain.domain.model.aggregate.FactoringContract;
import com.forex.supplychain.domain.model.aggregate.ForfaitingContract;
import com.forex.supplychain.domain.repository.FactoringContractRepository;
import com.forex.supplychain.domain.repository.ForfaitingContractRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDate;

@Service
@RequiredArgsConstructor
public class SupplyChainDomainService {

    private final FactoringContractRepository factoringRepository;
    private final ForfaitingContractRepository forfaitingRepository;

    public FactoringContract createFactoring(Long sellerId, Long buyerId, String factoringType,
                                              String recourse, BigDecimal invoiceAmount, String currency,
                                              BigDecimal advanceRate, BigDecimal factoringFee,
                                              LocalDate invoiceDate, LocalDate dueDate,
                                              String goodsDescription, String invoiceNo) {
        FactoringContract contract = FactoringContract.create(sellerId, buyerId, factoringType,
                recourse, invoiceAmount, currency, advanceRate, factoringFee,
                invoiceDate, dueDate, goodsDescription, invoiceNo);
        contract.assignContractNo("FC" + System.currentTimeMillis());
        factoringRepository.save(contract);
        return contract;
    }

    public ForfaitingContract createForfaiting(Long exporterId, Long importerId,
                                                 String forfaitingType, BigDecimal faceAmount,
                                                 String currency, BigDecimal discountRate,
                                                 LocalDate shipmentDate, LocalDate maturityDate,
                                                 String goodsDescription, String issuingBank) {
        ForfaitingContract contract = ForfaitingContract.create(exporterId, importerId,
                forfaitingType, faceAmount, currency, discountRate,
                shipmentDate, maturityDate, goodsDescription, issuingBank);
        contract.assignContractNo("FF" + System.currentTimeMillis());
        forfaitingRepository.save(contract);
        return contract;
    }
}
