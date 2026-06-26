package com.forex.position.adapter.dto;

import com.forex.common.base.dto.PageReq;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.time.LocalDate;

@Data
@EqualsAndHashCode(callSuper = true)
@Schema(description = "敞口分页查询请求")
public class PositionPageQuery extends PageReq {

    @Schema(description = "货币对")
    private String currencyPair;

    @Schema(description = "敞口类型")
    private String positionType;

    @Schema(description = "敞口币种")
    private String positionCurrency;

    @Schema(description = "敞口日期")
    private LocalDate positionDate;

    @Schema(description = "风险级别")
    private String riskLevel;

    @Schema(description = "交易员ID")
    private Long traderId;
}
