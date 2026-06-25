package com.forex.trading.adapter.dto;

import com.forex.common.base.dto.PageReq;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.time.LocalDate;

@Data
@EqualsAndHashCode(callSuper = true)
@Schema(description = "外汇交易分页查询请求")
public class TradePageQuery extends PageReq {

    @Schema(description = "交易编号")
    private String tradeNo;

    @Schema(description = "客户ID")
    private Long customerId;

    @Schema(description = "交易类型")
    private String tradeType;

    @Schema(description = "交易状态")
    private String tradeStatus;

    @Schema(description = "交易方向")
    private String dealType;

    @Schema(description = "开始日期")
    private LocalDate startDate;

    @Schema(description = "结束日期")
    private LocalDate endDate;
}
