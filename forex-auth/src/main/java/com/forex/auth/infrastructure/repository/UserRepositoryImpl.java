package com.forex.auth.infrastructure.repository;

import com.forex.auth.domain.model.aggregate.Permission;
import com.forex.auth.domain.model.aggregate.Role;
import com.forex.auth.domain.model.aggregate.User;
import com.forex.auth.domain.repository.UserRepository;
import com.forex.auth.infrastructure.mapper.UserMapper;
import com.forex.auth.infrastructure.persistence.PermissionPO;
import com.forex.auth.infrastructure.persistence.RolePO;
import com.forex.auth.infrastructure.persistence.UserPO;

import lombok.RequiredArgsConstructor;

import org.springframework.stereotype.Repository;

import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

@Repository
@RequiredArgsConstructor
public class UserRepositoryImpl implements UserRepository {

    private final UserMapper userMapper;

    @Override
    public User save(User user) {
        UserPO po = toUserPO(user);
        if (user.getId() == null) {
            userMapper.insert(po);
        } else {
            userMapper.updateById(po);
        }
        return toUser(po);
    }

    @Override
    public Optional<User> findById(Long id) {
        UserPO po = userMapper.selectById(id);
        return Optional.ofNullable(po).map(this::toUser);
    }

    @Override
    public Optional<User> findByUsername(String username) {
        UserPO po = userMapper.selectByUsername(username);
        return Optional.ofNullable(po).map(this::toUser);
    }

    @Override
    public Set<Role> findRolesByUserId(Long userId) {
        List<RolePO> rolePOs = userMapper.selectRolesByUserId(userId);
        return rolePOs.stream().map(this::toRole).collect(Collectors.toSet());
    }

    @Override
    public Set<Permission> findPermissionsByRoleIds(Set<Long> roleIds) {
        if (roleIds.isEmpty()) return Set.of();
        List<PermissionPO> permPOs = userMapper.selectPermissionsByRoleIds(List.copyOf(roleIds));
        return permPOs.stream().map(this::toPermission).collect(Collectors.toSet());
    }

    @Override
    public List<Permission> findAllPermissions() {
        return userMapper.selectAllPermissions().stream().map(this::toPermission).toList();
    }

    @Override
    public boolean existsByUsername(String username) {
        return userMapper.selectByUsername(username) != null;
    }

    private User toUser(UserPO po) {
        return User.reconstitute(po.getId(), po.getUsername(), po.getPassword(),
                po.getRealName(), po.getEmail(), po.getPhone(), po.getStatus());
    }

    private UserPO toUserPO(User user) {
        UserPO po = new UserPO();
        po.setId(user.getId());
        po.setUsername(user.getUsername());
        po.setPassword(user.getPassword());
        po.setRealName(user.getRealName());
        po.setEmail(user.getEmail());
        po.setPhone(user.getPhone());
        po.setStatus(user.isActive() ? 1 : 0);
        return po;
    }

    private Role toRole(RolePO po) {
        return new Role(po.getId(), po.getRoleCode(), po.getRoleName(), new HashSet<>());
    }

    private Permission toPermission(PermissionPO po) {
        return new Permission(po.getId(), po.getPermCode(), po.getPermName(),
                po.getPermType(), po.getParentCode(), po.getPath());
    }
}
