package com.forex.customer.infrastructure.mapper;

import com.forex.common.mybatis.base.BaseMapperExt;
import com.forex.customer.infrastructure.persistence.CustomerQuotaPO;

import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface CustomerQuotaMapper extends BaseMapperExt<CustomerQuotaPO> {
}
