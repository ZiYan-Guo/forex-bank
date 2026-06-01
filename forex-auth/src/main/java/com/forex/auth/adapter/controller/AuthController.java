package com.forex.auth.adapter.controller;

import com.forex.auth.adapter.dto.LoginReq;
import com.forex.auth.adapter.dto.TokenResp;
import com.forex.auth.application.service.AuthAppService;
import com.forex.common.base.annotation.RateLimit;
import com.forex.common.base.exception.BusinessException;
import com.forex.common.base.result.R;
import com.forex.common.base.result.ResultCode;
import com.forex.common.security.jwt.JwtUtil;

import io.jsonwebtoken.Claims;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;

import lombok.RequiredArgsConstructor;

import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.concurrent.TimeUnit;

/**
 * Authentication REST controller. Provides login, logout, token refresh and user info endpoints.
 * 认证REST控制器。提供登录/登出/刷新令牌/用户信息接口。
 */
@Tag(name = "认证管理")
@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthAppService authAppService;
    private final JwtUtil jwtUtil;
    private final StringRedisTemplate stringRedisTemplate;

    private static final int MAX_LOGIN_ATTEMPTS = 5;
    private static final long LOCKOUT_MINUTES = 15;
    private static final String LOGIN_ATTEMPT_PREFIX = "login:attempt:";
    private static final String LOGIN_LOCK_PREFIX = "login:lock:";

    /**
     * POST /api/auth/login. Rate-limited (10 req/60s). Auto-locks account after 5 failed attempts for 15 min.
     * 登录接口。限流10次/60秒，5次失败锁定15分钟。
     */
    @Operation(summary = "用户登录")
    @PostMapping("/login")
    @RateLimit(key = "'login:ip:'", limit = 10, windowSeconds = 60)
    public R<TokenResp> login(@Valid @RequestBody LoginReq req) {
        String lockKey = LOGIN_LOCK_PREFIX + req.getUsername();
        if (Boolean.TRUE.equals(stringRedisTemplate.hasKey(lockKey))) {
            throw new BusinessException(ResultCode.LOCK_FAILED.getCode(),
                    "账户已被临时锁定，请" + LOCKOUT_MINUTES + "分钟后重试");
        }

        try {
            String accessToken = authAppService.login(req.getUsername(), req.getPassword());
            clearLoginAttempts(req.getUsername());

            Claims claims = jwtUtil.parseToken(accessToken);
            String refreshToken = jwtUtil.generateRefreshToken(
                    jwtUtil.getUserId(claims), jwtUtil.getUsername(claims));

            TokenResp resp = new TokenResp();
            resp.setAccessToken(accessToken);
            resp.setRefreshToken(refreshToken);
            resp.setExpiresIn(30 * 60);
            resp.setUserId(jwtUtil.getUserId(claims));
            resp.setUsername(jwtUtil.getUsername(claims));
            resp.setRealName(jwtUtil.getUsername(claims));

            return R.ok("登录成功", resp);
        } catch (IllegalArgumentException e) {
            int attempts = recordLoginAttempt(req.getUsername());
            if (attempts >= MAX_LOGIN_ATTEMPTS) {
                lockAccount(req.getUsername());
                throw new BusinessException(ResultCode.LOCK_FAILED.getCode(),
                        "密码错误次数过多，账户已锁定" + LOCKOUT_MINUTES + "分钟");
            }
            throw e;
        }
    }

    /**
     * POST /api/auth/refresh. Refreshes token with rotation (new refresh token issued).
     * 刷新令牌，每次签发新的refresh token。
     */
    @Operation(summary = "刷新令牌")
    @PostMapping("/refresh")
    @RateLimit(key = "'refresh:ip:'", limit = 20, windowSeconds = 60)
    public R<TokenResp> refresh(@RequestHeader("Authorization") String authHeader) {
        String refreshToken = authHeader.replace("Bearer ", "");

        if (authAppService.isTokenBlacklisted(refreshToken)) {
            throw new BusinessException(ResultCode.UNAUTHORIZED.getCode(), "Token 已失效，请重新登录");
        }

        String accessToken = authAppService.refreshToken(refreshToken);
        String newRefreshToken = jwtUtil.generateRefreshToken(
                jwtUtil.getUserId(jwtUtil.parseToken(accessToken)),
                jwtUtil.getUsername(jwtUtil.parseToken(accessToken)));

        Claims claims = jwtUtil.parseToken(accessToken);
        TokenResp resp = new TokenResp();
        resp.setAccessToken(accessToken);
        resp.setRefreshToken(newRefreshToken);
        resp.setExpiresIn(30 * 60);
        resp.setUserId(jwtUtil.getUserId(claims));
        resp.setUsername(jwtUtil.getUsername(claims));
        resp.setRealName(jwtUtil.getUsername(claims));

        return R.ok(resp);
    }

    /**
     * POST /api/auth/logout. Adds token to blacklist.
     * 登出，令牌加入黑名单。
     */
    @Operation(summary = "用户登出")
    @PostMapping("/logout")
    public R<Void> logout(@RequestHeader("Authorization") String authHeader) {
        String token = authHeader.replace("Bearer ", "");
        authAppService.logout(token);
        R<Void> r = R.ok();
        r.setMessage("登出成功");
        return r;
    }

    @Operation(summary = "获取当前用户信息")
    @GetMapping("/me")
    public R<TokenResp> me(@RequestHeader("Authorization") String authHeader) {
        String token = authHeader.replace("Bearer ", "");
        Claims claims = jwtUtil.parseToken(token);

        TokenResp resp = new TokenResp();
        resp.setUserId(jwtUtil.getUserId(claims));
        resp.setUsername(jwtUtil.getUsername(claims));
        resp.setRealName(jwtUtil.getUsername(claims));

        return R.ok(resp);
    }

    private int recordLoginAttempt(String username) {
        String key = LOGIN_ATTEMPT_PREFIX + username;
        Long attempts = stringRedisTemplate.opsForValue().increment(key);
        stringRedisTemplate.expire(key, LOCKOUT_MINUTES, TimeUnit.MINUTES);
        return attempts != null ? attempts.intValue() : 0;
    }

    private void clearLoginAttempts(String username) {
        stringRedisTemplate.delete(LOGIN_ATTEMPT_PREFIX + username);
        stringRedisTemplate.delete(LOGIN_LOCK_PREFIX + username);
    }

    private void lockAccount(String username) {
        stringRedisTemplate.opsForValue().set(LOGIN_LOCK_PREFIX + username, "1",
                LOCKOUT_MINUTES, TimeUnit.MINUTES);
    }
}
