package com.forex.settlement.infrastructure.persistence;

import com.baomidou.mybatisplus.annotation.TableName;
import com.forex.common.mybatis.base.BasePO;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.math.BigDecimal;

@Data
@EqualsAndHashCode(callSuper = true)
@TableName("t_documentary_collection")
public class DocumentaryCollectionPO extends BasePO {

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
}
