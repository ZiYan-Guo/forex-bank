package com.forex.customer.adapter.controller;

import com.forex.common.base.annotation.Idempotent;
import com.forex.common.base.annotation.RedisLock;
import com.forex.common.base.dto.PageResp;
import com.forex.common.base.result.R;
import com.forex.customer.adapter.dto.CreditCheckReq;
import com.forex.customer.adapter.dto.CustomerReq;
import com.forex.customer.adapter.dto.CustomerResp;
import com.forex.customer.application.command.CreateCustomerCmd;
import com.forex.customer.application.command.UpdateCustomerCmd;
import com.forex.customer.application.query.CustomerDetailDTO;
import com.forex.customer.application.query.CustomerListDTO;
import com.forex.customer.application.query.CustomerQuery;
import com.forex.customer.application.service.CustomerAppService;
import com.forex.customer.domain.model.aggregate.Customer;
import com.forex.customer.domain.model.entity.CreditLimit;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;

import lombok.RequiredArgsConstructor;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.stream.Collectors;

@Tag(name = "客户管理")
@RestController
@RequestMapping("/api/customer")
@RequiredArgsConstructor
public class CustomerController {

    private final CustomerAppService customerAppService;

    @Operation(summary = "创建客户")
    @PostMapping("/create")
    public R<CustomerResp> create(@Valid @RequestBody CustomerReq req) {
        CreateCustomerCmd cmd = new CreateCustomerCmd();
        cmd.setCustomerType(req.getCustomerType());
        cmd.setCustomerName(req.getCustomerName());
        cmd.setEnglishName(req.getEnglishName());
        cmd.setCertType(req.getCertType());
        cmd.setCertNo(req.getCertNo());
        cmd.setCountryCode(req.getCountryCode());
        cmd.setAddress(req.getAddress());
        cmd.setContactPerson(req.getContactPerson());
        cmd.setContactPhone(req.getContactPhone());
        cmd.setEmail(req.getEmail());
        cmd.setRemark(req.getRemark());
        Customer customer = customerAppService.createCustomer(cmd);
        return R.ok("客户创建成功", toResp(customer));
    }

    @Operation(summary = "更新客户信息")
    @PutMapping("/update")
    public R<CustomerResp> update(@Valid @RequestBody CustomerReq req) {
        UpdateCustomerCmd cmd = new UpdateCustomerCmd();
        cmd.setCustomerId(req.getCustomerId());
        cmd.setCustomerName(req.getCustomerName());
        cmd.setEnglishName(req.getEnglishName());
        cmd.setAddress(req.getAddress());
        cmd.setContactPerson(req.getContactPerson());
        cmd.setContactPhone(req.getContactPhone());
        cmd.setEmail(req.getEmail());
        cmd.setRiskLevel(req.getRiskLevel());
        cmd.setRiskReason(req.getRiskReason());
        cmd.setStatus(req.getStatus());
        cmd.setRemark(req.getRemark());
        Customer customer = customerAppService.updateCustomer(cmd);
        return R.ok("客户更新成功", toResp(customer));
    }

    @Operation(summary = "查询客户详情")
    @GetMapping("/{id}")
    public R<CustomerResp> getById(@PathVariable Long id) {
        CustomerDetailDTO dto = customerAppService.getCustomerDetail(id);
        return R.ok(toResp(dto));
    }

    @Operation(summary = "分页查询客户")
    @PostMapping("/page")
    public R<PageResp<CustomerResp>> pageQuery(@RequestBody CustomerQuery query) {
        PageResp<CustomerListDTO> pageResp = customerAppService.pageQuery(query);
        List<CustomerResp> records = pageResp.getRecords().stream()
                .map(this::toResp)
                .collect(Collectors.toList());
        PageResp<CustomerResp> resp = PageResp.of(
                pageResp.getTotal(), records, pageResp.getPageNum(), pageResp.getPageSize());
        return R.ok(resp);
    }

    @Operation(summary = "更新客户风险等级")
    @PutMapping("/risk-level")
    @RedisLock(key = "'customer:riskLevel:' + #cmd.customerId")
    @Idempotent(key = "'customer:riskLevel:' + #cmd.customerId", expireSeconds = 30)
    public R<Void> updateRiskLevel(@RequestBody UpdateCustomerCmd cmd) {
        customerAppService.updateRiskLevel(cmd.getCustomerId(), cmd.getRiskLevel(), cmd.getRiskReason());
        return R.okMsg("风险等级更新成功");
    }

    @Operation(summary = "校验信用额度")
    @PostMapping("/check-credit")
    public R<Boolean> checkCredit(@Valid @RequestBody CreditCheckReq req) {
        boolean available = customerAppService.checkCredit(
                req.getCustomerId(), req.getLimitType(), req.getCurrency(), req.getAmount());
        return R.ok(available);
    }

    @Operation(summary = "扣减信用额度")
    @PostMapping("/deduct-credit")
    @RedisLock(key = "'customer:credit:deduct:' + #req.customerId")
    public R<Void> deductCredit(@Valid @RequestBody CreditCheckReq req) {
        customerAppService.deductCredit(req.getCustomerId(), req.getLimitType(), req.getCurrency(), req.getAmount());
        return R.okMsg("信用额度扣减成功");
    }

    @Operation(summary = "执行尽职调查")
    @PutMapping("/due-diligence/{id}")
    public R<Void> performDueDiligence(@PathVariable Long id) {
        customerAppService.performDueDiligence(id);
        return R.okMsg("尽职调查完成");
    }

    private CustomerResp toResp(Customer customer) {
        CustomerResp resp = new CustomerResp();
        resp.setId(customer.getId());
        resp.setCustomerNo(customer.getCustomerNo());
        resp.setCustomerType(customer.getCustomerType());
        resp.setCustomerName(customer.getCustomerName());
        resp.setEnglishName(customer.getEnglishName());
        resp.setCertType(customer.getCertType());
        resp.setCertNo(customer.getCertNo());
        resp.setCountryCode(customer.getCountryCode());
        resp.setAddress(customer.getAddress());
        resp.setContactPerson(customer.getContactPerson());
        resp.setContactPhone(customer.getContactPhone());
        resp.setEmail(customer.getEmail());
        resp.setRiskLevel(customer.getRiskLevel());
        resp.setRiskReason(customer.getRiskReason());
        resp.setDueDiligenceStatus(customer.getDueDiligenceStatus());
        resp.setDueDiligenceDate(customer.getDueDiligenceDate());
        resp.setCrossBorderPlatformId(customer.getCrossBorderPlatformId());
        resp.setStatus(customer.getStatus());
        resp.setRemark(customer.getRemark());
        return resp;
    }

    private CustomerResp toResp(CustomerDetailDTO dto) {
        CustomerResp resp = new CustomerResp();
        resp.setId(dto.getId());
        resp.setCustomerNo(dto.getCustomerNo());
        resp.setCustomerType(dto.getCustomerType());
        resp.setCustomerName(dto.getCustomerName());
        resp.setEnglishName(dto.getEnglishName());
        resp.setCertType(dto.getCertType());
        resp.setCertNo(dto.getCertNo());
        resp.setCountryCode(dto.getCountryCode());
        resp.setAddress(dto.getAddress());
        resp.setContactPerson(dto.getContactPerson());
        resp.setContactPhone(dto.getContactPhone());
        resp.setEmail(dto.getEmail());
        resp.setRiskLevel(dto.getRiskLevel());
        resp.setRiskReason(dto.getRiskReason());
        resp.setDueDiligenceStatus(dto.getDueDiligenceStatus());
        resp.setDueDiligenceDate(dto.getDueDiligenceDate());
        resp.setCrossBorderPlatformId(dto.getCrossBorderPlatformId());
        resp.setStatus(dto.getStatus());
        resp.setRemark(dto.getRemark());
        if (dto.getLimits() != null) {
            resp.setLimits(dto.getLimits().stream().map(this::toCreditLimitResp).collect(Collectors.toList()));
        }
        return resp;
    }

    private CustomerResp toResp(CustomerListDTO dto) {
        CustomerResp resp = new CustomerResp();
        resp.setId(dto.getId());
        resp.setCustomerNo(dto.getCustomerNo());
        resp.setCustomerType(dto.getCustomerType());
        resp.setCustomerName(dto.getCustomerName());
        resp.setRiskLevel(dto.getRiskLevel());
        resp.setStatus(dto.getStatus());
        return resp;
    }

    private CustomerResp.CreditLimitResp toCreditLimitResp(CreditLimit limit) {
        CustomerResp.CreditLimitResp lr = new CustomerResp.CreditLimitResp();
        lr.setId(limit.getId());
        lr.setLimitType(limit.getLimitType());
        lr.setCurrency(limit.getCurrency());
        lr.setTotalLimit(limit.getTotalLimit());
        lr.setUsedLimit(limit.getUsedLimit());
        lr.setAvailableLimit(limit.getAvailableLimit());
        return lr;
    }
}
