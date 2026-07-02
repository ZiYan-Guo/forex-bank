package com.forex.supplychain.domain.model.aggregate;

import com.forex.common.base.domain.BaseAggregate;
import com.forex.common.base.exception.BusinessException;
import com.forex.common.base.result.ResultCode;
import lombok.Getter;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Getter
public class ForfaitingContract extends BaseAggregate {

    private Long id;
    private String contractNo;
    private Long exporterId;
    private Long importerId;
    private String forfaitingType;
    private BigDecimal faceAmount;
    private String currency;
    private BigDecimal discountRate;
    private BigDecimal discountAmount;
    private BigDecimal netAmount;
    private LocalDate shipmentDate;
    private LocalDate maturityDate;
    private String goodsDescription;
    private String issuingBank;
    private String contractStatus;
    private LocalDateTime createTime;

    private ForfaitingContract() {
        super();
    }

    public static ForfaitingContract create(Long exporterId, Long importerId,
                                             String forfaitingType, BigDecimal faceAmount,
                                             String currency, BigDecimal discountRate,
                                             LocalDate shipmentDate, LocalDate maturityDate,
                                             String goodsDescription, String issuingBank) {
        ForfaitingContract contract = new ForfaitingContract();
        contract.exporterId = exporterId;
        contract.importerId = importerId;
        contract.forfaitingType = forfaitingType;
        contract.faceAmount = faceAmount;
        contract.currency = currency;
        contract.discountRate = discountRate;
        contract.shipmentDate = shipmentDate;
        contract.maturityDate = maturityDate;
        contract.goodsDescription = goodsDescription;
        contract.issuingBank = issuingBank;
        contract.calculateDiscount();
        contract.contractStatus = "DRAFT";
        contract.createTime = LocalDateTime.now();
        contract.validate();
        return contract;
    }

    public void calculateDiscount() {
        long days = java.time.temporal.ChronoUnit.DAYS.between(LocalDate.now(), this.maturityDate);
        BigDecimal dailyRate = this.discountRate.divide(new BigDecimal("360"), 10, BigDecimal.ROUND_HALF_UP);
        this.discountAmount = this.faceAmount.multiply(dailyRate).multiply(new BigDecimal(days));
        this.netAmount = this.faceAmount.subtract(this.discountAmount);
    }

    public void submit() {
        this.contractStatus = "PENDING_REVIEW";
        markUpdated();
    }

    public void approve() {
        this.contractStatus = "APPROVED";
        markUpdated();
    }

    public void discount() {
        if (!"APPROVED".equals(this.contractStatus)) {
            throw new BusinessException(ResultCode.VALIDATE_FAIL, "仅已审批状态可贴现");
        }
        this.contractStatus = "DISCOUNTED";
        markUpdated();
    }

    public void assignContractNo(String contractNo) {
        this.contractNo = contractNo;
    }

    @Override
    protected void validate() {
        if (exporterId == null) {
            throw new BusinessException(ResultCode.VALIDATE_FAIL, "出口商不能为空");
        }
        if (faceAmount == null || faceAmount.compareTo(BigDecimal.ZERO) <= 0) {
            throw new BusinessException(ResultCode.VALIDATE_FAIL, "票面金额必须大于0");
        }
    }
}
