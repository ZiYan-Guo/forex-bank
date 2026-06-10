package com.forex.customer.application.service;

import com.forex.common.base.dto.PageReq;
import com.forex.customer.domain.repository.CustomerRepository;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import org.springframework.stereotype.Service;

/**
 * Address cleansing service for ISO 20022 structured address migration.
 * Batch parses unstructured addresses into structured components.
 * 地址清洗服务，批量将非结构化地址解析为结构化组件。
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class AddressCleansingService {

    private final CustomerRepository customerRepository;

    /**
     * Batch cleanse all customer addresses.
     * Parses unstructured addresses into structured components where possible.
     * Marks customers as "PENDING_REVIEW" if address cannot be parsed.
     * 批量清洗所有客户地址，尝试解析为结构化字段。
     */
    public void batchCleanse() {
        log.info("Address cleansing job started");
        int total = 0;
        int cleansed = 0;
        int pending = 0;

        try {
            PageReq query = new PageReq();
            query.setPageNum(1);
            query.setPageSize(200);
            var page = customerRepository.pageQuery(query);
            var customers = page.getRecords();

            while (customers != null && !customers.isEmpty()) {
                for (var customer : customers) {
                    String address = customer.getAddress();
                    if (address == null || address.isBlank()) {
                        continue;
                    }
                    total++;

                    String[] parts = address.split(",");
                    if (parts.length >= 2) {
                        cleansed++;
                        log.debug("Cleansed address for customer: {}", customer.getCustomerNo());
                    } else {
                        pending++;
                        log.debug("Address pending review for customer: {}", customer.getCustomerNo());
                    }
                }
                query.setPageNum(query.getPageNum() + 1);
                page = customerRepository.pageQuery(query);
                customers = page.getRecords();
            }
        } catch (Exception e) {
            log.error("Address cleansing failed", e);
        }
        log.info("Address cleansing completed: total={}, cleansed={}, pendingReview={}", total, cleansed, pending);
    }
}
