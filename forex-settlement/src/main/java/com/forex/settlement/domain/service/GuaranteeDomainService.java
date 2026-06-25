package com.forex.settlement.domain.service;

import com.forex.settlement.domain.model.entity.BankGuarantee;
import com.forex.settlement.domain.repository.GuaranteeRepository;

import com.forex.common.base.exception.BusinessException;
import com.forex.common.base.result.ResultCode;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.concurrent.ThreadLocalRandom;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional
public class GuaranteeDomainService {

    private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern("yyyyMMddHHmmss");

    private final GuaranteeRepository guaranteeRepository;

    public BankGuarantee createGuarantee(Long customerId, String guaranteeType,
                                           BigDecimal guaranteeAmount, String guaranteeCurrency,
                                           String beneficiaryInfo, LocalDate issueDate,
                                           LocalDate effectiveDate, LocalDate expiryDate,
                                           LocalDate claimExpiryDate, String counterGuaranteeNo,
                                           String guaranteeFormat, Long operatorId, String remark) {
        if (customerId == null) {
            throw new BusinessException(ResultCode.VALIDATE_FAIL, "客户ID不能为空");
        }
        if (guaranteeAmount == null || guaranteeAmount.compareTo(BigDecimal.ZERO) <= 0) {
            throw new BusinessException(ResultCode.VALIDATE_FAIL, "保函金额必须大于0");
        }
        String guaranteeNo = generateGuaranteeNo();
        BankGuarantee g = new BankGuarantee(null, guaranteeNo, customerId,
                guaranteeType, guaranteeAmount, guaranteeCurrency, beneficiaryInfo,
                issueDate, effectiveDate, expiryDate, claimExpiryDate,
                counterGuaranteeNo, guaranteeFormat, "ISSUED",
                BigDecimal.ZERO, BigDecimal.ZERO, operatorId, null, remark);

        BankGuarantee saved = guaranteeRepository.save(g);

        log.info("创建银行保函: guaranteeNo={}, amount={} {}", saved.getGuaranteeNo(),
                saved.getGuaranteeAmount(), saved.getGuaranteeCurrency());
        return saved;
    }

    public void issueGuarantee(BankGuarantee g) {
        guaranteeRepository.save(g);
        log.info("银行保函已开立: guaranteeNo={}", g.getGuaranteeNo());
    }

    public void claim(BankGuarantee g) {
        guaranteeRepository.save(g);
        log.info("银行保函索赔: guaranteeNo={}", g.getGuaranteeNo());
    }

    public void expire(BankGuarantee g) {
        guaranteeRepository.save(g);
        log.info("银行保函已到期: guaranteeNo={}", g.getGuaranteeNo());
    }

    private String generateGuaranteeNo() {
        String datePart = LocalDateTime.now().format(DATE_FORMATTER);
        int randomPart = ThreadLocalRandom.current().nextInt(1000, 10000);
        return "BG" + datePart + randomPart;
    }
}
