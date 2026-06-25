package com.forex.customer.domain.service;

import com.forex.customer.domain.event.CustomerCreatedEvent;
import com.forex.customer.domain.model.aggregate.Customer;
import com.forex.customer.domain.model.dto.CustomerRegInfo;
import com.forex.customer.domain.repository.CustomerRepository;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.concurrent.ThreadLocalRandom;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional
public class CustomerDomainService {

    private final CustomerRepository customerRepository;
    private final ApplicationEventPublisher eventPublisher;

    public Customer createCustomer(CustomerRegInfo info) {
        if (info.getCertType() != null && info.getCertNo() != null
                && customerRepository.existsByCertNo(info.getCertType(), info.getCertNo())) {
            throw new IllegalArgumentException("证件信息已存在");
        }

        String customerNo = generateCustomerNo();

        Customer customer = Customer.create(
                customerNo,
                info.getCustomerType(),
                info.getCustomerName(),
                info.getEnglishName(),
                info.getCertType(),
                info.getCertNo(),
                info.getCountryCode(),
                info.getAddress(),
                info.getContactPerson(),
                info.getContactPhone(),
                info.getEmail(),
                info.getRiskLevel(),
                info.getRiskReason(),
                info.getCrossBorderPlatformId(),
                info.getRemark()
        );

        Customer saved = customerRepository.save(customer);

        eventPublisher.publishEvent(new CustomerCreatedEvent(
                saved.getId(), saved.getCustomerNo(), saved.getCustomerName()));

        log.info("客户创建成功: customerNo={}, customerName={}", saved.getCustomerNo(), saved.getCustomerName());
        return saved;
    }

    public void updateRiskLevel(Customer customer, Integer newLevel, String reason) {
        Integer oldLevel = customer.getRiskLevel();
        customer.updateRiskLevel(newLevel, reason);
        customerRepository.save(customer);

        log.info("客户风险等级变更: customerId={}, oldLevel={}, newLevel={}, reason={}",
                customer.getId(), oldLevel, newLevel, reason);
    }

    public void performDueDiligence(Customer customer) {
        if (customer.isDueDiligenceCompleted()) {
            throw new IllegalStateException("客户已完成尽职调查");
        }
        customer.completeDueDiligence();
        customerRepository.save(customer);

        log.info("客户尽职调查完成: customerId={}", customer.getId());
    }

    private String generateCustomerNo() {
        String datePart = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMddHHmmss"));
        int randomPart = ThreadLocalRandom.current().nextInt(1000, 10000);
        return "CUS" + datePart + randomPart;
    }
}
