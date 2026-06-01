package com.forex.settlement.application.service;

import com.forex.common.base.annotation.RedisLock;
import com.forex.common.base.dto.PageResp;
import com.forex.settlement.application.command.AmendLcCmd;
import com.forex.settlement.application.command.CreateCollectionCmd;
import com.forex.settlement.application.command.CreateGuaranteeCmd;
import com.forex.settlement.application.command.CreateLcCmd;
import com.forex.settlement.domain.model.aggregate.LetterOfCredit;
import com.forex.settlement.domain.model.entity.BankGuarantee;
import com.forex.settlement.domain.model.entity.DocumentaryCollection;
import com.forex.settlement.domain.model.query.CollectionQuery;
import com.forex.settlement.domain.model.query.GuaranteeQuery;
import com.forex.settlement.domain.model.query.LcQuery;
import com.forex.settlement.domain.repository.CollectionRepository;
import com.forex.settlement.domain.repository.GuaranteeRepository;
import com.forex.settlement.domain.repository.LcRepository;
import com.forex.settlement.domain.service.CollectionDomainService;
import com.forex.settlement.domain.service.GuaranteeDomainService;
import com.forex.settlement.domain.service.LcDomainService;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;

@Service
@RequiredArgsConstructor
@Transactional
public class SettlementAppService {

    private final LcDomainService lcDomainService;
    private final LcRepository lcRepository;
    private final CollectionDomainService collectionDomainService;
    private final CollectionRepository collectionRepository;
    private final GuaranteeDomainService guaranteeDomainService;
    private final GuaranteeRepository guaranteeRepository;

    public LetterOfCredit createLc(CreateLcCmd cmd) {
        LetterOfCredit lc = LetterOfCredit.create(
                cmd.getCustomerId(),
                cmd.getLcType(),
                cmd.getLcDirection(),
                cmd.getLcAmount(),
                cmd.getLcCurrency(),
                cmd.getApplicantName(),
                cmd.getBeneficiaryName(),
                cmd.getIssuingBank(),
                cmd.getExpiryDate(),
                null,
                null,
                cmd.getAvailableBy(),
                null,
                cmd.getRemark()
        );
        return lcDomainService.createLc(lc);
    }

    public LetterOfCredit getLcDetail(String lcNo) {
        return lcRepository.findByLcNo(lcNo)
                .orElseThrow(() -> new IllegalArgumentException("信用证不存在: " + lcNo));
    }

    public PageResp<LetterOfCredit> pageQuery(LcQuery query) {
        return lcRepository.pageQuery(query);
    }

    @RedisLock(key = "#lcNo")
    public void issueLc(String lcNo) {
        LetterOfCredit lc = lcRepository.findByLcNo(lcNo)
                .orElseThrow(() -> new IllegalArgumentException("信用证不存在: " + lcNo));
        lcDomainService.issueLc(lc);
    }

    @RedisLock(key = "#lcNo")
    public void amendLc(String lcNo, AmendLcCmd cmd) {
        LetterOfCredit lc = lcRepository.findByLcNo(lcNo)
                .orElseThrow(() -> new IllegalArgumentException("信用证不存在: " + lcNo));
        lc.amend(cmd.getNewAmount(), cmd.getNewExpiryDate(), null);
        lcRepository.save(lc);
    }

    @RedisLock(key = "#lcNo")
    public void presentDocuments(String lcNo) {
        LetterOfCredit lc = lcRepository.findByLcNo(lcNo)
                .orElseThrow(() -> new IllegalArgumentException("信用证不存在: " + lcNo));
        lcDomainService.presentDocuments(lc);
    }

    @RedisLock(key = "#lcNo")
    public void checkDocuments(String lcNo, boolean isDiscrepant) {
        LetterOfCredit lc = lcRepository.findByLcNo(lcNo)
                .orElseThrow(() -> new IllegalArgumentException("信用证不存在: " + lcNo));
        lcDomainService.checkDocuments(lc, isDiscrepant);
    }

    @RedisLock(key = "#lcNo")
    public void acceptLc(String lcNo) {
        LetterOfCredit lc = lcRepository.findByLcNo(lcNo)
                .orElseThrow(() -> new IllegalArgumentException("信用证不存在: " + lcNo));
        lcDomainService.acceptLc(lc);
    }

    @RedisLock(key = "#lcNo")
    public void payLc(String lcNo) {
        LetterOfCredit lc = lcRepository.findByLcNo(lcNo)
                .orElseThrow(() -> new IllegalArgumentException("信用证不存在: " + lcNo));
        lcDomainService.payLc(lc);
    }

    public DocumentaryCollection createCollection(CreateCollectionCmd cmd) {
        return collectionDomainService.createCollection(
                cmd.getCustomerId(),
                cmd.getCollectionType(),
                cmd.getCollectionForm(),
                cmd.getCollectionAmount(),
                cmd.getCollectionCurrency(),
                null,
                cmd.getDraweeName(),
                null,
                null,
                cmd.getDocumentsList(),
                null,
                cmd.getRemark()
        );
    }

    @RedisLock(key = "#collectionNo")
    public DocumentaryCollection getCollectionDetail(String collectionNo) {
        return collectionRepository.findByCollectionNo(collectionNo)
                .orElseThrow(() -> new IllegalArgumentException("托收不存在: " + collectionNo));
    }

    public void payCollection(String collectionNo) {
        DocumentaryCollection col = collectionRepository.findByCollectionNo(collectionNo)
                .orElseThrow(() -> new IllegalArgumentException("托收不存在: " + collectionNo));
        collectionDomainService.pay(col);
    }

    public BankGuarantee createGuarantee(CreateGuaranteeCmd cmd) {
        return guaranteeDomainService.createGuarantee(
                cmd.getCustomerId(),
                cmd.getGuaranteeType(),
                cmd.getGuaranteeAmount(),
                cmd.getGuaranteeCurrency(),
                cmd.getBeneficiaryName(),
                LocalDate.now(),
                cmd.getEffectiveDate(),
                cmd.getExpiryDate(),
                null,
                null,
                cmd.getGuaranteeFormat(),
                null,
                cmd.getRemark()
        );
    }

    @RedisLock(key = "#guaranteeNo")
    public BankGuarantee getGuaranteeDetail(String guaranteeNo) {
        return guaranteeRepository.findByGuaranteeNo(guaranteeNo)
                .orElseThrow(() -> new IllegalArgumentException("保函不存在: " + guaranteeNo));
    }

    public void issueGuarantee(String guaranteeNo) {
        BankGuarantee guarantee = guaranteeRepository.findByGuaranteeNo(guaranteeNo)
                .orElseThrow(() -> new IllegalArgumentException("保函不存在: " + guaranteeNo));
        guaranteeDomainService.issueGuarantee(guarantee);
    }
}
