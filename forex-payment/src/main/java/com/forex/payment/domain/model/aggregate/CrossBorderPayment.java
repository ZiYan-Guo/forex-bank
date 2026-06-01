package com.forex.payment.domain.model.aggregate;

import com.forex.common.base.domain.BaseAggregate;
import lombok.Getter;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

/**
 * Cross-border payment aggregate root. Tracks international wire transfers through the full
 * lifecycle: DRAFT → SUBMITTED → AML_CHECK → APPROVED → SENT → FUNDS_CREDITED.
 * 跨境支付聚合根，跟踪国际电汇支付指令的全生命周期。
 */
@Getter
public class CrossBorderPayment extends BaseAggregate {

    public static final String STATUS_DRAFT = "DRAFT";
    public static final String STATUS_SUBMITTED = "SUBMITTED";
    public static final String STATUS_AML_CHECK = "AML_CHECK";
    public static final String STATUS_AML_REJECTED = "AML_REJECTED";
    public static final String STATUS_APPROVED = "APPROVED";
    public static final String STATUS_SENT = "SENT";
    public static final String STATUS_FUNDS_CREDITED = "FUNDS_CREDITED";
    public static final String STATUS_CANCELLED = "CANCELLED";

    private static final long serialVersionUID = 1L;

    private Long id;
    /** Unique payment number. 支付唯一编号。 */
    private String paymentNo;
    private Long customerId;
    /** Payment direction: INWARD or OUTWARD. 支付方向：汇入/汇出。 */
    private String paymentDirection;
    /** Payment type: TT (telegraphic transfer), DD (demand draft), CIPS. 支付类型：电汇/票汇/CIPS。 */
    private String paymentType;
    /** Payment amount in payCurrency. 支付金额。 */
    private BigDecimal payAmount;
    private String payCurrency;
    private BigDecimal settlementAmount;
    private BigDecimal exchangeRate;
    private String senderInfo;
    /** Beneficiary information. 收款人信息。 */
    private String beneficiaryInfo;
    private String intermediaryBankInfo;
    private String payingBankCode;
    private String receivingBankCode;
    private String messageType;
    /** SWIFT message reference number. SWIFT报文参考号。 */
    private String swiftRef;
    /** CIPS payment reference number. CIPS支付参考号。 */
    private String cipsRef;
    /** SWIFT gpi tracking ID for payment traceability. SWIFT gpi追踪ID，用于支付追踪。 */
    private String gpiTrackingId;
    private String gpiStatus;
    private String paymentPurpose;
    private String bankPurposeCode;
    private String chargeBearer;
    private BigDecimal feeAmount;
    private BigDecimal telegraphicFee;
    private BigDecimal commissionAmount;
    /** Current payment status. 支付当前状态。 */
    private String paymentStatus;
    private LocalDateTime submitTime;
    private LocalDate valueDate;
    private LocalDate settlementDate;
    private Long operatorId;
    private Long approverId;
    private String remark;

    private CrossBorderPayment() {
        super();
    }

    /**
     * Create a new payment instruction. 创建支付指令。
     */
    public static CrossBorderPayment create(Long customerId, String paymentDirection,
                                             String paymentType, BigDecimal payAmount,
                                             String payCurrency, BigDecimal settlementAmount,
                                             BigDecimal exchangeRate, String senderInfo,
                                             String beneficiaryInfo, String intermediaryBankInfo,
                                             String payingBankCode, String receivingBankCode,
                                             String messageType, String gpiTrackingId,
                                             String paymentPurpose, String bankPurposeCode,
                                             String chargeBearer, LocalDate valueDate,
                                             Long operatorId, String remark) {
        CrossBorderPayment payment = new CrossBorderPayment();
        payment.customerId = customerId;
        payment.paymentDirection = paymentDirection;
        payment.paymentType = paymentType;
        payment.payAmount = payAmount;
        payment.payCurrency = payCurrency;
        payment.settlementAmount = settlementAmount;
        payment.exchangeRate = exchangeRate;
        payment.senderInfo = senderInfo;
        payment.beneficiaryInfo = beneficiaryInfo;
        payment.intermediaryBankInfo = intermediaryBankInfo;
        payment.payingBankCode = payingBankCode;
        payment.receivingBankCode = receivingBankCode;
        payment.messageType = messageType;
        payment.gpiTrackingId = gpiTrackingId;
        payment.paymentPurpose = paymentPurpose;
        payment.bankPurposeCode = bankPurposeCode;
        payment.chargeBearer = chargeBearer;
        payment.valueDate = valueDate;
        payment.operatorId = operatorId;
        payment.remark = remark;
        payment.paymentStatus = STATUS_DRAFT;
        payment.feeAmount = BigDecimal.ZERO;
        payment.telegraphicFee = BigDecimal.ZERO;
        payment.commissionAmount = BigDecimal.ZERO;
        payment.validate();
        return payment;
    }

    /**
     * Rebuild aggregate from persistence. 从持久化重建聚合。
     */
    public static CrossBorderPayment reconstitute(Long id, String paymentNo, Long customerId,
                                                    String paymentDirection, String paymentType,
                                                    BigDecimal payAmount, String payCurrency,
                                                    BigDecimal settlementAmount, BigDecimal exchangeRate,
                                                    String senderInfo, String beneficiaryInfo,
                                                    String intermediaryBankInfo, String payingBankCode,
                                                    String receivingBankCode, String messageType,
                                                    String swiftRef, String cipsRef, String gpiTrackingId,
                                                    String gpiStatus, String paymentPurpose,
                                                    String bankPurposeCode, String chargeBearer,
                                                    BigDecimal feeAmount, BigDecimal telegraphicFee,
                                                    BigDecimal commissionAmount, String paymentStatus,
                                                    LocalDateTime submitTime, LocalDate valueDate,
                                                    LocalDate settlementDate, Long operatorId,
                                                    Long approverId, String remark,
                                                    LocalDateTime createdAt, LocalDateTime updatedAt,
                                                    Integer version) {
        CrossBorderPayment payment = new CrossBorderPayment();
        payment.id = id;
        payment.paymentNo = paymentNo;
        payment.customerId = customerId;
        payment.paymentDirection = paymentDirection;
        payment.paymentType = paymentType;
        payment.payAmount = payAmount;
        payment.payCurrency = payCurrency;
        payment.settlementAmount = settlementAmount;
        payment.exchangeRate = exchangeRate;
        payment.senderInfo = senderInfo;
        payment.beneficiaryInfo = beneficiaryInfo;
        payment.intermediaryBankInfo = intermediaryBankInfo;
        payment.payingBankCode = payingBankCode;
        payment.receivingBankCode = receivingBankCode;
        payment.messageType = messageType;
        payment.swiftRef = swiftRef;
        payment.cipsRef = cipsRef;
        payment.gpiTrackingId = gpiTrackingId;
        payment.gpiStatus = gpiStatus;
        payment.paymentPurpose = paymentPurpose;
        payment.bankPurposeCode = bankPurposeCode;
        payment.chargeBearer = chargeBearer;
        payment.feeAmount = feeAmount;
        payment.telegraphicFee = telegraphicFee;
        payment.commissionAmount = commissionAmount;
        payment.paymentStatus = paymentStatus;
        payment.submitTime = submitTime;
        payment.valueDate = valueDate;
        payment.settlementDate = settlementDate;
        payment.operatorId = operatorId;
        payment.approverId = approverId;
        payment.remark = remark;
        return payment;
    }

    /**
     * Submit the draft payment for processing. 提交支付指令。
     */
    public void submit() {
        if (!STATUS_DRAFT.equals(this.paymentStatus)) {
            throw new IllegalStateException("只有草稿状态的支付才能提交");
        }
        this.paymentStatus = STATUS_SUBMITTED;
        this.submitTime = LocalDateTime.now();
        markUpdated();
    }

    /**
     * Approve the submitted payment and trigger AML check. 审批支付并触发反洗钱检查。
     */
    public void approve(Long approverId) {
        if (!STATUS_SUBMITTED.equals(this.paymentStatus)) {
            throw new IllegalStateException("只有已提交状态的支付才能审批");
        }
        this.paymentStatus = STATUS_AML_CHECK;
        this.approverId = approverId;
        markUpdated();
    }

    /**
     * Send the approved payment via the chosen channel. 发送支付指令。
     */
    public void send() {
        if (!STATUS_APPROVED.equals(this.paymentStatus) && !STATUS_AML_CHECK.equals(this.paymentStatus)) {
            throw new IllegalStateException("当前状态不允许发送");
        }
        this.paymentStatus = STATUS_SENT;
        this.settlementDate = LocalDate.now();
        markUpdated();
    }

    /**
     * Update the SWIFT reference number. 更新SWIFT参考号。
     */
    public void updateSwiftRef(String ref) {
        if (ref == null || ref.isBlank()) {
            throw new IllegalArgumentException("SWIFT参考号不能为空");
        }
        this.swiftRef = ref;
        markUpdated();
    }

    /**
     * Update the gpi tracking status. 更新gpi追踪状态。
     */
    public void updateGpiStatus(String status) {
        if (status == null || status.isBlank()) {
            throw new IllegalArgumentException("GPI状态不能为空");
        }
        this.gpiStatus = status;
        markUpdated();
    }

    /**
     * Mark the AML check as passed and transition to approved status. 反洗钱检查通过。
     */
    public void markAmlCheckPassed() {
        if (!STATUS_AML_CHECK.equals(this.paymentStatus)) {
            throw new IllegalStateException("当前状态不允许反洗钱检查");
        }
        this.paymentStatus = STATUS_APPROVED;
        markUpdated();
    }

    /**
     * Mark the AML check as rejected. 反洗钱检查拒绝。
     */
    public void markAmlCheckRejected() {
        if (!STATUS_AML_CHECK.equals(this.paymentStatus)) {
            throw new IllegalStateException("当前状态不允许反洗钱检查");
        }
        this.paymentStatus = STATUS_AML_REJECTED;
        markUpdated();
    }

    /**
     * Mark funds as credited to the beneficiary account. 标记资金已到账。
     */
    public void markFundsCredited() {
        if (!STATUS_SENT.equals(this.paymentStatus)) {
            throw new IllegalStateException("只有已发送状态的支付才能标记资金到账");
        }
        this.paymentStatus = STATUS_FUNDS_CREDITED;
        markUpdated();
    }

    /**
     * Cancel the payment with the given reason. 取消支付指令。
     */
    public void cancel(String reason) {
        if (STATUS_CANCELLED.equals(this.paymentStatus)) {
            throw new IllegalStateException("支付已取消");
        }
        if (STATUS_FUNDS_CREDITED.equals(this.paymentStatus)) {
            throw new IllegalStateException("已到账的支付不能取消");
        }
        this.paymentStatus = STATUS_CANCELLED;
        this.remark = reason;
        markUpdated();
    }

    public void setPaymentNo(String paymentNo) {
        if (paymentNo == null || paymentNo.isBlank()) {
            throw new IllegalArgumentException("支付编号不能为空");
        }
        this.paymentNo = paymentNo;
    }

    public void setCipsRef(String cipsRef) {
        this.cipsRef = cipsRef;
        markUpdated();
    }

    @Override
    protected void validate() {
        if (customerId == null) {
            throw new IllegalArgumentException("客户ID不能为空");
        }
        if (paymentDirection == null || paymentDirection.isBlank()) {
            throw new IllegalArgumentException("支付方向不能为空");
        }
        if (payAmount == null || payAmount.compareTo(BigDecimal.ZERO) <= 0) {
            throw new IllegalArgumentException("支付金额必须大于0");
        }
        if (payCurrency == null || payCurrency.isBlank()) {
            throw new IllegalArgumentException("支付币种不能为空");
        }
    }
}
