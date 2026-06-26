package com.forex.cashpool.domain.model.aggregate;

import com.forex.common.base.domain.BaseAggregate;

import lombok.Getter;
import lombok.extern.slf4j.Slf4j;

import java.math.BigDecimal;
import java.time.LocalDate;
import com.forex.common.base.exception.BusinessException;
import com.forex.common.base.result.ResultCode;

/**
 * 境外放款聚合根 - 管理境外放款合同生命周期与还款处理
 * Overseas Lending Aggregate Root - Manages overseas lending contract lifecycle and repayment
 */
@Slf4j
@Getter
public class OverseasLending extends BaseAggregate {

    private Long id;
    private String contractNo;
    private Long customerId;
    private BigDecimal loanAmount;
    private String loanCurrency;
    private BigDecimal interestRate;
    private LocalDate startDate;
    private LocalDate endDate;
    private String repaymentMethod;
    private String loanStatus;
    private BigDecimal outstandingPrincipal;
    private BigDecimal totalInterest;
    private Long poolId;

    private OverseasLending() {
        super();
    }

    /**
     * 创建境外放款合同 - 初始化合同信息，状态置为草稿
     * Create overseas lending contract - Initialize contract info, set status to DRAFT
     */
    public static OverseasLending create(String contractNo, Long customerId, BigDecimal loanAmount,
                                          String loanCurrency, BigDecimal interestRate,
                                          LocalDate startDate, LocalDate endDate,
                                          String repaymentMethod, Long poolId) {
        OverseasLending lending = new OverseasLending();
        lending.contractNo = contractNo;
        lending.customerId = customerId;
        lending.loanAmount = loanAmount;
        lending.loanCurrency = loanCurrency;
        lending.interestRate = interestRate;
        lending.startDate = startDate;
        lending.endDate = endDate;
        lending.repaymentMethod = repaymentMethod;
        lending.loanStatus = "DRAFT";
        lending.outstandingPrincipal = loanAmount;
        lending.totalInterest = BigDecimal.ZERO;
        lending.poolId = poolId;
        lending.validate();
        log.info("境外放款合同创建, contractNo: {}, customerId: {}, loanAmount: {}, loanCurrency: {}",
                contractNo, customerId, loanAmount, loanCurrency);
        return lending;
    }

    /**
     * 从持久化数据重建境外放款聚合根
     * Reconstitute OverseasLending aggregate root from persisted data
     */
    public static OverseasLending reconstitute(Long id, String contractNo, Long customerId,
                                                BigDecimal loanAmount, String loanCurrency,
                                                BigDecimal interestRate, LocalDate startDate,
                                                LocalDate endDate, String repaymentMethod,
                                                String loanStatus, BigDecimal outstandingPrincipal,
                                                BigDecimal totalInterest, Long poolId) {
        OverseasLending lending = new OverseasLending();
        lending.id = id;
        lending.contractNo = contractNo;
        lending.customerId = customerId;
        lending.loanAmount = loanAmount;
        lending.loanCurrency = loanCurrency;
        lending.interestRate = interestRate;
        lending.startDate = startDate;
        lending.endDate = endDate;
        lending.repaymentMethod = repaymentMethod;
        lending.loanStatus = loanStatus;
        lending.outstandingPrincipal = outstandingPrincipal;
        lending.totalInterest = totalInterest;
        lending.poolId = poolId;
        return lending;
    }

    /**
     * 审批通过放款合同 - 状态由SUBMITTED变更至APPROVED
     * Approve the lending contract - Change status from SUBMITTED to APPROVED
     */
    public void approve() {
        if (!"SUBMITTED".equals(this.loanStatus)) {
            throw new IllegalStateException("当前合同状态为 " + this.loanStatus + "，无法审批，仅SUBMITTED状态可审批");
        }
        this.loanStatus = "APPROVED";
        markUpdated();
        log.info("境外放款合同审批通过, contractNo: {}", contractNo);
    }

    /**
     * 激活放款合同 - 状态变更至ACTIVE，正式生效
     * Activate the lending contract - Change status to ACTIVE, officially effective
     */
    public void activate() {
        if (!"APPROVED".equals(this.loanStatus)) {
            throw new IllegalStateException("当前合同状态为 " + this.loanStatus + "，无法激活，仅APPROVED状态可激活");
        }
        this.loanStatus = "ACTIVE";
        markUpdated();
        log.info("境外放款合同已激活, contractNo: {}, outstandingPrincipal: {}", contractNo, outstandingPrincipal);
    }

    /**
     * 记录还款 - 冲减未偿还本金，至零则标记已结清
     * Record repayment - Reduce outstanding principal, mark REPAID if fully repaid
     */
    public void recordRepayment(BigDecimal amount) {
        if (amount == null || amount.compareTo(BigDecimal.ZERO) <= 0) {
            throw new BusinessException(ResultCode.VALIDATE_FAIL, "还款金额必须大于0");
        }
        if (!"ACTIVE".equals(this.loanStatus) && !"OVERDUE".equals(this.loanStatus)) {
            throw new IllegalStateException("当前合同状态为 " + this.loanStatus + "，无法进行还款");
        }
        if (amount.compareTo(outstandingPrincipal) > 0) {
            throw new BusinessException(ResultCode.VALIDATE_FAIL, "还款金额超过未偿还本金，还款金额: " + amount
                    + "，未偿还本金: " + outstandingPrincipal);
        }
        this.outstandingPrincipal = this.outstandingPrincipal.subtract(amount);
        if (this.outstandingPrincipal.compareTo(BigDecimal.ZERO) == 0) {
            this.loanStatus = "REPAID";
            log.info("境外放款合同已结清, contractNo: {}", contractNo);
        }
        markUpdated();
        log.info("境外放款还款记录, contractNo: {}, 还款金额: {}, 剩余本金: {}",
                contractNo, amount, outstandingPrincipal);
    }

    /**
     * 标记逾期 - 将合同状态置为OVERDUE
     * Mark as overdue - Set contract status to OVERDUE
     */
    public void markOverdue() {
        if (!"ACTIVE".equals(this.loanStatus)) {
            throw new IllegalStateException("当前合同状态为 " + this.loanStatus + "，无法标记逾期");
        }
        this.loanStatus = "OVERDUE";
        markUpdated();
        log.info("境外放款合同已逾期, contractNo: {}, outstandingPrincipal: {}", contractNo, outstandingPrincipal);
    }

    /**
     * 取消放款合同 - 仅草稿、已提交、已审批状态可取消
     * Cancel lending contract - Only DRAFT, SUBMITTED, APPROVED statuses can be cancelled
     */
    public void cancel() {
        if ("ACTIVE".equals(this.loanStatus) || "REPAID".equals(this.loanStatus)
                || "OVERDUE".equals(this.loanStatus)) {
            throw new IllegalStateException("当前合同状态为 " + this.loanStatus + "，无法取消");
        }
        this.loanStatus = "CANCELLED";
        markUpdated();
        log.info("境外放款合同已取消, contractNo: {}", contractNo);
    }

    /**
     * 验证放款合同关键字段 - 确保必填字段不为空、金额为正数
     * Validate lending contract mandatory fields - Ensure required fields not null, amounts positive
     */
    @Override
    protected void validate() {
        if (contractNo == null || contractNo.isBlank()) {
            throw new BusinessException(ResultCode.VALIDATE_FAIL, "合同编号不能为空");
        }
        if (customerId == null) {
            throw new BusinessException(ResultCode.VALIDATE_FAIL, "客户ID不能为空");
        }
        if (loanAmount == null || loanAmount.compareTo(BigDecimal.ZERO) <= 0) {
            throw new BusinessException(ResultCode.VALIDATE_FAIL, "放款金额必须大于0");
        }
        if (loanCurrency == null || loanCurrency.isBlank()) {
            throw new BusinessException(ResultCode.VALIDATE_FAIL, "放款币种不能为空");
        }
        if (interestRate == null || interestRate.compareTo(BigDecimal.ZERO) < 0) {
            throw new BusinessException(ResultCode.VALIDATE_FAIL, "利率不能为负数");
        }
        if (startDate == null) {
            throw new BusinessException(ResultCode.VALIDATE_FAIL, "放款日期不能为空");
        }
        if (endDate == null) {
            throw new BusinessException(ResultCode.VALIDATE_FAIL, "到期日期不能为空");
        }
        if (endDate.isBefore(startDate)) {
            throw new BusinessException(ResultCode.VALIDATE_FAIL, "到期日期不能早于放款日期");
        }
        if (repaymentMethod == null || repaymentMethod.isBlank()) {
            throw new BusinessException(ResultCode.VALIDATE_FAIL, "还款方式不能为空");
        }
    }
}
