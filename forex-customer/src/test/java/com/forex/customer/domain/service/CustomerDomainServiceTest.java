package com.forex.customer.domain.service;

import com.forex.customer.domain.event.CustomerCreatedEvent;
import com.forex.customer.domain.model.aggregate.Customer;
import com.forex.customer.domain.model.dto.CustomerRegInfo;
import com.forex.customer.domain.model.entity.CreditLimit;
import com.forex.customer.domain.repository.CustomerRepository;
import com.forex.customer.domain.repository.CreditLimitRepository;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.context.ApplicationEventPublisher;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class CustomerDomainServiceTest {

    @Mock private CustomerRepository customerRepository;
    @Mock private ApplicationEventPublisher eventPublisher;
    @Captor private ArgumentCaptor<CustomerCreatedEvent> createdCaptor;

    private CustomerDomainService customerDomainService;

    @BeforeEach
    void setUp() {
        customerDomainService = new CustomerDomainService(customerRepository, eventPublisher);
    }

    private CustomerRegInfo createRegInfo() {
        return new CustomerRegInfo(1, "TEST COMPANY", "TEST COMPANY LTD",
                "USCC", "91110000XXXX", "CN", "Beijing", "Mr. Wang",
                "13800138000", "test@company.com", 1, null,
                "CBP001", "New customer");
    }

    @Test
    @DisplayName("Create customer publishes event")
    void testCreateCustomer() {
        CustomerRegInfo info = createRegInfo();
        when(customerRepository.existsByCertNo(anyString(), anyString())).thenReturn(false);
        when(customerRepository.save(any())).thenAnswer(i -> i.getArgument(0));

        Customer result = customerDomainService.createCustomer(info);

        assertNotNull(result);
        assertTrue(result.getCustomerNo().startsWith("CUS"));
        verify(eventPublisher).publishEvent(createdCaptor.capture());
        assertEquals("TEST COMPANY", createdCaptor.getValue().getCustomerName());
    }

    @Test
    @DisplayName("Update risk level saves and updates")
    void testUpdateRiskLevel() {
        Customer customer = Customer.create("CUS001", 1, "TEST CO", null,
                "USCC", "91110000XXXX", "CN", "Shanghai",
                "Li", "1390000", "li@test.com", 1, null, null, null);
        when(customerRepository.save(any())).thenReturn(customer);

        customerDomainService.updateRiskLevel(customer, 3, "负面新闻");

        assertEquals(3, customer.getRiskLevel());
        verify(customerRepository).save(customer);
    }
}

@ExtendWith(MockitoExtension.class)
class CreditCheckDomainServiceTest {

    @Mock private CreditLimitRepository creditLimitRepository;

    private CreditCheckDomainService creditCheckService;

    @BeforeEach
    void setUp() {
        creditCheckService = new CreditCheckDomainService(creditLimitRepository);
    }

    @Test
    @DisplayName("Check credit returns true when sufficient")
    void testCheckCreditAvailability_Sufficient() {
        CreditLimit limit = new CreditLimit(1L, 1001L, "TRADE", "USD",
                new BigDecimal("1000000"), new BigDecimal("200000"),
                new BigDecimal("800000"),
                LocalDate.now(), LocalDate.now().plusYears(1), 1);
        when(creditLimitRepository.findByCustomerAndType(1001L, "TRADE", "USD"))
                .thenReturn(Optional.of(limit));

        assertTrue(creditCheckService.checkCreditAvailability(1001L, "TRADE", "USD",
                new BigDecimal("500000")));
    }

    @Test
    @DisplayName("Check credit returns false when insufficient")
    void testCheckCreditAvailability_Insufficient() {
        CreditLimit limit = new CreditLimit(1L, 1001L, "TRADE", "USD",
                new BigDecimal("100000"), new BigDecimal("90000"),
                new BigDecimal("10000"),
                LocalDate.now(), LocalDate.now().plusYears(1), 1);
        when(creditLimitRepository.findByCustomerAndType(1001L, "TRADE", "USD"))
                .thenReturn(Optional.of(limit));

        assertFalse(creditCheckService.checkCreditAvailability(1001L, "TRADE", "USD",
                new BigDecimal("50000")));
    }
}
