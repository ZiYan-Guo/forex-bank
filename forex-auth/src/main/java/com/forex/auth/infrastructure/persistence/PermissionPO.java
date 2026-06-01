package com.forex.auth.infrastructure.persistence;

import com.forex.common.mybatis.base.BasePO;

import com.baomidou.mybatisplus.annotation.TableName;

import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = true)
@TableName("sys_permission")
public class PermissionPO extends BasePO {

    private String permCode;
    private String permName;
    private String permType;
    private String parentCode;
    private String path;
    private Integer sortOrder;
}
