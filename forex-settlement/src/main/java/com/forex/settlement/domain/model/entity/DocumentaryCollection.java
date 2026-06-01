package com.forex.settlement.domain.model.entity;

import com.forex.common.base.domain.BaseEntity;
import lombok.Getter;

import java.math.BigDecimal;

@Getter
public class DocumentaryCollection extends BaseEntity {

    private static final long serialVersionUID = 1L;

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
}
