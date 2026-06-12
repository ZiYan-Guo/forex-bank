package com.forex.payment.domain.model.entity;

import com.forex.common.base.domain.BaseEntity;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

/**
 * Cross-border payment scenario template entity.
 * Prefills payment form fields for common cross-border payment scenarios.
 * 跨境支付场景模板实体。为常见跨境支付场景预填表单字段。
 */
@Getter
@NoArgsConstructor
@AllArgsConstructor
public class PaymentTemplate extends BaseEntity {

    private static final long serialVersionUID = 1L;

    /** Primary key ID 主键ID */
    private Long id;

    /** Template code, unique identifier 模板编码，唯一标识 */
    private String templateCode;

    /** Template display name 模板显示名称 */
    private String templateName;

    /** Scenario type: STUDY_ABROAD/TRAVEL_DEPOSIT/MEDICAL_EXPENSE/CUSTOM 场景类型 */
    private String scenarioType;

    /** Payment direction: OUTWARD/INWARD 支付方向：汇出/汇入 */
    private String paymentDirection;

    /** Default pay currency code 默认支付币种 */
    private String defaultPayCurrency;

    /** Default beneficiary country code 默认收款国别代码 */
    private String defaultBeneficiaryCountry;

    /** Pre-filled beneficiary details (String/JSON for pre-filled values) 预填受益人信息JSON */
    private String beneficiaryDetails;

    /** Default payment purpose text 默认汇款用途 */
    private String defaultPurpose;

    /** Default bank purpose code for regulatory reporting 默认银行用途代码 */
    private String defaultPurposeCode;

    /** Usage instructions for the customer 使用说明 */
    private String usageInstructions;

    /** Display sort order 排序序号 */
    private Integer sortOrder;

    /** Whether this template is public to all users 是否公开模板 */
    private Boolean isPublic;

    /** Owner customer ID for custom templates 自定义模板所属的客户ID */
    private Long ownerCustomerId;
}
