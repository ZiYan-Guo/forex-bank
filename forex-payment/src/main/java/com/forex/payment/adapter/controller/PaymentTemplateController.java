package com.forex.payment.adapter.controller;

import com.forex.common.base.result.R;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.atomic.AtomicLong;

/**
 * Payment scenario template controller.
 * Manages pre-configured templates for common cross-border payment scenarios.
 * 支付场景模板控制器。管理常见跨境支付场景的预配置模板。
 */
@Tag(name = "场景模板")
@RestController
@RequestMapping("/api/payment/template")
@RequiredArgsConstructor
@Slf4j
public class PaymentTemplateController {

    private final List<Map<String, Object>> templateStore = new CopyOnWriteArrayList<>();
    private final AtomicLong idGenerator = new AtomicLong(100);

    {
        // Seed 3 default templates 种子3个默认模板
        seedDefaultTemplates();
    }

    /**
     * Seed initial default scenario templates.
     * 初始化默认场景模板数据。
     */
    private void seedDefaultTemplates() {
        // 留学汇款模板 - 美国哈佛大学 留学汇款(USA Harvard)
        Map<String, Object> t1 = new LinkedHashMap<>();
        t1.put("id", idGenerator.getAndIncrement());
        t1.put("templateCode", "TPL_STUDY_001");
        t1.put("templateName", "Study Abroad - US Tuition 美国留学学费");
        t1.put("scenarioType", "STUDY_ABROAD");
        t1.put("paymentDirection", "OUTWARD");
        t1.put("defaultPayCurrency", "USD");
        t1.put("defaultBeneficiaryCountry", "US");
        t1.put("beneficiaryDetails", "{\"name\":\"Harvard University\",\"bank\":\"Bank of America\",\"swift\":\"BOFAUS3N\"}");
        t1.put("defaultPurpose", "OVERSEAS STUDY TUITION PAYMENT");
        t1.put("defaultPurposeCode", "S0001");
        t1.put("usageInstructions", "适用于美国大学留学学费汇款。请确保汇款账户与录取通知书一致。");
        t1.put("sortOrder", 1);
        t1.put("isPublic", true);
        t1.put("ownerCustomerId", null);
        t1.put("createTime", LocalDateTime.now().toString());
        templateStore.add(t1);

        // 旅游保证金模板 - 泰国 旅游保证金(Thailand)
        Map<String, Object> t2 = new LinkedHashMap<>();
        t2.put("id", idGenerator.getAndIncrement());
        t2.put("templateCode", "TPL_TRAVEL_001");
        t2.put("templateName", "Travel Deposit - Thailand 泰国旅游保证金");
        t2.put("scenarioType", "TRAVEL_DEPOSIT");
        t2.put("paymentDirection", "OUTWARD");
        t2.put("defaultPayCurrency", "CNY");
        t2.put("defaultBeneficiaryCountry", "TH");
        t2.put("beneficiaryDetails", "{\"name\":\"Tourism Authority of Thailand\",\"bank\":\"Bangkok Bank\",\"swift\":\"BKKBTHBK\"}");
        t2.put("defaultPurpose", "TRAVEL SECURITY DEPOSIT");
        t2.put("defaultPurposeCode", "T0001");
        t2.put("usageInstructions", "适用于出境旅游保证金缴纳。请提供旅游合同编号及出行日期。");
        t2.put("sortOrder", 2);
        t2.put("isPublic", true);
        t2.put("ownerCustomerId", null);
        t2.put("createTime", LocalDateTime.now().toString());
        templateStore.add(t2);

        // 境外医疗模板 - 日本 境外医疗(Japan)
        Map<String, Object> t3 = new LinkedHashMap<>();
        t3.put("id", idGenerator.getAndIncrement());
        t3.put("templateCode", "TPL_MEDICAL_001");
        t3.put("templateName", "Overseas Medical Expense 境外医疗费用");
        t3.put("scenarioType", "MEDICAL_EXPENSE");
        t3.put("paymentDirection", "OUTWARD");
        t3.put("defaultPayCurrency", "JPY");
        t3.put("defaultBeneficiaryCountry", "JP");
        t3.put("beneficiaryDetails", "{\"name\":\"Tokyo Medical University Hospital\",\"bank\":\"MUFG Bank\",\"swift\":\"BOTKJPJT\"}");
        t3.put("defaultPurpose", "OVERSEAS MEDICAL TREATMENT EXPENSE");
        t3.put("defaultPurposeCode", "M0001");
        t3.put("usageInstructions", "适用于境外就医费用支付。请提供医院出具的诊断证明及费用清单。");
        t3.put("sortOrder", 3);
        t3.put("isPublic", true);
        t3.put("ownerCustomerId", null);
        t3.put("createTime", LocalDateTime.now().toString());
        templateStore.add(t3);
    }

    /**
     * Create a new payment scenario template.
     * 创建新的支付场景模板。
     */
    @Operation(summary = "创建场景模板")
    @PostMapping("/create")
    public R<Map<String, Object>> createTemplate(@RequestBody Map<String, Object> body) {
        String templateName = (String) body.get("templateName");
        String scenarioType = (String) body.get("scenarioType");
        log.info("Creating payment template: name={}, scenarioType={}", templateName, scenarioType);

        Map<String, Object> template = new LinkedHashMap<>();
        template.put("id", idGenerator.getAndIncrement());
        template.put("templateCode", "TPL_" + System.currentTimeMillis());
        template.put("templateName", templateName);
        template.put("scenarioType", scenarioType);
        template.put("paymentDirection", body.getOrDefault("paymentDirection", "OUTWARD"));
        template.put("defaultPayCurrency", body.getOrDefault("defaultPayCurrency", "USD"));
        template.put("defaultBeneficiaryCountry", body.getOrDefault("defaultBeneficiaryCountry", ""));
        template.put("beneficiaryDetails", body.getOrDefault("beneficiaryDetails", "{}"));
        template.put("defaultPurpose", body.getOrDefault("defaultPurpose", ""));
        template.put("defaultPurposeCode", body.getOrDefault("defaultPurposeCode", ""));
        template.put("usageInstructions", body.getOrDefault("usageInstructions", ""));
        template.put("sortOrder", body.getOrDefault("sortOrder", 0));
        template.put("isPublic", body.getOrDefault("isPublic", false));
        template.put("ownerCustomerId", body.getOrDefault("ownerCustomerId", null));
        template.put("createTime", LocalDateTime.now().toString());
        templateStore.add(template);

        log.info("Template created: code={}, name={}", template.get("templateCode"), templateName);
        return R.ok("模板创建成功", template);
    }

    /**
     * List all public templates and user's own custom templates.
     * 查询所有公开模板及用户的私有模板。
     */
    @Operation(summary = "查询模板列表")
    @GetMapping("/list")
    public R<Map<String, Object>> listTemplates(@RequestParam(required = false) Long customerId) {
        log.info("Listing payment templates, customerId={}", customerId);
        List<Map<String, Object>> result = new ArrayList<>();
        for (Map<String, Object> t : templateStore) {
            Boolean isPublic = (Boolean) t.get("isPublic");
            Long ownerId = (Long) t.get("ownerCustomerId");
            if (Boolean.TRUE.equals(isPublic)
                    || (customerId != null && customerId.equals(ownerId))) {
                result.add(t);
            }
        }
        Map<String, Object> resp = new LinkedHashMap<>();
        resp.put("total", result.size());
        resp.put("templates", result);
        return R.ok(resp);
    }

    /**
     * Get template detail by template code.
     * 根据模板编码查询模板详情。
     */
    @Operation(summary = "查询模板详情")
    @GetMapping("/{templateCode}")
    public R<Map<String, Object>> getTemplate(@PathVariable String templateCode) {
        log.info("Getting template detail: templateCode={}", templateCode);
        for (Map<String, Object> t : templateStore) {
            if (templateCode.equals(t.get("templateCode"))) {
                return R.ok(t);
            }
        }
        log.warn("Template not found: {}", templateCode);
        return R.fail("模板不存在");
    }

    /**
     * Update an existing payment template.
     * 更新支付模板。
     */
    @Operation(summary = "更新模板")
    @PutMapping("/{templateCode}")
    public R<Map<String, Object>> updateTemplate(@PathVariable String templateCode,
                                                  @RequestBody Map<String, Object> body) {
        log.info("Updating template: templateCode={}", templateCode);
        for (Map<String, Object> t : templateStore) {
            if (templateCode.equals(t.get("templateCode"))) {
                if (body.containsKey("templateName")) t.put("templateName", body.get("templateName"));
                if (body.containsKey("scenarioType")) t.put("scenarioType", body.get("scenarioType"));
                if (body.containsKey("defaultPayCurrency")) t.put("defaultPayCurrency", body.get("defaultPayCurrency"));
                if (body.containsKey("defaultBeneficiaryCountry")) t.put("defaultBeneficiaryCountry", body.get("defaultBeneficiaryCountry"));
                if (body.containsKey("beneficiaryDetails")) t.put("beneficiaryDetails", body.get("beneficiaryDetails"));
                if (body.containsKey("defaultPurpose")) t.put("defaultPurpose", body.get("defaultPurpose"));
                if (body.containsKey("defaultPurposeCode")) t.put("defaultPurposeCode", body.get("defaultPurposeCode"));
                if (body.containsKey("usageInstructions")) t.put("usageInstructions", body.get("usageInstructions"));
                if (body.containsKey("sortOrder")) t.put("sortOrder", body.get("sortOrder"));
                if (body.containsKey("isPublic")) t.put("isPublic", body.get("isPublic"));
                log.info("Template updated: {}", templateCode);
                return R.ok("模板更新成功", t);
            }
        }
        log.warn("Template not found for update: {}", templateCode);
        return R.fail("模板不存在");
    }

    /**
     * Delete a custom template (public templates cannot be deleted).
     * 删除自定义模板（公开模板不可删除）。
     */
    @Operation(summary = "删除模板")
    @DeleteMapping("/{templateCode}")
    public R<Void> deleteTemplate(@PathVariable String templateCode) {
        log.info("Deleting template: templateCode={}", templateCode);
        boolean removed = templateStore.removeIf(t ->
                templateCode.equals(t.get("templateCode")) && !Boolean.TRUE.equals(t.get("isPublic")));
        if (removed) {
            log.info("Template deleted: {}", templateCode);
            return R.okMsg("模板已删除");
        }
        log.warn("Template not found or is public, cannot delete: {}", templateCode);
        return R.fail("模板不存在或为公开模板，无法删除");
    }

    /**
     * List all available scenario types.
     * 查询所有可用的场景类型。
     */
    @Operation(summary = "查询场景类型")
    @GetMapping("/scenarios")
    public R<List<Map<String, String>>> getScenarios() {
        log.info("Listing scenario types");
        List<Map<String, String>> scenarios = new ArrayList<>();
        scenarios.add(Map.of("code", "STUDY_ABROAD", "name", "留学汇款 留学汇款", "icon", "read"));
        scenarios.add(Map.of("code", "TRAVEL_DEPOSIT", "name", "旅游保证金 旅游保证金", "icon", "compass"));
        scenarios.add(Map.of("code", "MEDICAL_EXPENSE", "name", "境外医疗 境外医疗", "icon", "medicine-box"));
        scenarios.add(Map.of("code", "CUSTOM", "name", "自定义 自定义", "icon", "form"));
        return R.ok(scenarios);
    }
}
