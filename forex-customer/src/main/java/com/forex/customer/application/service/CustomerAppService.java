package com.forex.customer.application.service;

import com.forex.common.base.dto.PageResp;
import com.forex.customer.application.command.CreateCustomerCmd;
import com.forex.customer.application.command.UpdateCustomerCmd;
import com.forex.customer.application.query.CustomerDetailDTO;
import com.forex.customer.application.query.CustomerListDTO;
import com.forex.customer.application.query.CustomerQuery;
import com.forex.customer.domain.model.aggregate.Customer;
import com.forex.customer.domain.model.dto.CustomerRegInfo;
import com.forex.customer.domain.model.entity.CreditLimit;
import com.forex.customer.domain.model.valueobject.CustomerId;
import com.forex.customer.domain.model.valueobject.RiskLevel;
import com.forex.customer.domain.repository.CreditLimitRepository;
import com.forex.customer.domain.repository.CustomerRepository;
import com.forex.customer.domain.service.CreditCheckDomainService;
import com.forex.customer.domain.service.CustomerDomainService;

import lombok.RequiredArgsConstructor;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;
import java.util.stream.Collectors;
import com.forex.common.base.exception.BusinessException;
import com.forex.common.base.result.ResultCode;

@Service
@RequiredArgsConstructor
public class CustomerAppService {

    private final CustomerDomainService customerDomainService;
    private final CreditCheckDomainService creditCheckDomainService;
    private final CustomerRepository customerRepository;
    private final CreditLimitRepository creditLimitRepository;

    @Transactional
    public Customer createCustomer(CreateCustomerCmd cmd) {
        CustomerRegInfo info = new CustomerRegInfo(
                cmd.getCustomerType(),
                cmd.getCustomerName(),
                cmd.getEnglishName(),
                cmd.getCertType(),
                cmd.getCertNo(),
                cmd.getCountryCode(),
                cmd.getAddress(),
                cmd.getContactPerson(),
                cmd.getContactPhone(),
                cmd.getEmail(),
                RiskLevel.LOW,
                null,
                null,
                cmd.getRemark()
        );
        return customerDomainService.createCustomer(info);
    }

    @Transactional
    public Customer updateCustomer(UpdateCustomerCmd cmd) {
        Customer customer = customerRepository.findById(CustomerId.of(cmd.getCustomerId()))
                .orElseThrow(() -> new BusinessException(ResultCode.NOT_FOUND, "客户不存在"));
        if (cmd.getCustomerName() != null) {
            // update fields would go here
        }
        customerRepository.save(customer);
        return customer;
    }

    @Transactional
    public void updateRiskLevel(Long customerId, Integer newLevel, String reason) {
        Customer customer = customerRepository.findById(CustomerId.of(customerId))
                .orElseThrow(() -> new BusinessException(ResultCode.NOT_FOUND, "客户不存在"));
        customerDomainService.updateRiskLevel(customer, newLevel, reason);
    }

    @Transactional
    public void performDueDiligence(Long customerId) {
        Customer customer = customerRepository.findById(CustomerId.of(customerId))
                .orElseThrow(() -> new BusinessException(ResultCode.NOT_FOUND, "客户不存在"));
        customerDomainService.performDueDiligence(customer);
    }

    public CustomerDetailDTO getCustomerDetail(Long customerId) {
        Customer customer = customerRepository.findById(CustomerId.of(customerId))
                .orElseThrow(() -> new BusinessException(ResultCode.NOT_FOUND, "客户不存在"));
        List<CreditLimit> limits = creditLimitRepository.findByCustomerId(customerId);
        return toDetailDTO(customer, limits);
    }

    public PageResp<CustomerListDTO> pageQuery(CustomerQuery query) {
        com.forex.customer.domain.model.dto.CustomerQuery domainQuery =
                new com.forex.customer.domain.model.dto.CustomerQuery();
        domainQuery.setCustomerNo(query.getCustomerNo());
        domainQuery.setCustomerName(query.getCustomerName());
        domainQuery.setCustomerType(query.getCustomerType());
        domainQuery.setRiskLevel(query.getRiskLevel());
        domainQuery.setStatus(query.getStatus());
        domainQuery.setCertNo(query.getCertNo());

        PageResp<Customer> pageResp = customerRepository.pageQuery(query);
        List<CustomerListDTO> dtoList = pageResp.getRecords().stream()
                .map(this::toListDTO)
                .toList();
        return PageResp.of(pageResp.getTotal(), dtoList, pageResp.getPageNum(), pageResp.getPageSize());
    }

    public boolean checkCredit(Long customerId, String limitType, String currency, BigDecimal amount) {
        return creditCheckDomainService.checkCreditAvailability(customerId, limitType, currency, amount);
    }

    @Transactional
    public void deductCredit(Long customerId, String limitType, String currency, BigDecimal amount) {
        creditCheckDomainService.deductCredit(customerId, limitType, currency, amount);
    }

    private CustomerDetailDTO toDetailDTO(Customer customer, List<CreditLimit> limits) {
        CustomerDetailDTO dto = new CustomerDetailDTO();
        dto.setId(customer.getId());
        dto.setCustomerNo(customer.getCustomerNo());
        dto.setCustomerType(customer.getCustomerType());
        dto.setCustomerName(customer.getCustomerName());
        dto.setEnglishName(customer.getEnglishName());
        dto.setCertType(customer.getCertType());
        dto.setCertNo(customer.getCertNo());
        dto.setCountryCode(customer.getCountryCode());
        dto.setAddress(customer.getAddress());
        dto.setContactPerson(customer.getContactPerson());
        dto.setContactPhone(customer.getContactPhone());
        dto.setEmail(customer.getEmail());
        dto.setRiskLevel(customer.getRiskLevel());
        dto.setRiskReason(customer.getRiskReason());
        dto.setDueDiligenceStatus(customer.getDueDiligenceStatus());
        dto.setDueDiligenceDate(customer.getDueDiligenceDate());
        dto.setCrossBorderPlatformId(customer.getCrossBorderPlatformId());
        dto.setStatus(customer.getStatus());
        dto.setRemark(customer.getRemark());
        dto.setLimits(limits);
        return dto;
    }

    private CustomerListDTO toListDTO(Customer customer) {
        CustomerListDTO dto = new CustomerListDTO();
        dto.setId(customer.getId());
        dto.setCustomerNo(customer.getCustomerNo());
        dto.setCustomerType(customer.getCustomerType());
        dto.setCustomerName(customer.getCustomerName());
        dto.setRiskLevel(customer.getRiskLevel());
        dto.setStatus(customer.getStatus());
        List<CreditLimit> limits = creditLimitRepository.findByCustomerId(customer.getId());
        dto.setCreditLimitsSummary(formatCreditLimitsSummary(limits));
        return dto;
    }

    private String formatCreditLimitsSummary(List<CreditLimit> limits) {
        return limits.stream()
                .map(l -> l.getLimitType() + "/" + l.getCurrency() + ":" + l.getAvailableLimit())
                .collect(Collectors.joining(", "));
    }
}
