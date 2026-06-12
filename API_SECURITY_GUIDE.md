# API 安全增强指南

## 📚 目录
1. [权限控制](#权限控制)
2. [限流防护](#限流防护)
3. [输入验证](#输入验证)
4. [响应头安全](#响应头安全)
5. [SQL 注入防护](#sql-注入防护)
6. [请求/响应加密](#请求响应加密)
7. [安全最佳实践](#安全最佳实践)

---

## 权限控制

### 注解定义
```java
@RequirePermission(
    value = {"exchange:create", "exchange:view"},
    mode = PermissionMode.ANY,
    message = "当前用户无权执行此操作"
)
```

### 使用示例

#### 1. 单权限检查
```java
@PostMapping("/exchange/create")
@RequirePermission("exchange:create")
public R<ExchangeOrder> createOrder(@RequestBody CreateOrderCmd cmd) {
    // 仅拥有 exchange:create 权限的用户可以调用
    return exchangeService.createOrder(cmd);
}
```

#### 2. 多权限检查 (任意一个)
```java
@PostMapping("/payment/approve")
@RequirePermission(
    value = {"payment:approve", "payment:admin_approve"},
    mode = PermissionMode.ANY
)
public R<PaymentResult> approvePayment(@RequestBody PaymentApprovalCmd cmd) {
    // 拥有任意一个权限即可
    return paymentService.approve(cmd);
}
```

---

## 限流防护

### 限流类型
- **USER**: 基于用户限流
- **IP**: 基于 IP 限流
- **API**: 基于 API 端点限流
- **USER_IP**: 基于用户 + IP 组合限流

### 使用示例
```java
@PostMapping("/auth/login")
@RateLimit(
    type = RateLimitType.IP,
    windowSeconds = 300,
    maxRequests = 5,
    message = "登录尝试过多，请稍后再试"
)
public R<LoginResponse> login(@RequestBody LoginCmd cmd) {
    return authService.login(cmd);
}
```

---

## 输入验证

### 使用示例
```java
@PostMapping("/account/open")
public R<ForexAccount> openAccount(
    @ValidateInput(
        pattern = "^[A-Z]{2}\\d{3,4}$",
        message = "账户号码格式不符合要求"
    ) @RequestParam String accountNo) {
    return accountService.open(accountNo);
}
```

---

## 响应头安全

### 自动添加的安全响应头

| 响应头 | 值 | 说明 |
|--------|-----|------|
| X-Content-Type-Options | nosniff | 防止 MIME 类型嗅探 |
| X-Frame-Options | DENY | 防止点击劫持 |
| X-XSS-Protection | 1; mode=block | 启用浏览器 XSS 保护 |
| Strict-Transport-Security | max-age=... | 强制使用 HTTPS |
| Content-Security-Policy | ... | 内容安全策略 |
| Cache-Control | no-cache, no-store... | 防止缓存敏感信息 |
| Referrer-Policy | strict-origin-when-cross-origin | 控制 referrer 信息 |
| Permissions-Policy | geolocation=()... | 特性策略 |

---

## SQL 注入防护

### 自动检测的攻击模式
- SQL 关键字: UNION, SELECT, INSERT, UPDATE, DELETE, DROP
- 脚本标签: SCRIPT, JAVASCRIPT, ONERROR
- SQL 注释: --, /*, */
- 常见函数: xp_, sp_

### 防护示例

#### ✅ 正确用法（参数化查询）
```java
// MyBatis-Plus 自动参数化
@Repository
public interface ExchangeOrderMapper extends BaseMapper<ExchangeOrderPO> {
    @Select("SELECT * FROM t_exchange_order WHERE order_no = #{orderNo}")
    ExchangeOrderPO findByOrderNo(@Param("orderNo") String orderNo);
}
```

---

## 安全最佳实践

### 1. 最小权限原则
```java
// ✅ 正确：只授予必要的权限
@RequirePermission("exchange:view")
public List<Exchange> listExchanges() { }
```

### 2. 深度防护
```java
// 结合多个安全措施
@PostMapping("/payment/submit")
@RequirePermission("payment:submit")
@RateLimit(type = RateLimitType.USER, maxRequests = 5)
@AuditLog(operation = "SUBMIT_PAYMENT")
@Idempotent
public R<PaymentResult> submitPayment(
    @ValidateInput(allowNull = false) @RequestBody PaymentCmd cmd) {
    return paymentService.submit(cmd);
}
```

---

生成时间: 2026-06-12
