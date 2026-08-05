package com.forex.settlement.domain.model.entity;

import com.forex.common.base.domain.BaseEntity;
import com.forex.common.base.exception.BusinessException;

import lombok.Getter;

import java.math.BigDecimal;

/**
 * Documentary collection entity with lifecycle state transitions.
 * 跟单托收实体，封装生命周期状态流转。
 */
@Getter
public class DocumentaryCollection extends BaseEntity {

    private static final long serialVersionUID = 1L;

    public static final String STATUS_DRAFT = "DRAFT";
    public static final String STATUS_DOCS_RECEIVED = "DOCS_RECEIVED";
    public static final String STATUS_PRESENTED = "PRESENTED";
    public static final String STATUS_ACCEPTED = "ACCEPTED";
    public static final String STATUS_PAID = "PAID";

    private Long id;
    private String collectionNo;
    private Long customerId;
    private String collectionType;
    private String collectionForm;
    private BigDecimal collectionAmount;
    private String collectionCurrency;
    private String drawerInfo;
    private String draweeInfo;
    private String remittingBank;
    private String collectingBank;
    private String documentsList;
    private String collectionStatus;
    private String swiftRef;
    private Long operatorId;
    private String remark;

    public DocumentaryCollection(Long id, String collectionNo, Long customerId,
                                  String collectionType, String collectionForm,
                                  BigDecimal collectionAmount, String collectionCurrency,
                                  String drawerInfo, String draweeInfo,
                                  String remittingBank, String collectingBank,
                                  String documentsList, String collectionStatus,
                                  String swiftRef, Long operatorId, String remark) {
        this.id = id;
        this.collectionNo = collectionNo;
        this.customerId = customerId;
        this.collectionType = collectionType;
        this.collectionForm = collectionForm;
        this.collectionAmount = collectionAmount;
        this.collectionCurrency = collectionCurrency;
        this.drawerInfo = drawerInfo;
        this.draweeInfo = draweeInfo;
        this.remittingBank = remittingBank;
        this.collectingBank = collectingBank;
        this.documentsList = documentsList;
        this.collectionStatus = collectionStatus;
        this.swiftRef = swiftRef;
        this.operatorId = operatorId;
        this.remark = remark;
    }

    /**
     * Marks documents as received by the bank.
     * 标记银行已收单。
     */
    public void markReceivedDocuments() {
        if (!STATUS_DRAFT.equals(collectionStatus)) {
            throw new BusinessException("只有草稿状态的托收才能收单");
        }
        this.collectionStatus = STATUS_DOCS_RECEIVED;
    }

    /**
     * Presents documents to the drawee.
     * 向付款人提示单据。
     */
    public void presentToDrawee() {
        if (!STATUS_DOCS_RECEIVED.equals(collectionStatus)) {
            throw new BusinessException("只有已收单的托收才能提示付款人");
        }
        this.collectionStatus = STATUS_PRESENTED;
    }

    /**
     * Accepts the collection.
     * 承兑托收。
     */
    public void accept() {
        if (!STATUS_PRESENTED.equals(collectionStatus) && !STATUS_DOCS_RECEIVED.equals(collectionStatus)) {
            throw new BusinessException("当前托收状态不允许承兑");
        }
        this.collectionStatus = STATUS_ACCEPTED;
    }

    /**
     * Pays the collection.
     * 托收付款。
     */
    public void pay() {
        if (!STATUS_DOCS_RECEIVED.equals(collectionStatus)
                && !STATUS_PRESENTED.equals(collectionStatus)
                && !STATUS_ACCEPTED.equals(collectionStatus)) {
            throw new BusinessException("当前托收状态不允许付款");
        }
        this.collectionStatus = STATUS_PAID;
    }
}
