package com.forex.settlement.adapter.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
public class CollectionResp {

    @Schema(description = "ID")
    private Long id;

    @Schema(description = "托收编号")
    private String collectionNo;

    @Schema(description = "客户ID")
    private Long customerId;

    @Schema(description = "托收类型")
    private String collectionType;

    @Schema(description = "托收方式")
    private String collectionForm;

    @Schema(description = "托收金额")
    private BigDecimal collectionAmount;

    @Schema(description = "托收币种")
    private String collectionCurrency;

    @Schema(description = "委托人信息")
    private String drawerInfo;

    @Schema(description = "付款人信息")
    private String draweeInfo;

    @Schema(description = "寄单行")
    private String remittingBank;

    @Schema(description = "代收行")
    private String collectingBank;

    @Schema(description = "单据清单")
    private String documentsList;

    @Schema(description = "托收状态")
    private String collectionStatus;

    @Schema(description = "SWIFT参考号")
    private String swiftRef;

    @Schema(description = "操作员ID")
    private Long operatorId;

    @Schema(description = "备注")
    private String remark;

    @Schema(description = "创建时间")
    private LocalDateTime createTime;
}
