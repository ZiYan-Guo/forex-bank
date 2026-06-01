package com.forex.auth.domain.model.aggregate;

import com.forex.common.base.domain.BaseEntity;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.Set;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class Role extends BaseEntity {

    private Long id;
    private String roleCode;
    private String roleName;
    private Set<Permission> permissions;
}
