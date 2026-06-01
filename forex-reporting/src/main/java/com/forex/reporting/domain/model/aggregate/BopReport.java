package com.forex.reporting.domain.model.aggregate;

import com.forex.common.base.domain.BaseAggregate;
import com.forex.common.base.exception.BusinessException;
import lombok.Getter;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

/**
 * Balance of Payment report aggregate. Manages regulatory BOP reporting lifecycle:
 * DRAFT → READY → SUBMITTED → ACCEPTED (or REJECTED → DRAFT for correction).
 * 国际收支申报聚合根，管理监管国际收支申报的全生命周期。
 */
@Getter
public class BopReport extends BaseAggregate {

    private static final String STATUS_DRAFT = "DRAFT";
    private static final String STATUS_READY = "READY";
    private static final String STATUS_SUBMITTED = "SUBMITTED";
    private static final String STATUS_ACCEPTED = "ACCEPTED";
    private static final String STATUS_REJECTED = "REJECTED";

    private Long id;
    /** Unique report number. 申报唯一编号。 */
    private String reportNo;
    /** Report type classification. 申报类型。 */
    private String reportType;
    private Long customerId;
    private String customerName;
    private String transactionNo;
    private String transactionType;
    /** Transaction amount in original currency. 交易金额（原币）。 */
    private BigDecimal transactionAmount;
    private String transactionCurrency;
    private BigDecimal cnyAmount;
    private BigDecimal exchangeRate;
    private LocalDate transactionDate;
    private LocalDate settlementDate;
    /** Balance of Payment classification code. 国际收支编码。 */
    private String bopCode;
    private String bopName;
    private String purposeCode;
    private String purposeRemark;
    private String counterpartyCountry;
    private String counterpartyName;
    /** Current report status. 申报当前状态。 */
    private String reportStatus;
    private LocalDateTime submitTime;
    /** Regulatory authority reference number. 监管机构参考号。 */
    private String regulatoryRef;
    private String errorMsg;

    private BopReport() {
        super();
    }

    /**
     * Create a new BOP report. 创建国际收支申报。
     */
    public static BopReport create(String reportNo, String reportType, Long customerId,
                                    String customerName, String transactionNo,
                                    String transactionType, BigDecimal transactionAmount,
                                    String transactionCurrency, BigDecimal cnyAmount,
                                    BigDecimal exchangeRate, LocalDate transactionDate,
                                    LocalDate settlementDate, String bopCode, String bopName,
                                    String purposeCode, String purposeRemark,
                                    String counterpartyCountry, String counterpartyName) {
        BopReport report = new BopReport();
        report.reportNo = reportNo;
        report.reportType = reportType;
        report.customerId = customerId;
        report.customerName = customerName;
        report.transactionNo = transactionNo;
        report.transactionType = transactionType;
        report.transactionAmount = transactionAmount;
        report.transactionCurrency = transactionCurrency;
        report.cnyAmount = cnyAmount;
        report.exchangeRate = exchangeRate;
        report.transactionDate = transactionDate;
        report.settlementDate = settlementDate;
        report.bopCode = bopCode;
        report.bopName = bopName;
        report.purposeCode = purposeCode;
        report.purposeRemark = purposeRemark;
        report.counterpartyCountry = counterpartyCountry;
        report.counterpartyName = counterpartyName;
        report.reportStatus = STATUS_DRAFT;
        report.validate();
        return report;
    }

    /**
     * Rebuild aggregate from persistence. 从持久化重建聚合。
     */
    public static BopReport reconstitute(Long id, String reportNo, String reportType,
                                          Long customerId, String customerName,
                                          String transactionNo, String transactionType,
                                          BigDecimal transactionAmount, String transactionCurrency,
                                          BigDecimal cnyAmount, BigDecimal exchangeRate,
                                          LocalDate transactionDate, LocalDate settlementDate,
                                          String bopCode, String bopName, String purposeCode,
                                          String purposeRemark, String counterpartyCountry,
                                          String counterpartyName, String reportStatus,
                                          LocalDateTime submitTime, String regulatoryRef,
                                          String errorMsg) {
        BopReport report = new BopReport();
        report.id = id;
        report.reportNo = reportNo;
        report.reportType = reportType;
        report.customerId = customerId;
        report.customerName = customerName;
        report.transactionNo = transactionNo;
        report.transactionType = transactionType;
        report.transactionAmount = transactionAmount;
        report.transactionCurrency = transactionCurrency;
        report.cnyAmount = cnyAmount;
        report.exchangeRate = exchangeRate;
        report.transactionDate = transactionDate;
        report.settlementDate = settlementDate;
        report.bopCode = bopCode;
        report.bopName = bopName;
        report.purposeCode = purposeCode;
        report.purposeRemark = purposeRemark;
        report.counterpartyCountry = counterpartyCountry;
        report.counterpartyName = counterpartyName;
        report.reportStatus = reportStatus;
        report.submitTime = submitTime;
        report.regulatoryRef = regulatoryRef;
        report.errorMsg = errorMsg;
        return report;
    }

    /**
     * Mark the draft report as ready for submission. 标记申报为就绪状态。
     */
    public void markReady() {
        if (!STATUS_DRAFT.equals(this.reportStatus)) {
            throw new BusinessException("只有草稿状态的申报才能标记就绪");
        }
        this.reportStatus = STATUS_READY;
        markUpdated();
    }

    /**
     * Submit the report to the regulatory authority. 提交申报。
     */
    public void submit() {
        if (!STATUS_READY.equals(this.reportStatus) && !STATUS_DRAFT.equals(this.reportStatus)) {
            throw new BusinessException("只能提交草稿或就绪状态的申报");
        }
        this.reportStatus = STATUS_SUBMITTED;
        this.submitTime = LocalDateTime.now();
        markUpdated();
    }

    /**
     * Accept the submitted report by the regulatory authority. 监管机构受理申报。
     */
    public void accept() {
        if (!STATUS_SUBMITTED.equals(this.reportStatus)) {
            throw new BusinessException("只能受理已提交状态的申报");
        }
        this.reportStatus = STATUS_ACCEPTED;
        markUpdated();
    }

    /**
     * Reject the submitted report with an error reason. 监管机构退回申报。
     */
    public void reject(String reason) {
        if (!STATUS_SUBMITTED.equals(this.reportStatus)) {
            throw new BusinessException("只能拒绝已提交状态的申报");
        }
        this.reportStatus = STATUS_REJECTED;
        this.errorMsg = reason;
        markUpdated();
    }

    /**
     * Correct a rejected report and reset to draft. 更正被退回的申报。
     */
    public void correct() {
        if (!STATUS_REJECTED.equals(this.reportStatus)) {
            throw new BusinessException("只有被退回的申报才能更正");
        }
        this.reportStatus = STATUS_DRAFT;
        this.errorMsg = null;
        markUpdated();
    }

    @Override
    protected void validate() {
        if (reportNo == null || reportNo.isBlank()) {
            throw new BusinessException("申报编号不能为空");
        }
        if (reportType == null || reportType.isBlank()) {
            throw new BusinessException("申报类型不能为空");
        }
        if (customerId == null) {
            throw new BusinessException("客户ID不能为空");
        }
        if (transactionNo == null || transactionNo.isBlank()) {
            throw new BusinessException("交易编号不能为空");
        }
        if (transactionAmount == null || transactionAmount.compareTo(BigDecimal.ZERO) <= 0) {
            throw new BusinessException("交易金额必须大于0");
        }
        if (transactionCurrency == null || transactionCurrency.isBlank()) {
            throw new BusinessException("交易币种不能为空");
        }
    }
}
