package com.forex.payment.infrastructure.persistence;

import com.forex.common.mybatis.base.BasePO;

import com.baomidou.mybatisplus.annotation.TableName;

import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * Payment template persistent object.
 * 跨境支付场景模板持久化对象。
 */
@Data
@EqualsAndHashCode(callSuper = true)
@TableName("t_payment_template")
public class PaymentTemplatePO extends BasePO {
    private String templateCode;
    private String templateName;
    private String scenarioType;
    private String paymentDirection;
    private String defaultPayCurrency;
    private String defaultBeneficiaryCountry;
    private String beneficiaryDetails;
    private String defaultPurpose;
    private String defaultPurposeCode;
    private String usageInstructions;
    private Integer sortOrder;
    private Boolean isPublic;
    private Long ownerCustomerId;
}
