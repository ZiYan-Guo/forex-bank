# 银行外汇系统 (Forex Bank System)

> 银行国际业务综合管理平台 — 覆盖结售汇、外汇买卖、跨境支付、国际结算、风险监测、监管报送等全业务流程。

> **International Business Management Platform** — covering forex exchange, FX trading, cross-border payment, international settlement, risk monitoring, regulatory reporting and more.

---

## 📋 技术栈 (Tech Stack)

### 后端 (Backend)
| 组件 | 版本 |
|------|------|
| JDK | 17 (Eclipse Adoptium) |
| Spring Boot | 3.2.5 |
| Spring Cloud | 2023.0.3 |
| Spring Cloud Alibaba | 2023.0.1.0 |
| MyBatis-Plus | 3.5.5 |
| MySQL | 8.0.35 |
| Redis (Redisson) | 3.27.0 |
| Caffeine | 3.1.8 |
| Nacos | 2.3.2 |
| Sentinel | 1.8.6 |
| Seata (AT Mode) | 1.8.0 |
| RocketMQ | 5.1.4 |
| Flowable | 7.0.0 |
| XXL-JOB | 2.4.1 |
| JWT (jjwt) | 0.12.3 |
| Bouncy Castle (国密) | 1.77 |
| Knife4j (Swagger) | 4.5.0 |
| Hutool | 5.8.25 |
| MapStruct | 1.5.5 |

### 前端 (Frontend)
| 组件 | 版本 |
|------|------|
| Vue | 3.4.21 |
| TypeScript | 5.4 |
| Ant Design Vue | 4.2.1 |
| Pinia | 2.1.7 |
| ECharts + vue-echarts | 5.5.0 / 6.6.9 |
| Axios | 1.6.8 |
| Vite | 5.2.0 |

### 基础设施 (Infrastructure)
| 组件 | 用途 |
|------|------|
| Docker Compose | 本地开发环境 |
| MySQL 8.0 | 持久化存储 |
| Redis 7 | 缓存/分布式锁 |
| Nacos | 服务注册发现 + 配置中心 |
| Seata Server | 分布式事务协调器 |
| RocketMQ | 消息队列/异步解耦 |

---

## 🏗️ 架构设计 (Architecture)

### 总体架构
```
┌──────────────────────────────────────────────────────────┐
│                      渠道层 (Channel)                     │
│         企业网银 / 手机银行 / 柜面 / 银企直连              │
└──────────────────────┬───────────────────────────────────┘
                       │
┌──────────────────────┴───────────────────────────────────┐
│                API Gateway (:8080)                       │
│      路由 / 鉴权 / 限流 / 安全头 / 跨域                    │
└──────────────────────┬───────────────────────────────────┘
                       │
        ┌──────────────┼──────────────────────────────┐
        │              │                              │
   ┌────┴────┐   ┌─────┴─────┐                 ┌──────┴──────┐
   │ 前台     │   │   中台     │                 │    后台      │
   │exchange │   │  risk     │                 │ bookkeeping │
   │ trading │   │ valuation │                 │  payment    │
   └─────────┘   │  margin   │                 │ settlement  │
                 │ position  │                 │  clearing   │
                 └───────────┘                 │  reporting  │
                                               └─────────────┘
        ┌──────────────┬──────────────┬──────────────┐
   ┌────┴────┐   ┌─────┴─────┐  ┌─────┴─────┐  ┌────┴─────┐
   │customer │   │  account  │  │   rate    │  │   auth   │
   └─────────┘   └───────────┘  └───────────┘  └──────────┘
```

### DDD 四层架构 (每个微服务内部)
```
adapter (入站适配器)
  ├── controller/     REST API
  ├── message/        MQ 消费者
  └── dto/            请求/响应 DTO
        │
application (应用层)
  ├── service/        AppService (用例编排)
  ├── command/        CQRS 命令对象
  └── query/          CQRS 查询对象
        │
domain (领域层 - 零框架依赖)
  ├── model/aggregate/   聚合根
  ├── model/entity/      实体
  ├── model/valueobject/ 值对象
  ├── service/           领域服务
  ├── repository/        仓储接口 (Port)
  ├── event/             领域事件
  └── specification/     业务规则
        │
infrastructure (基础设施层)
  ├── repository/    RepositoryImpl
  ├── mapper/        MyBatis Mapper
  ├── persistence/   PO (持久化对象)
  ├── client/        FeignClient (ACL 防腐层)
  ├── config/        模块配置
  └── event/         事件发布/消费
```

---

## 📁 项目结构 (Project Structure)

```
forex-bank-system/
│
├── pom.xml                          # 父 POM (版本仲裁/依赖管理)
│
├── forex-common/                    # 共享内核
│   ├── forex-common-base/           # DDD基类 + R<T> + @RedisLock/@Idempotent/@RateLimit
│   ├── forex-common-security/       # JWT + 权限注解 + 国密 SM2/3/4
│   └── forex-common-mybatis/        # MyBatis-Plus + BasePO + 加密TypeHandler
│
├── forex-gateway/          :8080    # API 网关 (Gateway + Sentinel)
├── forex-auth/             :8101    # 认证授权 (登录/登出/Token刷新/RBAC)
│
├── forex-exchange/         :8201    # 结售汇业务 (即期/远期/挂单/锁价)
├── forex-trading/          :8202    # 外汇买卖 (即期/远期/掉期/期权)
├── forex-valuation/        :8203    # 衍生品估值 (BS/GK模型/盯市)
├── forex-margin/           :8204    # 保证金管理 (初始/变动/追缴)
├── forex-position/         :8205    # 敞口管理 (限额/预警)
├── forex-bookkeeping/      :8206    # 簿记核算 (分录/过账/冲正)
├── forex-payment/          :8207    # 跨境支付 (SWIFT/CIPS/GPI)
├── forex-settlement/       :8208    # 国际结算 (信用证/托收/保函)
├── forex-clearing/         :8209    # 清算 (轧差/代理行对账)
├── forex-risk/             :8210    # 风险监测 (规则引擎/反洗钱/可疑交易)
├── forex-reporting/        :8211    # 监管报送 (国际收支/结售汇/资本项目)
├── forex-customer/         :8212    # 客户管理 (建档/尽调/额度)
├── forex-account/          :8213    # 账户管理 (开立/存取/冻结)
├── forex-rate/             :8214    # 汇率管理 (牌价/缓存/发布)
├── forex-workflow/         :8215    # 工作流 (Flowable 审批)
├── forex-notification/     :8216    # 通知公告
├── forex-ocr/              :8217    # OCR 识别
├── forex-schedule/         :8218    # 定时任务 (XXL-JOB)
│
├── sql/                              # 58张数据库表
├── docker/                           # Docker Compose 环境
└── forex-bank-web/                   # 前端项目 (Vue3+TS)
    └── src/views/                    # 23个页面
```

---

## 🚀 快速启动 (Quick Start)

### 环境要求
- JDK 17+
- Maven 3.9+
- Node.js 18+
- Docker & Docker Compose
- MySQL 8.0 (或使用 Docker)

### 1. 启动基础设施
```bash
cd docker
docker-compose up -d
# 启动: MySQL(:3306) + Redis(:6379) + Nacos(:8848) + Seata(:8091) + RocketMQ(:9876)
```

### 2. 初始化数据库
```bash
# 创建所有数据库
mysql -u root -p < sql/init-all.sql

# 导入认证模块表结构和种子数据
mysql -u root -p < sql/auth/init.sql

# 导入其他模块表结构...
# mysql -u root -p < sql/exchange/init.sql
# mysql -u root -p < sql/payment/init.sql
# ... (按需导入)
```

### 3. 启动后端服务
```bash
# 编译
mvn clean install -DskipTests

# 逐个启动 (或使用 IDE 批量启动)
cd forex-gateway && mvn spring-boot:run    # :8080
cd forex-auth && mvn spring-boot:run       # :8101
# ... 启动其他模块
```

### 4. 启动前端
```bash
cd forex-bank-web
npm install
npm run dev      # :3000 → 代理后端 :8080
```

### 5. 访问
```
前端:    http://localhost:3000
网关:    http://localhost:8080
Nacos:   http://localhost:8848/nacos
Swagger: http://localhost:8080/doc.html
```

### 默认账户
```
用户名: admin
密码:   admin123
```

---

## 🔐 安全特性 (Security)

| 特性 | 实现 |
|------|------|
| 密码加密 | BCrypt (12轮) / BCrypt 12-round hashing |
| JWT 签名 | HMAC-SHA256，密钥 ≥256 bits / 256-bit minimum key |
| 登录保护 | 限流 10次/60秒 + 5次失败锁定15分钟 / Rate-limited with account lockout |
| Token 管理 | 黑名单 + Refresh Token 轮换 / Blacklist + Token Rotation |
| 权限控制 | RBAC (角色-权限) + `@RequireRole`/`@RequirePermission` AOP |
| 安全响应头 | X-Content-Type-Options, X-Frame-Options, XSS-Protection, HSTS, CSP |
| 国密支持 | SM2/SM3/SM4 (基于 Bouncy Castle) |
| 分布式锁 | `@RedisLock` 注解 (Redisson) |
| 幂等保护 | `@Idempotent` 注解 (Redis SETNX) |
| 接口限流 | `@RateLimit` 注解 + Sentinel Gateway |

---

## 📊 数据库 (Database)

58 张业务表，按模块分库：

| 数据库 | 表数 | 核心表 |
|--------|-----|--------|
| forex_auth | 5 | sys_user, sys_role, sys_permission, sys_user_role, sys_role_permission |
| forex_customer | 3 | t_customer, t_customer_credit_limit, t_customer_quota |
| forex_exchange | 3 | t_exchange_order, t_exchange_quote, t_exchange_position |
| forex_trading | 3 | t_fx_trade, t_trade_position, t_trade_lifecycle |
| forex_payment | 4 | t_cross_border_payment, t_payment_blacklist_hit, t_payment_reconciliation |
| forex_settlement | 3 | t_letter_of_credit, t_documentary_collection, t_bank_guarantee |
| forex_account | 3 | t_forex_account, t_account_transaction, t_account_balance_snapshot |
| forex_rate | 3 | t_exchange_rate, t_rate_publish_config, t_rate_publish_log |
| forex_risk | 3 | t_risk_monitor_log, t_monitor_rule, t_risk_report |
| forex_reporting | 4 | t_balance_of_payment, t_forex_settlement_report, t_capital_account_report, t_regulatory_submit_log |
| forex_bookkeeping | 3 | t_journal_entry, t_account_subject, t_ledger |
| forex_clearing | 2 | t_clearing_instruction, t_settlement_batch |
| forex_margin | 2 | t_margin_account, t_margin_call |
| forex_valuation | 2 | t_valuation_result, t_valuation_model_config |
| forex_position | 2 | t_position, t_position_limit_config |
| forex_workflow | 2 | t_workflow_task, t_approval_record |
| forex_notification | 2 | t_notification, t_notice |
| forex_ocr | 1 | t_ocr_task |
| forex_schedule | 2 | t_schedule_job, t_job_log |

---

## 🔧 API 概览 (API Overview)

150+ REST API，全部通过 Gateway (:8080) 统一入口。

| 模块 | 前缀 | 端点数 | 说明 |
|------|------|--------|------|
| 认证 | `/api/auth` | 4 | 登录/登出/刷新令牌/用户信息 |
| 客户 | `/api/customer` | 8 | CRUD/风险评级/尽调/额度 |
| 结售汇 | `/api/exchange` | 9 | 创建订单/锁价/确认/取消/冲正/报价 |
| 外汇买卖 | `/api/trading` | 12 | 即期/远期/掉期/期权/展期/平仓 |
| 跨境支付 | `/api/payment` | 10 | 汇出/汇入/提交/审批/AML/发送/GPI |
| 信用证 | `/api/settlement/lc` | 9 | 开立/修改/通知/交单/审单/承兑/付款 |
| 托收 | `/api/settlement/collection` | 3 | 创建/查询/付款 |
| 保函 | `/api/settlement/guarantee` | 3 | 创建/查询/开立 |
| 风险监测 | `/api/risk` | 6 | 评估/日志/报告/提交 |
| 监管报送 | `/api/reporting` | 6 | 国际收支/结售汇/资本项目/批量提交 |
| 账户 | `/api/account` | 9 | 开户/销户/存取/冻结/流水 |
| 汇率 | `/api/rate` | 5 | 查询/换算/保存/发布(公开) |
| 敞口 | `/api/position` | 6 | CRUD/汇总/超限检查 |
| 估值 | `/api/valuation` | 3 | 计算/查询/重估 |
| 保证金 | `/api/margin` | 6 | 创建/追缴/释放/存入 |
| 簿记 | `/api/bookkeeping` | 6 | 分录创建/过账/冲正/日终 |
| 清算 | `/api/clearing` | 7 | 生成/发送/回执/结算/取消 |
| 工作流 | `/api/workflow` | 5 | 发起/审批/任务列表 |
| 通知 | `/api/notification` | 4 | 发送/公告管理 |
| OCR | `/api/ocr` | 4 | 上传/识别/结果查询 |
| 定时任务 | `/api/schedule` | 6 | 任务管理/日志查看 |

---

## 🧪 安全审计 (Security Audit)

系统经过 5 批全维度自测，修复 23 项安全问题：

| 类别 | 已修复 |
|------|--------|
| 密码安全 | MD5 → BCrypt(12轮) |
| JWT 安全 | 弱密钥 → ≥32字符 + 启动校验 |
| 暴力破解 | 无防护 → 限流 + 5次锁定 |
| Token 管理 | 无黑名单检查 → Hash Key + Rotation |
| 安全头 | 缺失 → 7个安全响应头 |
| SQL注入 | MyBatis-Plus 参数化查询 |
| XXE | Jackson 默认禁用外部实体 |

---

## 📝 开发规范 (Development Convention)

- **响应格式**: 统一 `R<T>` (code/message/data/timestamp/traceId)
- **异常处理**: `BusinessException` + `GlobalExceptionHandler`
- **分页**: `PageReq` (pageNum/pageSize) → `PageResp<T>` (total/records)
- **状态机**: 所有状态变更仅在聚合根内部方法完成
- **工厂方法**: `create()` 新建 / `reconstitute()` 重建
- **乐观锁**: `version` 字段 + MyBatis-Plus `@Version`
- **逻辑删除**: `deleted` + MyBatis-Plus `@TableLogic`
- **API文档**: Knife4j (Swagger) 自动生成

---

## 📄 License

Internal project — 银行内部系统

---

## 👥 角色设计 (User Roles)

| 角色 | 英文 | 主要功能 |
|------|------|---------|
| 系统管理员 | ROLE_ADMIN | 全部权限 |
| 外汇交易员 | ROLE_TRADER | 汇率报价、交易确认、敞口管理 |
| 国际结算操作员 | ROLE_SETTLEMENT_OPERATOR | 信用证开立/通知、托收处理、单证审核 |
| 合规审核岗 | ROLE_COMPLIANCE | 客户尽调、风险筛查、可疑交易报告审核 |
| 财务/会计岗 | ROLE_ACCOUNTANT | 账务核算、对账管理、报表报送 |
| 客户经理 | ROLE_CUSTOMER_MANAGER | 客户开户、产品推介、业务查询 |
| 支行柜员 | ROLE_TELLER | 柜面结售汇、汇款受理、业务查询 |
