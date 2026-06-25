package com.forex.exchange.adapter.dto;

import com.forex.common.base.dto.PageReq;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = true)
@Schema(description = "结售汇订单分页查询请求")
public class ExchangeOrderPageQuery extends PageReq {

    @Schema(description = "客户ID")
    private Long customerId;

    @Schema(description = "订单编号")
    private String orderNo;

    @Schema(description = "订单类型")
    private String orderType;

    @Schema(description = "交易方向")
    private String dealType;

    @Schema(description = "订单状态")
    private String orderStatus;

    @Schema(description = "基础币种")
    private String baseCurrency;

    @Schema(description = "报价币种")
    private String quoteCurrency;
}
