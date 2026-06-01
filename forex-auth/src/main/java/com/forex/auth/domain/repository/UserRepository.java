package com.forex.auth.domain.repository;

import com.forex.auth.domain.model.aggregate.Permission;
import com.forex.auth.domain.model.aggregate.Role;
import com.forex.auth.domain.model.aggregate.User;

import java.util.List;
import java.util.Optional;
import java.util.Set;

public interface UserRepository {

    User save(User user);

    Optional<User> findById(Long id);

    Optional<User> findByUsername(String username);

    Set<Role> findRolesByUserId(Long userId);

    Set<Permission> findPermissionsByRoleIds(Set<Long> roleIds);

    List<Permission> findAllPermissions();

    boolean existsByUsername(String username);
}
