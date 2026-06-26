package com.forex.auth.application.service;

import com.forex.auth.domain.model.aggregate.User;
import com.forex.auth.domain.service.AuthenticationDomainService;
import com.forex.common.security.jwt.JwtUtil;

import lombok.RequiredArgsConstructor;

import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.util.DigestUtils;

import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.concurrent.TimeUnit;
import com.forex.common.base.exception.BusinessException;
import com.forex.common.base.result.ResultCode;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
/** Auth application service. Orchestrates login, token refresh, and logout. 认证应用服务。编排登录/刷新/登出用例。 */
@Transactional
public class AuthAppService {

    private final AuthenticationDomainService authDomainService;
    private final JwtUtil jwtUtil;
    private final StringRedisTemplate stringRedisTemplate;

    private static final long ACCESS_TOKEN_EXPIRE_SECONDS = 30 * 60;
    private static final String TOKEN_BLACKLIST_PREFIX = "token:blacklist:";

    /** User login. Validates credentials and returns access token. 用户登录，验证凭证返回令牌。 */
    public String login(String username, String password) {
        User user = authDomainService.authenticate(username, password);

        List<String> roles = user.getRoles().stream()
                .map(r -> r.getRoleCode())
                .toList();
        List<String> permissions = user.getPermissions().stream()
                .map(p -> p.getPermCode())
                .toList();

        return jwtUtil.generateAccessToken(user.getId(), user.getUsername(), roles, permissions);
    }

    /** Refresh access token. Checks blacklist before issuing new token. 刷新令牌，检查黑名单后签发新令牌。 */
    public String refreshToken(String refreshToken) {
        if (isTokenBlacklisted(refreshToken)) {
            throw new BusinessException(ResultCode.VALIDATE_FAIL, "Token 已失效");
        }
        var claims = jwtUtil.parseToken(refreshToken);
        Long userId = jwtUtil.getUserId(claims);
        String username = jwtUtil.getUsername(claims);

        return jwtUtil.generateAccessToken(userId, username, jwtUtil.getRoles(claims), jwtUtil.getPermissions(claims));
    }

    /** Logout. Adds token to blacklist via hashed key. 登出，将token加入黑名单(hash后存储)。 */
    public void logout(String token) {
        String hash = hashToken(token);
        stringRedisTemplate.opsForValue().set(
                TOKEN_BLACKLIST_PREFIX + hash, "1",
                ACCESS_TOKEN_EXPIRE_SECONDS, TimeUnit.SECONDS);
    }

    /** Check if token is revoked. 检查令牌是否已吊销。 */
    public boolean isTokenBlacklisted(String token) {
        return Boolean.TRUE.equals(stringRedisTemplate.hasKey(TOKEN_BLACKLIST_PREFIX + hashToken(token)));
    }

    /** Hash token for compact blacklist key. 对token做hash用于黑名单存储。 */
    private String hashToken(String token) {
        return DigestUtils.md5DigestAsHex(token.getBytes(StandardCharsets.UTF_8));
    }
}
