package com.forex.auth.domain.service;

import com.forex.auth.domain.model.aggregate.Role;
import com.forex.auth.domain.model.aggregate.User;
import com.forex.auth.domain.repository.UserRepository;

import cn.hutool.crypto.digest.BCrypt;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import org.springframework.stereotype.Service;

import java.util.Set;
import org.springframework.transaction.annotation.Transactional;
import com.forex.common.base.exception.BusinessException;
import com.forex.common.base.result.ResultCode;

@Slf4j
@Service
@RequiredArgsConstructor
/** User authentication domain service. 用户认证领域服务。 */
@Transactional
public class AuthenticationDomainService {

    private static final int BCRYPT_ROUNDS = 12;
    private final UserRepository userRepository;

    /** Authenticate user credentials. Uses BCrypt(12 rounds) for password verification. 使用BCrypt(12轮)验证密码。 */
    public User authenticate(String username, String rawPassword) {
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new BusinessException(ResultCode.VALIDATE_FAIL, "用户名或密码错误"));

        if (!user.isActive()) {
            throw new BusinessException(ResultCode.VALIDATE_FAIL, "账户已被禁用");
        }

        if (!BCrypt.checkpw(rawPassword, user.getPassword())) {
            throw new BusinessException(ResultCode.VALIDATE_FAIL, "用户名或密码错误");
        }

        Set<Role> roles = userRepository.findRolesByUserId(user.getId());
        Set<Long> roleIds = roles.stream().map(Role::getId).collect(java.util.stream.Collectors.toSet());
        roles.forEach(r -> r.getPermissions().addAll(userRepository.findPermissionsByRoleIds(roleIds)));

        user.assignRoles(roles);
        return user;
    }

    /** Encode raw password with BCrypt. 使用BCrypt加密密码。 */
    public static String encodePassword(String rawPassword) {
        return BCrypt.hashpw(rawPassword, BCrypt.gensalt(BCRYPT_ROUNDS));
    }
}
