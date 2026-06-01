package com.forex.settlement.infrastructure.repository;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.forex.common.base.dto.PageResp;
import com.forex.settlement.domain.model.aggregate.LetterOfCredit;
import com.forex.settlement.domain.model.query.LcQuery;
import com.forex.settlement.domain.repository.LcRepository;
import com.forex.settlement.infrastructure.mapper.LetterOfCreditMapper;
import com.forex.settlement.infrastructure.persistence.LetterOfCreditPO;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
@RequiredArgsConstructor
public class LcRepositoryImpl implements LcRepository {

    private final LetterOfCreditMapper lcMapper;

    @Override
    public LetterOfCredit save(LetterOfCredit lc) {
        LetterOfCreditPO po = toPO(lc);
        if (lc.getId() == null) {
            lcMapper.insert(po);
        } else {
            lcMapper.updateById(po);
        }
        return toDomain(po);
    }

    @Override
    public Optional<LetterOfCredit> findById(Long id) {
        LetterOfCreditPO po = lcMapper.selectById(id);
        return Optional.ofNullable(po).map(this::toDomain);
    }

    @Override
    public Optional<LetterOfCredit> findByLcNo(String lcNo) {
        LetterOfCreditPO po = lcMapper.selectByLcNo(lcNo);
        return Optional.ofNullable(po).map(this::toDomain);
    }

    @Override
    public List<LetterOfCredit> findByCustomerId(Long customerId) {
        List<LetterOfCreditPO> poList = lcMapper.selectList(
                new com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper<LetterOfCreditPO>()
                        .eq(LetterOfCreditPO::getCustomerId, customerId)
                        .orderByDesc(LetterOfCreditPO::getCreateTime));
        return poList.stream().map(this::toDomain).toList();
    }

    @Override
    public PageResp<LetterOfCredit> pageQuery(LcQuery query) {
        Page<LetterOfCreditPO> page = new Page<>(query.getPageNum(), query.getPageSize());
        lcMapper.pageQuery(page, query);
        List<LetterOfCredit> records = page.getRecords().stream().map(this::toDomain).toList();
        return PageResp.of(page.getTotal(), records, (int) page.getCurrent(), (int) page.getSize());
    }

    private LetterOfCredit toDomain(LetterOfCreditPO po) {
        return LetterOfCredit.reconstitute(
                po.getId(),
                po.getLcNo(),
                po.getCustomerId(),
                po.getLcType(),
                po.getLcDirection(),
                po.getLcAmount(),
                po.getLcCurrency(),
                po.getTolerancePct(),
                po.getApplicantInfo(),
                po.getBeneficiaryInfo(),
                po.getIssuingBankInfo(),
                po.getAdvisingBankInfo(),
                po.getConfirmingBankInfo(),
                po.getIssueDate(),
                po.getExpiryDate(),
                po.getExpiryPlace(),
                po.getLatestShipDate(),
                po.getPresentationPeriod(),
                po.getAvailableWith(),
                po.getAvailableBy(),
                po.getDraftTenor(),
                po.getPartialShipment(),
                po.getTransshipment(),
                po.getPortOfLoading(),
                po.getPortOfDischarge(),
                po.getGoodsDescription(),
                po.getDocumentsRequired(),
                po.getAdditionalConditions(),
                po.getConfirmationInstruction(),
                po.getChargeBearer(),
                po.getLcStatus(),
                po.getSwiftRef(),
                po.getMarginPct(),
                po.getMarginAmount(),
                po.getFeeAmount(),
                po.getOperatorId(),
                po.getRemark()
        );
    }

    private LetterOfCreditPO toPO(LetterOfCredit lc) {
        LetterOfCreditPO po = new LetterOfCreditPO();
        po.setId(lc.getId());
        po.setLcNo(lc.getLcNo());
        po.setCustomerId(lc.getCustomerId());
        po.setLcType(lc.getLcType());
        po.setLcDirection(lc.getLcDirection());
        po.setLcAmount(lc.getLcAmount());
        po.setLcCurrency(lc.getLcCurrency());
        po.setTolerancePct(lc.getTolerancePct());
        po.setApplicantInfo(lc.getApplicantInfo());
        po.setBeneficiaryInfo(lc.getBeneficiaryInfo());
        po.setIssuingBankInfo(lc.getIssuingBankInfo());
        po.setAdvisingBankInfo(lc.getAdvisingBankInfo());
        po.setConfirmingBankInfo(lc.getConfirmingBankInfo());
        po.setIssueDate(lc.getIssueDate());
        po.setExpiryDate(lc.getExpiryDate());
        po.setExpiryPlace(lc.getExpiryPlace());
        po.setLatestShipDate(lc.getLatestShipDate());
        po.setPresentationPeriod(lc.getPresentationPeriod());
        po.setAvailableWith(lc.getAvailableWith());
        po.setAvailableBy(lc.getAvailableBy());
        po.setDraftTenor(lc.getDraftTenor());
        po.setPartialShipment(lc.getPartialShipment());
        po.setTransshipment(lc.getTransshipment());
        po.setPortOfLoading(lc.getPortOfLoading());
        po.setPortOfDischarge(lc.getPortOfDischarge());
        po.setGoodsDescription(lc.getGoodsDescription());
        po.setDocumentsRequired(lc.getDocumentsRequired());
        po.setAdditionalConditions(lc.getAdditionalConditions());
        po.setConfirmationInstruction(lc.getConfirmationInstruction());
        po.setChargeBearer(lc.getChargeBearer());
        po.setLcStatus(lc.getLcStatus());
        po.setSwiftRef(lc.getSwiftRef());
        po.setMarginPct(lc.getMarginPct());
        po.setMarginAmount(lc.getMarginAmount());
        po.setFeeAmount(lc.getFeeAmount());
        po.setOperatorId(lc.getOperatorId());
        po.setRemark(lc.getRemark());
        po.setVersion(lc.getVersion());
        return po;
    }
}
