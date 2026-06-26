package com.forex.bookkeeping.adapter.dto;

import com.forex.common.base.dto.PageReq;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.time.LocalDate;

@Data
@EqualsAndHashCode(callSuper = true)
@Schema(description = "会计分录分页查询请求")
public class JournalPageQuery extends PageReq {

    @Schema(description = "凭证号")
    private String voucherNo;

    @Schema(description = "凭证日期")
    private LocalDate voucherDate;

    @Schema(description = "会计期")
    private String fiscalPeriod;

    @Schema(description = "业务类型")
    private String bizType;

    @Schema(description = "分录状态")
    private String entryStatus;

    @Schema(description = "科目代码")
    private String accountCode;

    @Schema(description = "借贷方向")
    private String entryDirection;

    @Schema(description = "币种")
    private String currency;

    @Schema(description = "开始日期")
    private LocalDate startDate;

    @Schema(description = "结束日期")
    private LocalDate endDate;
}
