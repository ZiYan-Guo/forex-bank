package com.forex.payment.adapter.dto;

import com.forex.common.base.dto.PageReq;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.time.LocalDate;

@Data
@EqualsAndHashCode(callSuper = true)
@Schema(description = "支付分页查询请求")
public class PaymentPageQuery extends PageReq {

    @Schema(description = "支付编号")
    private String paymentNo;

    @Schema(description = "客户ID")
    private Long customerId;

    @Schema(description = "支付方向")
    private String paymentDirection;

    @Schema(description = "支付类型")
    private String paymentType;

    @Schema(description = "支付币种")
    private String payCurrency;

    @Schema(description = "支付状态")
    private String paymentStatus;

    @Schema(description = "费用承担方")
    private String chargeBearer;

    @Schema(description = "开始日期")
    private LocalDate startDate;

    @Schema(description = "结束日期")
    private LocalDate endDate;

    @Schema(description = "关键字搜索")
    private String keyword;
}
