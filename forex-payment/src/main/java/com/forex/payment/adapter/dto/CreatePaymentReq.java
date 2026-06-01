package com.forex.payment.adapter.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDate;

@Data
public class CreatePaymentReq {

    @NotNull(message = "客户ID不能为空")
    @Schema(description = "客户ID", example = "1")
    private Long customerId;

    @NotBlank(message = "支付类型不能为空")
    @Schema(description = "支付类型: TT/DD/CIPS", example = "TT")
    private String paymentType;

    @NotNull(message = "支付金额不能为空")
    @DecimalMin(value = "0.01", message = "支付金额必须大于0.01")
    @Schema(description = "支付金额", example = "10000.00")
    private BigDecimal payAmount;

    @NotBlank(message = "支付币种不能为空")
    @Schema(description = "支付币种", example = "USD")
    private String payCurrency;

    @Schema(description = "收款人名称")
    private String beneficiaryName;

    @Schema(description = "收款人账号")
    private String beneficiaryAccount;

    @Schema(description = "收款银行")
    private String beneficiaryBank;

    @Schema(description = "收款银行SWIFT代码")
    private String beneficiarySwift;

    @Schema(description = "收款人地址")
    private String beneficiaryAddress;

    @Schema(description = "收款人国家")
    private String beneficiaryCountry;

    @Schema(description = "汇款人名称")
    private String senderName;

    @Schema(description = "汇款人账号")
    private String senderAccount;

    @Schema(description = "汇款人地址")
    private String senderAddress;

    @Schema(description = "中间行")
    private String intermediaryBank;

    @Schema(description = "付款行代码")
    private String payingBankCode;

    @Schema(description = "收款行代码")
    private String receivingBankCode;

    @Schema(description = "支付用途")
    private String paymentPurpose;

    @Schema(description = "银行用途代码")
    private String bankPurposeCode;

    @Schema(description = "费用承担方: OUR/SHA/BEN")
    private String chargeBearer;

    @Schema(description = "起息日")
    private LocalDate valueDate;

    @Schema(description = "备注")
    private String remark;
}
