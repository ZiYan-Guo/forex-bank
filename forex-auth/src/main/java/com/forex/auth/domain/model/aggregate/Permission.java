package com.forex.auth.domain.model.aggregate;

import com.forex.common.base.domain.BaseEntity;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class Permission extends BaseEntity {

    private Long id;
    private String permCode;
    private String permName;
    private String permType;
    private String parentCode;
    private String path;
}
