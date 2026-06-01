package com.forex.settlement.adapter.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.math.BigDecimal;

@Data
public class CollectionReq {

    @Schema(description = "客户ID", example = "1")
    private Long customerId;

    @NotBlank(message = "托收类型不能为空")
    @Schema(description = "托收类型", example = "DA")
    private String collectionType;

    @Schema(description = "托收方式")
    private String collectionForm;

    @NotNull(message = "托收金额不能为空")
    @Schema(description = "托收金额", example = "30000.00")
    private BigDecimal collectionAmount;

    @NotBlank(message = "托收币种不能为空")
    @Schema(description = "托收币种", example = "USD")
    private String collectionCurrency;

    @Schema(description = "付款人名称")
    private String draweeName;

    @Schema(description = "单据清单")
    private String documentsList;

    @Schema(description = "备注")
    private String remark;
}
