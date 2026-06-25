package com.forex.settlement.domain.service;

import com.forex.settlement.domain.event.LcDocCheckedEvent;
import com.forex.settlement.domain.event.LcIssuedEvent;
import com.forex.settlement.domain.model.aggregate.LetterOfCredit;
import com.forex.settlement.domain.model.valueobject.LcNo;
import com.forex.settlement.domain.repository.LcRepository;

import java.math.BigDecimal;
import java.time.LocalDate;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional
public class LcDomainService {

    private final LcRepository lcRepository;
    private final ApplicationEventPublisher eventPublisher;

    public LetterOfCredit createLc(LetterOfCredit lc) {
        String lcNo = LcNo.generate(lc.getLcType()).getValue();
        lc.setLcNo(lcNo);

        LetterOfCredit saved = lcRepository.save(lc);

        log.info("创建信用证: lcNo={}, amount={} {}", saved.getLcNo(), saved.getLcAmount(), saved.getLcCurrency());
        return saved;
    }

    public void issueLc(LetterOfCredit lc) {
        lc.issue();
        lcRepository.save(lc);

        eventPublisher.publishEvent(new LcIssuedEvent(lc.getId(), lc.getLcNo(), lc.getLcAmount()));

        log.info("信用证已开立: lcNo={}", lc.getLcNo());
    }

    public void adviseLc(LetterOfCredit lc) {
        lc.advise();
        lcRepository.save(lc);

        log.info("信用证已通知: lcNo={}", lc.getLcNo());
    }

    public void presentDocuments(LetterOfCredit lc) {
        lc.presentDocuments();
        lcRepository.save(lc);

        log.info("信用证交单: lcNo={}", lc.getLcNo());
    }

    public void checkDocuments(LetterOfCredit lc, boolean isDiscrepant) {
        lc.checkDocuments(isDiscrepant);
        lcRepository.save(lc);

        eventPublisher.publishEvent(new LcDocCheckedEvent(lc.getId(), lc.getLcNo(), isDiscrepant));

        log.info("信用证审单: lcNo={}, disapprove={}", lc.getLcNo(), isDiscrepant);
    }

    public void acceptLc(LetterOfCredit lc) {
        lc.accept();
        lcRepository.save(lc);

        log.info("信用证已承兑: lcNo={}", lc.getLcNo());
    }

    public void payLc(LetterOfCredit lc) {
        lc.pay();
        lcRepository.save(lc);

        log.info("信用证已付款: lcNo={}", lc.getLcNo());
    }

    public void amendLc(LetterOfCredit lc, BigDecimal newAmount, LocalDate newExpiryDate) {
        lc.amend(newAmount, newExpiryDate, null);
        lcRepository.save(lc);

        log.info("信用证已修改: lcNo={}, newAmount={}, newExpiryDate={}", lc.getLcNo(), newAmount, newExpiryDate);
    }
}
