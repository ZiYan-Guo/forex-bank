package com.forex.supplychain.domain.model.aggregate;

import com.forex.common.base.domain.BaseAggregate;
import com.forex.common.base.exception.BusinessException;
import com.forex.common.base.result.ResultCode;
import lombok.Getter;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Getter
public class FactoringContract extends BaseAggregate {

    private Long id;
    private String contractNo;
    private Long sellerId;
    private Long buyerId;
    private String factoringType;
    private String recourse;
    private BigDecimal invoiceAmount;
    private String currency;
    private BigDecimal advanceRate;
    private BigDecimal advanceAmount;
    private BigDecimal factoringFee;
    private LocalDate invoiceDate;
    private LocalDate dueDate;
    private String goodsDescription;
    private String invoiceNo;
    private String contractStatus;
    private String riskLevel;
    private LocalDateTime createTime;

    private FactoringContract() {
        super();
    }

    public static FactoringContract create(Long sellerId, Long buyerId, String factoringType,
                                            String recourse, BigDecimal invoiceAmount, String currency,
                                            BigDecimal advanceRate, BigDecimal factoringFee,
                                            LocalDate invoiceDate, LocalDate dueDate,
                                            String goodsDescription, String invoiceNo) {
        FactoringContract contract = new FactoringContract();
        contract.sellerId = sellerId;
        contract.buyerId = buyerId;
        contract.factoringType = factoringType;
        contract.recourse = recourse;
        contract.invoiceAmount = invoiceAmount;
        contract.currency = currency;
        contract.advanceRate = advanceRate;
        contract.factoringFee = factoringFee;
        contract.invoiceDate = invoiceDate;
        contract.dueDate = dueDate;
        contract.goodsDescription = goodsDescription;
        contract.invoiceNo = invoiceNo;
        contract.calculateAdvance();
        contract.contractStatus = "DRAFT";
        contract.createTime = LocalDateTime.now();
        contract.validate();
        return contract;
    }

    public void calculateAdvance() {
        this.advanceAmount = this.invoiceAmount.multiply(this.advanceRate)
                .subtract(this.factoringFee);
    }

    public void submit() {
        if (!"DRAFT".equals(this.contractStatus)) {
            throw new BusinessException(ResultCode.VALIDATE_FAIL, "仅草稿状态可提交");
        }
        this.contractStatus = "PENDING_REVIEW";
        markUpdated();
    }

    public void approve() {
        if (!"PENDING_REVIEW".equals(this.contractStatus)) {
            throw new BusinessException(ResultCode.VALIDATE_FAIL, "仅待审核状态可审批");
        }
        this.contractStatus = "APPROVED";
        markUpdated();
    }

    public void reject(String reason) {
        if (!"PENDING_REVIEW".equals(this.contractStatus)) {
            throw new BusinessException(ResultCode.VALIDATE_FAIL, "仅待审核状态可驳回");
        }
        this.contractStatus = "REJECTED";
        markUpdated();
    }

    public void disburse() {
        if (!"APPROVED".equals(this.contractStatus)) {
            throw new BusinessException(ResultCode.VALIDATE_FAIL, "仅已审批状态可放款");
        }
        this.contractStatus = "DISBURSED";
        markUpdated();
    }

    public void repay(BigDecimal amount) {
        if (!"DISBURSED".equals(this.contractStatus)) {
            throw new BusinessException(ResultCode.VALIDATE_FAIL, "仅已放款状态可还款");
        }
        this.contractStatus = "SETTLED";
        markUpdated();
    }

    public void assignContractNo(String contractNo) {
        this.contractNo = contractNo;
    }

    public void evaluateRisk(String riskLevel) {
        this.riskLevel = riskLevel;
    }

    @Override
    protected void validate() {
        if (sellerId == null || buyerId == null) {
            throw new BusinessException(ResultCode.VALIDATE_FAIL, "买卖方不能为空");
        }
        if (invoiceAmount == null || invoiceAmount.compareTo(BigDecimal.ZERO) <= 0) {
            throw new BusinessException(ResultCode.VALIDATE_FAIL, "发票金额必须大于0");
        }
        if (advanceRate == null || advanceRate.compareTo(BigDecimal.ZERO) <= 0 || advanceRate.compareTo(BigDecimal.ONE) > 0) {
            throw new BusinessException(ResultCode.VALIDATE_FAIL, "预付比例需在0到1之间");
        }
    }
}
