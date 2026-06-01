package com.forex.settlement.domain.model.aggregate;

import com.forex.common.base.domain.BaseAggregate;
import com.forex.common.base.exception.BusinessException;
import lombok.Getter;

import java.math.BigDecimal;
import java.time.LocalDate;

/**
 * Letter of Credit aggregate root. Manages the full lifecycle from issuance to payment and
 * discharge: DRAFT → ISSUED → ADVISED → DOC_PRESENTED → DOC_CHECKED → ACCEPTED → PAID → DISCHARGED.
 * 信用证聚合根，管理从开立到付款结清的全生命周期。
 */
@Getter
public class LetterOfCredit extends BaseAggregate {

    public static final String STATUS_DRAFT = "DRAFT";
    public static final String STATUS_ISSUED = "ISSUED";
    public static final String STATUS_ADVISED = "ADVISED";
    public static final String STATUS_DOC_PRESENTED = "DOC_PRESENTED";
    public static final String STATUS_DOC_CHECKED = "DOC_CHECKED";
    public static final String STATUS_ACCEPTED = "ACCEPTED";
    public static final String STATUS_PAID = "PAID";
    public static final String STATUS_DISCHARGED = "DISCHARGED";
    public static final String STATUS_CANCELLED = "CANCELLED";

    private static final long serialVersionUID = 1L;

    private Long id;
    /** Unique LC number. 信用证唯一编号。 */
    private String lcNo;
    private Long customerId;
    /** LC type (e.g., irrevocable, revocable). 信用证类型。 */
    private String lcType;
    private String lcDirection;
    /** LC amount in lcCurrency. 信用证金额。 */
    private BigDecimal lcAmount;
    private String lcCurrency;
    private BigDecimal tolerancePct;
    /** Applicant's information. 申请人信息。 */
    private String applicantInfo;
    /** Beneficiary's information. 受益人信息。 */
    private String beneficiaryInfo;
    private String issuingBankInfo;
    private String advisingBankInfo;
    private String confirmingBankInfo;
    private LocalDate issueDate;
    /** LC expiry date. 信用证效期。 */
    private LocalDate expiryDate;
    private String expiryPlace;
    private LocalDate latestShipDate;
    private Integer presentationPeriod;
    private String availableWith;
    /** How the LC is available (e.g., by payment, by acceptance). 可用方式。 */
    private String availableBy;
    private String draftTenor;
    private String partialShipment;
    private String transshipment;
    private String portOfLoading;
    private String portOfDischarge;
    private String goodsDescription;
    private String documentsRequired;
    private String additionalConditions;
    private String confirmationInstruction;
    private String chargeBearer;
    /** Current LC status. 信用证当前状态。 */
    private String lcStatus;
    private String swiftRef;
    private BigDecimal marginPct;
    private BigDecimal marginAmount;
    private BigDecimal feeAmount;
    private Long operatorId;
    private String remark;

    private LetterOfCredit() {
        super();
    }

    /**
     * Create a new LC draft. 创建信用证草稿。
     */
    public static LetterOfCredit create(Long customerId, String lcType, String lcDirection,
                                         BigDecimal lcAmount, String lcCurrency,
                                         String applicantInfo, String beneficiaryInfo,
                                         String issuingBankInfo, LocalDate expiryDate,
                                         String expiryPlace, String availableWith,
                                         String availableBy, Long operatorId, String remark) {
        LetterOfCredit lc = new LetterOfCredit();
        lc.customerId = customerId;
        lc.lcType = lcType;
        lc.lcDirection = lcDirection;
        lc.lcAmount = lcAmount;
        lc.lcCurrency = lcCurrency;
        lc.applicantInfo = applicantInfo;
        lc.beneficiaryInfo = beneficiaryInfo;
        lc.issuingBankInfo = issuingBankInfo;
        lc.expiryDate = expiryDate;
        lc.expiryPlace = expiryPlace;
        lc.availableWith = availableWith;
        lc.availableBy = availableBy;
        lc.operatorId = operatorId;
        lc.remark = remark;
        lc.lcStatus = STATUS_DRAFT;
        lc.tolerancePct = BigDecimal.ZERO;
        lc.marginPct = BigDecimal.ZERO;
        lc.marginAmount = BigDecimal.ZERO;
        lc.feeAmount = BigDecimal.ZERO;
        lc.validate();
        return lc;
    }

    /**
     * Rebuild aggregate from persistence. 从持久化重建聚合。
     */
    public static LetterOfCredit reconstitute(Long id, String lcNo, Long customerId,
                                               String lcType, String lcDirection,
                                               BigDecimal lcAmount, String lcCurrency,
                                               BigDecimal tolerancePct, String applicantInfo,
                                               String beneficiaryInfo, String issuingBankInfo,
                                               String advisingBankInfo, String confirmingBankInfo,
                                               LocalDate issueDate, LocalDate expiryDate,
                                               String expiryPlace, LocalDate latestShipDate,
                                               Integer presentationPeriod, String availableWith,
                                               String availableBy, String draftTenor,
                                               String partialShipment, String transshipment,
                                               String portOfLoading, String portOfDischarge,
                                               String goodsDescription, String documentsRequired,
                                               String additionalConditions, String confirmationInstruction,
                                               String chargeBearer, String lcStatus, String swiftRef,
                                               BigDecimal marginPct, BigDecimal marginAmount,
                                               BigDecimal feeAmount, Long operatorId, String remark) {
        LetterOfCredit lc = new LetterOfCredit();
        lc.id = id;
        lc.lcNo = lcNo;
        lc.customerId = customerId;
        lc.lcType = lcType;
        lc.lcDirection = lcDirection;
        lc.lcAmount = lcAmount;
        lc.lcCurrency = lcCurrency;
        lc.tolerancePct = tolerancePct;
        lc.applicantInfo = applicantInfo;
        lc.beneficiaryInfo = beneficiaryInfo;
        lc.issuingBankInfo = issuingBankInfo;
        lc.advisingBankInfo = advisingBankInfo;
        lc.confirmingBankInfo = confirmingBankInfo;
        lc.issueDate = issueDate;
        lc.expiryDate = expiryDate;
        lc.expiryPlace = expiryPlace;
        lc.latestShipDate = latestShipDate;
        lc.presentationPeriod = presentationPeriod;
        lc.availableWith = availableWith;
        lc.availableBy = availableBy;
        lc.draftTenor = draftTenor;
        lc.partialShipment = partialShipment;
        lc.transshipment = transshipment;
        lc.portOfLoading = portOfLoading;
        lc.portOfDischarge = portOfDischarge;
        lc.goodsDescription = goodsDescription;
        lc.documentsRequired = documentsRequired;
        lc.additionalConditions = additionalConditions;
        lc.confirmationInstruction = confirmationInstruction;
        lc.chargeBearer = chargeBearer;
        lc.lcStatus = lcStatus;
        lc.swiftRef = swiftRef;
        lc.marginPct = marginPct;
        lc.marginAmount = marginAmount;
        lc.feeAmount = feeAmount;
        lc.operatorId = operatorId;
        lc.remark = remark;
        return lc;
    }

    /**
     * Issue the LC draft. 开立信用证。
     */
    public void issue() {
        if (!STATUS_DRAFT.equals(this.lcStatus)) {
            throw new BusinessException("只有草稿状态的信用证才能开立");
        }
        this.lcStatus = STATUS_ISSUED;
        this.issueDate = LocalDate.now();
        markUpdated();
    }

    /**
     * Advise the LC to the beneficiary. 通知信用证至受益人。
     */
    public void advise() {
        if (!STATUS_ISSUED.equals(this.lcStatus)) {
            throw new BusinessException("只有已开立的信用证才能通知");
        }
        this.lcStatus = STATUS_ADVISED;
        markUpdated();
    }

    /**
     * Amend the LC amount, expiry date, or goods description. 修改信用证。
     */
    public void amend(BigDecimal newAmount, LocalDate newExpiryDate, String newGoodsDescription) {
        if (STATUS_CANCELLED.equals(this.lcStatus) || STATUS_PAID.equals(this.lcStatus)
                || STATUS_DISCHARGED.equals(this.lcStatus)) {
            throw new BusinessException("当前状态不允许修改信用证");
        }
        if (newAmount != null && newAmount.compareTo(BigDecimal.ZERO) > 0) {
            this.lcAmount = newAmount;
        }
        if (newExpiryDate != null) {
            this.expiryDate = newExpiryDate;
        }
        if (newGoodsDescription != null) {
            this.goodsDescription = newGoodsDescription;
        }
        markUpdated();
    }

    /**
     * Present documents under the LC. 交单。
     */
    public void presentDocuments() {
        if (!STATUS_ADVISED.equals(this.lcStatus)) {
            throw new BusinessException("只有已通知的信用证才能交单");
        }
        this.lcStatus = STATUS_DOC_PRESENTED;
        markUpdated();
    }

    /**
     * Check the presented documents for compliance. 审单。
     */
    public void checkDocuments(boolean discrepant) {
        if (!STATUS_DOC_PRESENTED.equals(this.lcStatus)) {
            throw new BusinessException("只有交单状态的信用证才能审单");
        }
        this.lcStatus = STATUS_DOC_CHECKED;
        if (discrepant) {
            this.remark = (this.remark == null ? "" : this.remark + "; ") + "单据存在不符点";
        }
        markUpdated();
    }

    /**
     * Accept the documents and the payment obligation. 承兑。
     */
    public void accept() {
        if (!STATUS_DOC_CHECKED.equals(this.lcStatus)) {
            throw new BusinessException("只有审单完成的信用证才能承兑");
        }
        this.lcStatus = STATUS_ACCEPTED;
        markUpdated();
    }

    /**
     * Pay the LC. 付款。
     */
    public void pay() {
        if (!STATUS_ACCEPTED.equals(this.lcStatus)) {
            throw new BusinessException("只有已承兑的信用证才能付款");
        }
        this.lcStatus = STATUS_PAID;
        markUpdated();
    }

    /**
     * Discharge the LC after payment. 结清信用证。
     */
    public void discharge() {
        if (!STATUS_PAID.equals(this.lcStatus)) {
            throw new BusinessException("只有已付款的信用证才能结清");
        }
        this.lcStatus = STATUS_DISCHARGED;
        markUpdated();
    }

    /**
     * Cancel the LC with the given reason. 取消信用证。
     */
    public void cancel(String reason) {
        if (STATUS_CANCELLED.equals(this.lcStatus)) {
            throw new BusinessException("信用证已取消");
        }
        if (STATUS_PAID.equals(this.lcStatus) || STATUS_DISCHARGED.equals(this.lcStatus)) {
            throw new BusinessException("已付款或已结清的信用证不能取消");
        }
        this.lcStatus = STATUS_CANCELLED;
        this.remark = reason;
        markUpdated();
    }

    public void setLcNo(String lcNo) {
        this.lcNo = lcNo;
    }

    @Override
    protected void validate() {
        if (lcAmount == null || lcAmount.compareTo(BigDecimal.ZERO) <= 0) {
            throw new BusinessException("信用证金额必须大于0");
        }
        if (customerId == null) {
            throw new BusinessException("客户ID不能为空");
        }
    }
}
