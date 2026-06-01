package com.forex.customer.domain.repository;

import com.forex.common.base.dto.PageReq;
import com.forex.common.base.dto.PageResp;
import com.forex.customer.domain.model.aggregate.Customer;
import com.forex.customer.domain.model.dto.CustomerQuery;
import com.forex.customer.domain.model.valueobject.CustomerId;

import java.util.List;
import java.util.Optional;

public interface CustomerRepository {

    Customer save(Customer customer);

    Optional<Customer> findById(CustomerId id);

    Optional<Customer> findByCustomerNo(String customerNo);

    List<Customer> findByCondition(CustomerQuery query);

    boolean existsByCertNo(String certType, String certNo);

    PageResp<Customer> pageQuery(PageReq pageReq);
}
