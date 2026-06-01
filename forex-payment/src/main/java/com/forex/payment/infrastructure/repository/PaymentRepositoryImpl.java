package com.forex.payment.infrastructure.repository;

import com.forex.common.base.dto.PageResp;
import com.forex.payment.domain.model.aggregate.CrossBorderPayment;
import com.forex.payment.domain.model.dto.PaymentQuery;
import com.forex.payment.domain.repository.PaymentRepository;
import com.forex.payment.infrastructure.mapper.CrossBorderPaymentMapper;
import com.forex.payment.infrastructure.persistence.CrossBorderPaymentPO;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;

import lombok.RequiredArgsConstructor;

import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
@RequiredArgsConstructor
public class PaymentRepositoryImpl implements PaymentRepository {

    private final CrossBorderPaymentMapper crossBorderPaymentMapper;

    @Override
    public CrossBorderPayment save(CrossBorderPayment payment) {
        CrossBorderPaymentPO po = toPO(payment);
        if (payment.getId() == null) {
            crossBorderPaymentMapper.insert(po);
        } else {
            crossBorderPaymentMapper.updateById(po);
        }
        return toDomain(po);
    }

    @Override
    public Optional<CrossBorderPayment> findById(Long id) {
        CrossBorderPaymentPO po = crossBorderPaymentMapper.selectById(id);
        return Optional.ofNullable(po).map(this::toDomain);
    }

    @Override
    public Optional<CrossBorderPayment> findByPaymentNo(String paymentNo) {
        CrossBorderPaymentPO po = crossBorderPaymentMapper.selectByPaymentNo(paymentNo);
        return Optional.ofNullable(po).map(this::toDomain);
    }

    @Override
    public List<CrossBorderPayment> findByCustomerId(Long customerId) {
        List<CrossBorderPaymentPO> poList = crossBorderPaymentMapper.selectByCustomerId(customerId);
        return poList.stream().map(this::toDomain).toList();
    }

    @Override
    public PageResp<CrossBorderPayment> pageQuery(PaymentQuery query) {
        LambdaQueryWrapper<CrossBorderPaymentPO> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(query.getPaymentNo() != null, CrossBorderPaymentPO::getPaymentNo, query.getPaymentNo());
        wrapper.eq(query.getCustomerId() != null, CrossBorderPaymentPO::getCustomerId, query.getCustomerId());
        wrapper.eq(query.getPaymentDirection() != null, CrossBorderPaymentPO::getPaymentDirection, query.getPaymentDirection());
        wrapper.eq(query.getPaymentType() != null, CrossBorderPaymentPO::getPaymentType, query.getPaymentType());
        wrapper.eq(query.getPaymentStatus() != null, CrossBorderPaymentPO::getPaymentStatus, query.getPaymentStatus());
        wrapper.ge(query.getStartDate() != null, CrossBorderPaymentPO::getCreateTime, query.getStartDate());
        wrapper.le(query.getEndDate() != null, CrossBorderPaymentPO::getCreateTime, query.getEndDate());
        wrapper.orderByDesc(CrossBorderPaymentPO::getCreateTime);

        Page<CrossBorderPaymentPO> page = crossBorderPaymentMapper.selectPage(
                new Page<>(query.getPageNum(), query.getPageSize()), wrapper);

        List<CrossBorderPayment> records = page.getRecords().stream().map(this::toDomain).toList();
        return PageResp.of(page.getTotal(), records, (int) page.getCurrent(), (int) page.getSize());
    }

    private CrossBorderPayment toDomain(CrossBorderPaymentPO po) {
        return CrossBorderPayment.reconstitute(
                po.getId(),
                po.getPaymentNo(),
                po.getCustomerId(),
                po.getPaymentDirection(),
                po.getPaymentType(),
                po.getPayAmount(),
                po.getPayCurrency(),
                po.getSettlementAmount(),
                po.getExchangeRate(),
                po.getSenderInfo(),
                po.getBeneficiaryInfo(),
                po.getIntermediaryBankInfo(),
                po.getPayingBankCode(),
                po.getReceivingBankCode(),
                po.getMessageType(),
                po.getSwiftRef(),
                po.getCipsRef(),
                po.getGpiTrackingId(),
                po.getGpiStatus(),
                po.getPaymentPurpose(),
                po.getBankPurposeCode(),
                po.getChargeBearer(),
                po.getFeeAmount(),
                po.getTelegraphicFee(),
                po.getCommissionAmount(),
                po.getPaymentStatus(),
                po.getSubmitTime(),
                po.getValueDate(),
                po.getSettlementDate(),
                po.getOperatorId(),
                po.getApproverId(),
                po.getRemark(),
                po.getCreateTime(),
                po.getUpdateTime(),
                po.getVersion()
        );
    }

    private CrossBorderPaymentPO toPO(CrossBorderPayment payment) {
        CrossBorderPaymentPO po = new CrossBorderPaymentPO();
        po.setId(payment.getId());
        po.setPaymentNo(payment.getPaymentNo());
        po.setCustomerId(payment.getCustomerId());
        po.setPaymentDirection(payment.getPaymentDirection());
        po.setPaymentType(payment.getPaymentType());
        po.setPayAmount(payment.getPayAmount());
        po.setPayCurrency(payment.getPayCurrency());
        po.setSettlementAmount(payment.getSettlementAmount());
        po.setExchangeRate(payment.getExchangeRate());
        po.setSenderInfo(payment.getSenderInfo());
        po.setBeneficiaryInfo(payment.getBeneficiaryInfo());
        po.setIntermediaryBankInfo(payment.getIntermediaryBankInfo());
        po.setPayingBankCode(payment.getPayingBankCode());
        po.setReceivingBankCode(payment.getReceivingBankCode());
        po.setMessageType(payment.getMessageType());
        po.setSwiftRef(payment.getSwiftRef());
        po.setCipsRef(payment.getCipsRef());
        po.setGpiTrackingId(payment.getGpiTrackingId());
        po.setGpiStatus(payment.getGpiStatus());
        po.setPaymentPurpose(payment.getPaymentPurpose());
        po.setBankPurposeCode(payment.getBankPurposeCode());
        po.setChargeBearer(payment.getChargeBearer());
        po.setFeeAmount(payment.getFeeAmount());
        po.setTelegraphicFee(payment.getTelegraphicFee());
        po.setCommissionAmount(payment.getCommissionAmount());
        po.setPaymentStatus(payment.getPaymentStatus());
        po.setSubmitTime(payment.getSubmitTime());
        po.setValueDate(payment.getValueDate());
        po.setSettlementDate(payment.getSettlementDate());
        po.setOperatorId(payment.getOperatorId());
        po.setApproverId(payment.getApproverId());
        po.setRemark(payment.getRemark());
        po.setVersion(payment.getVersion());
        return po;
    }
}
