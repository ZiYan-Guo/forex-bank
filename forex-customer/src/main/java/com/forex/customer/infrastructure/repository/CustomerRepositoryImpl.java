package com.forex.customer.infrastructure.repository;

import com.forex.common.base.dto.PageReq;
import com.forex.common.base.dto.PageResp;
import com.forex.customer.domain.model.aggregate.Customer;
import com.forex.customer.domain.model.dto.CustomerQuery;
import com.forex.customer.domain.model.valueobject.CustomerId;
import com.forex.customer.domain.repository.CustomerRepository;
import com.forex.customer.infrastructure.mapper.CustomerMapper;
import com.forex.customer.infrastructure.persistence.CustomerPO;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;

import lombok.RequiredArgsConstructor;

import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
@RequiredArgsConstructor
public class CustomerRepositoryImpl implements CustomerRepository {

    private final CustomerMapper customerMapper;

    @Override
    public Customer save(Customer customer) {
        CustomerPO po = toPO(customer);
        if (customer.getId() == null) {
            customerMapper.insert(po);
        } else {
            customerMapper.updateById(po);
        }
        return toDomain(po);
    }

    @Override
    public Optional<Customer> findById(CustomerId id) {
        CustomerPO po = customerMapper.selectById(id.getValue());
        return Optional.ofNullable(po).map(this::toDomain);
    }

    @Override
    public Optional<Customer> findByCustomerNo(String customerNo) {
        CustomerPO po = customerMapper.selectByCustomerNo(customerNo);
        return Optional.ofNullable(po).map(this::toDomain);
    }

    @Override
    public List<Customer> findByCondition(CustomerQuery query) {
        return customerMapper.selectByCondition(query).stream()
                .map(this::toDomain)
                .toList();
    }

    @Override
    public boolean existsByCertNo(String certType, String certNo) {
        List<CustomerPO> list = customerMapper.selectByCertNo(certType, certNo);
        return !list.isEmpty();
    }

    @Override
    public PageResp<Customer> pageQuery(PageReq pageReq) {
        Page<CustomerPO> page = new Page<>(pageReq.getPageNum(), pageReq.getPageSize());
        Page<CustomerPO> result = customerMapper.selectPage(page, null);
        List<Customer> records = result.getRecords().stream()
                .map(this::toDomain)
                .toList();
        return PageResp.of(result.getTotal(), records, pageReq.getPageNum(), pageReq.getPageSize());
    }

    private Customer toDomain(CustomerPO po) {
        return Customer.reconstitute(
                po.getId(),
                po.getCustomerNo(),
                po.getCustomerType(),
                po.getCustomerName(),
                po.getEnglishName(),
                po.getCertType(),
                po.getCertNo(),
                po.getCountryCode(),
                po.getAddress(),
                po.getContactPerson(),
                po.getContactPhone(),
                po.getEmail(),
                po.getRiskLevel(),
                po.getRiskReason(),
                po.getDueDiligenceStatus(),
                po.getDueDiligenceDate(),
                po.getCrossBorderPlatformId(),
                po.getStatus(),
                po.getRemark()
        );
    }

    private CustomerPO toPO(Customer customer) {
        CustomerPO po = new CustomerPO();
        po.setId(customer.getId());
        po.setCustomerNo(customer.getCustomerNo());
        po.setCustomerType(customer.getCustomerType());
        po.setCustomerName(customer.getCustomerName());
        po.setEnglishName(customer.getEnglishName());
        po.setCertType(customer.getCertType());
        po.setCertNo(customer.getCertNo());
        po.setCountryCode(customer.getCountryCode());
        po.setAddress(customer.getAddress());
        po.setContactPerson(customer.getContactPerson());
        po.setContactPhone(customer.getContactPhone());
        po.setEmail(customer.getEmail());
        po.setRiskLevel(customer.getRiskLevel());
        po.setRiskReason(customer.getRiskReason());
        po.setDueDiligenceStatus(customer.getDueDiligenceStatus());
        po.setDueDiligenceDate(customer.getDueDiligenceDate());
        po.setCrossBorderPlatformId(customer.getCrossBorderPlatformId());
        po.setStatus(customer.getStatus());
        po.setRemark(customer.getRemark());
        return po;
    }
}
