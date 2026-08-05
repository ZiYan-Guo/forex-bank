# 银行外汇系统前后端逻辑、架构缺口与产品补齐分析

> Analysis date / 分析日期: 2026-08-05  
> Project / 项目: `forex-bank-system`  
> Scope / 范围: 前后端代码、基础设施、中间件、数据模型、性能、产品能力和市场需求  
> Git status / Git 状态: 本文档仅用于分析，不参与 Git 提交。

## 0. 分析说明

本次分析同时使用 Understand Anything 知识图谱和当前源码扫描结果。

- Knowledge graph / 知识图谱: `.understand-anything/knowledge-graph.json`
- Graph analyzed at / 图谱分析时间: 2026-07-09
- Graph commit / 图谱绑定提交: `e7429dc57a7d282a5eb3fcea129b28a5bb7202b9`
- Current HEAD / 当前提交: `71f3e89e544c15e1df762809209f5444923bbe07`
- Working tree / 工作区: 分析开始时无未提交源码改动

图谱提交与当前 HEAD 之间存在项目文件变化，尤其涉及 `forex-rate`、`forex-risk`、`forex-schedule`、前端接口及部分页面。因此，本文以当前源码扫描为主、知识图谱为架构关系补充。若后续需要让图谱完全同步，应重新执行 `/understand`。

## 1. 当前系统总体逻辑

### 1.1 技术架构定位

项目定位为银行国际业务综合管理平台，采用 Java 17、Spring Boot 3、Spring Cloud Alibaba、MyBatis-Plus、MySQL、Redis/Redisson、Caffeine、Nacos、Sentinel、Seata、RocketMQ、Flowable、XXL-JOB，以及 Vue 3、TypeScript、Ant Design Vue、Pinia、ECharts。

从业务域看，系统已经划分出以下几组能力：

| 业务层 | 主要模块 | 作用 |
| --- | --- | --- |
| 统一入口 | `forex-gateway`、`forex-auth` | 路由、鉴权、RBAC、JWT、限流和安全响应头 |
| 主数据和账户 | `forex-customer`、`forex-account`、`forex-limit` | 客户、尽调、额度、账户、余额和流水 |
| 市场前台 | `forex-rate`、`forex-exchange`、`forex-trading`、`forex-precious-metal` | 牌价、结售汇、外汇交易、贵金属交易 |
| 风险中台 | `forex-risk`、`forex-position`、`forex-margin`、`forex-valuation`、`forex-saccr` | 风险规则、敞口、保证金、估值和资本计量 |
| 交易后和后台 | `forex-payment`、`forex-settlement`、`forex-clearing`、`forex-bookkeeping`、`forex-reporting` | 跨境支付、国际结算、清算、簿记和监管报送 |
| 运营支撑 | `forex-workflow`、`forex-notification`、`forex-ocr`、`forex-schedule` | 审批、通知、OCR、批处理和调度 |
| 新能力 | `forex-ai`、`forex-cashpool`、`forex-supply-chain`、`forex-hedge-accounting` | AI 助手、现金池、供应链融资和套期会计 |

### 1.2 后端请求链

当前后端主链路：

```text
Vue 页面
  -> Axios request 封装
  -> Spring Cloud Gateway
  -> AuthGlobalFilter / SecurityHeadersFilter / RouteRateLimiter
  -> Controller / DTO
  -> Application Service / Command / Query
  -> Domain Aggregate / Domain Service / Repository Port
  -> MyBatis Mapper / RepositoryImpl
  -> MySQL
```

部分写操作还会经过：

```text
IdempotentAspect -> Redis SETNX
RedisLockAspect -> Redisson Distributed Lock
RateLimitAspect -> Redisson Rate Limiter
ApplicationEventPublisher -> Local Spring Event Listener
```

当前代码的优点是边界意识比较清晰，Controller、Application、Domain、Infrastructure 目录结构与 README 中的 DDD 设计一致。主要问题是跨服务异步化和外部系统连接尚未形成可运行闭环。

### 1.3 前端逻辑

前端采用 Vue 3 + TypeScript + Pinia：

```text
Router
  -> MainLayout
  -> Business View
  -> api/*.ts
  -> Axios request interceptor
  -> Gateway API
```

已经存在的前端能力：

- 登录、Token 保存、退出和刷新；
- 客户、账户、交易、支付、结算、清算、风险、报送、工作流、调度等 API 封装；
- `rate` Pinia Store 定时拉取最新牌价；
- 主布局展示用户信息和牌价 ticker；
- 多个业务页面已经具备列表、查询、详情、创建、审批和状态操作界面。

但前端仍存在真实 API 与模拟数据混用的问题。以下页面或功能仍能看到 mock、模拟延时或前端静态数据：

- `forex-bank-web/src/views/bookkeeping/JournalEntry.vue`
- `forex-bank-web/src/views/clearing/ClearingList.vue`
- `forex-bank-web/src/views/clearing/ConfirmationBoard.vue`
- `forex-bank-web/src/views/clearing/ReconciliationBoard.vue`
- `forex-bank-web/src/views/clearing/SettlementTracker.vue`
- `forex-bank-web/src/views/margin/MarginList.vue`
- `forex-bank-web/src/views/settlement/BankGuarantee.vue`
- `forex-bank-web/src/views/settlement/DocumentaryCollection.vue`
- `forex-bank-web/src/views/payment/BatchPayment.vue`
- `forex-bank-web/src/views/cashpool/OverseasLending.vue`
- `forex-bank-web/src/views/ocr/OcrUpload.vue`
- AI 页面中的部分推荐、OCR 和对话流程

这意味着当前前端更接近“业务工作台原型 + 部分真实接口”，还不是完整的生产运营前端。

## 2. 中间件和基础设施现状

### 2.1 已经落地的能力

| 能力 | 当前实现 | 结论 |
| --- | --- | --- |
| Redis/Redisson | 分布式锁、幂等、限流、登录失败次数和 Token 黑名单 | 基础能力已落地，但缺少统一缓存规范和一致性治理 |
| Caffeine | `forex-rate` 的牌价本地缓存，约 10 秒有效性判断 | 仅有局部 L1 缓存，未形成 L1 + L2 体系 |
| Gateway 限流 | `RequestRateLimiter` + Redis，路由级静态配置 | 有入口保护，但粒度和动态治理不足 |
| Spring Event | 多个领域事件和本地监听器 | 适合单体进程内解耦，不适合跨微服务可靠投递 |
| XXL-JOB | Executor、任务 Handler、任务日志表、任务管理 API | 接入存在，但部分业务执行仍是演示逻辑 |
| MySQL 约束 | 多数核心单号有唯一索引，部分查询有状态和时间索引 | 基础数据约束较好，但需要结合真实查询做复合索引审计 |
| DDD 分层 | 多数服务都有 adapter/application/domain/infrastructure | 结构基础较好，需要继续补齐跨域契约和基础设施实现 |

### 2.2 依赖存在但业务闭环不完整的能力

| 能力 | 当前观察 | 主要缺口 |
| --- | --- | --- |
| RocketMQ | Docker、父 POM 和 `RocketMqConsumerConfig` 存在；风险模块配置是占位 | 没有统一事件模型、生产者、消费者、Topic、重试、死信、幂等和消息监控 |
| Seata | Docker 和依赖存在 | 未发现 `@GlobalTransactional` 的实际业务使用，跨服务事务策略没有明确 |
| Flowable | 父 POM 和 workflow POM 有依赖 | 未发现 `RuntimeService`、`TaskService`、`ProcessEngine` 等实际引擎调用；当前更像自建任务表流程 |
| Sentinel | Gateway 依赖和限流配置存在 | 未发现动态规则治理、业务级熔断降级、热点参数保护和统一 fallback |
| 审计日志 | `IMPROVEMENTS.md` 描述了审计日志方案 | 当前源码需要重新核实实际启用范围、持久化表、异步失败补偿和敏感字段脱敏 |
| 观测体系 | 现有日志较多 | 未见完整的 Trace、Metrics、Dashboard、告警和业务 SLA 闭环 |

## 3. 架构缺口与补齐方案

### 3.1 消息中间件：RocketMQ 需要从依赖变成业务骨干

#### 主要问题

当前系统已经定义了大量领域事件，例如交易确认、交易执行、支付提交、支付发送、汇率更新、头寸突破、保证金追缴、报送提交等，但这些事件主要通过 `ApplicationEventPublisher` 在本地进程内传播。

风险包括：

1. 服务重启后事件无法恢复；
2. 消费者异常时没有可靠重试和死信；
3. 跨微服务无法订阅同一业务事件；
4. 主事务提交成功但消息发送失败时会出现数据不一致；
5. 当前没有消息积压、消费延迟和失败率指标。

#### 推荐方案

新增共享模块，例如：

```text
forex-common/forex-common-event
forex-common/forex-common-message
```

统一事件信封至少包含：

```java
eventId, eventType, eventVersion, sourceService,
aggregateType, aggregateId, businessNo, idempotencyKey,
traceId, tenantId, operatorId, occurredAt, payload
```

同时采用：

- Outbox Pattern：业务主事务内写业务表和 `t_event_outbox`，由可靠投递器发送 RocketMQ；
- Inbox Pattern：消费者先落 `t_event_inbox`，通过唯一键避免重复消费；
- 重试策略：指数退避、最大重试次数、死信 Topic、人工重放；
- 消费治理：消费超时、消息堆积、失败率、重试次数、死信数量全部可观测；
- 事件兼容：事件必须带版本号，不允许消费者依赖发送方内部 DTO。

建议 Topic 和 Tag：

| Topic | Tags |
| --- | --- |
| `FOREX_TRADE_EVENT` | `ORDER_CREATED`、`ORDER_CONFIRMED`、`ORDER_CANCELLED`、`TRADE_EXECUTED`、`TRADE_SETTLED` |
| `FOREX_PAYMENT_EVENT` | `PAYMENT_SUBMITTED`、`AML_PASSED`、`PAYMENT_SENT`、`ACK_RECEIVED`、`GPI_UPDATED`、`PAYMENT_RETURNED` |
| `FOREX_RISK_EVENT` | `RISK_ALERT`、`POSITION_BREACH`、`MARGIN_CALLED`、`LARGE_PAYMENT` |
| `FOREX_RATE_EVENT` | `RATE_UPDATED`、`RATE_PUBLISHED`、`RATE_EXPIRED` |
| `FOREX_REPORT_EVENT` | `REPORT_GENERATED`、`REPORT_SUBMITTED`、`REPORT_REJECTED` |
| `FOREX_CLEARING_EVENT` | `INSTRUCTION_SENT`、`ACK_RECEIVED`、`SETTLED`、`RECONCILIATION_EXCEPTION` |

第一批最值得异步化的链路：

```text
交易确认
  -> 风险检查
  -> 额度占用
  -> 头寸更新
  -> 会计分录
  -> 支付或清算
  -> 通知
  -> 报表和监管数据
```

### 3.2 缓存：从单点 Caffeine 升级为分层缓存

当前 `forex-rate` 只使用 Caffeine，适合单实例低延迟读取，但实例之间不共享，服务重启后失效，也不能直接支持多服务读写一致。

建议分层：

```text
L1: Caffeine, instance-local, low latency
L2: Redis, shared cache, cross-instance
DB: MySQL, source of truth
MQ: cache invalidation and warm-up event
```

建议缓存键：

| Key | TTL 建议 | 用途 |
| --- | --- | --- |
| `rate:latest:{currencyPair}` | 5-30 秒 | 最新牌价 |
| `rate:channel:{channelCode}:{currencyPair}` | 5-30 秒 | 渠道牌价和点差 |
| `customer:profile:{customerId}` | 5-30 分钟 | 客户摘要信息 |
| `auth:permission:{userId}` | 5-15 分钟 | 权限快照 |
| `risk:rule:active` | 1-5 分钟 | 当前生效风控规则 |
| `payment:route:{currency}:{country}:{amountTier}` | 1-10 分钟 | 支付路由候选 |
| `dict:safe:bop-code` | 1 天 | 监管和业务字典 |
| `cips:participant:{bicOrCipsId}` | 1-24 小时 | CIPS 参与者和路由信息 |

缓存治理必须同时补充：

- 空值缓存，防止缓存穿透；
- 随机 TTL，防止集中失效；
- 热点 Key 保护和单飞加载；
- 大 Key、Key 数量、命中率、回源次数指标；
- 事件驱动失效，不依赖纯 TTL；
- 牌价发布后的主动预热；
- 不把余额、额度占用、保证金扣减结果直接当作最终一致缓存；
- 缓存序列化版本，避免服务升级后反序列化失败。

### 3.3 网关、限流和容错

当前 Gateway 已配置静态 `RequestRateLimiter`，但建议继续补齐：

1. 使用 Nacos 或统一配置中心动态下发限流规则；
2. 限流维度从 IP 扩展到租户、客户、用户、渠道、接口和业务类型；
3. 对支付发送、牌价发布、批量报送等接口设置独立配额；
4. 为下游服务设置连接超时、响应超时和线程池隔离；
5. 仅对明确幂等的读或查询类请求做自动重试；
6. 对第三方支付、清算和行情连接增加熔断、降级和人工接管；
7. 统一注入 `traceId`、`tenantId`、`channelCode`、`operatorId`；
8. 错误响应统一携带可检索的 `requestId` 和业务错误码。

### 3.4 分布式事务和一致性

建议按业务重要性分为两类：

| 一致性类型 | 业务对象 | 处理方式 |
| --- | --- | --- |
| 强一致 | 账户余额、账户流水、额度占用、保证金扣减、订单状态机 | 单服务本地事务、行锁或乐观锁、唯一约束；跨服务使用 Saga/补偿 |
| 最终一致 | 通知、审计、报表草稿、运营看板、缓存刷新、风险画像 | Outbox + MQ + Inbox + 重试 |

不建议为了“看起来统一”而对所有链路使用 Seata AT。外汇交易和支付链路跨服务、跨外部系统，长事务和外部不可回滚操作不适合简单 AT。

重点补齐：

- 业务单号、请求幂等号、事件 ID 三者分离；
- 所有状态机使用显式状态转换校验；
- 状态推进必须记录操作人、时间、原因和来源；
- 每个跨服务动作有补偿动作；
- 外部系统调用前落库，调用后记录请求报文摘要、响应摘要和重试次数；
- 失败操作支持人工重放，但必须保证幂等。

### 3.5 幂等和并发控制

当前已有 `@Idempotent` 和 `@RedisLock`，但需要修正使用方式。

高风险示例：

- `forex-payment/.../PaymentController.java` 的出账和入账接口使用 `System.currentTimeMillis()` 拼接幂等 Key；
- `forex-settlement/.../GuaranteeController.java`、`LcController.java`、`CollectionController.java` 也存在相同模式；
- `forex-trading/.../TradingController.java` 的部分创建 Key 只包含客户和币种，可能把两个合法的并发订单误判为重复。

推荐改造：

```text
客户端生成 idempotencyToken
  -> Gateway 透传
  -> Controller 读取 Token
  -> Redis 记录处理中和已完成结果
  -> DB 以 businessNo/token 建立唯一约束
```

禁止用当前时间毫秒值充当幂等请求标识。时间可以参与业务单号生成，但不能替代客户端请求幂等号。

业务单号建议统一使用 Snowflake、号段模式或独立序列服务，并以数据库唯一索引兜底。

### 3.6 工作流

当前 README 声明使用 Flowable，但源码更接近自建 `t_workflow_task` 和 `t_approval_record` 的表驱动流程。建议二选一：

1. 如果只是固定审批链，删除 Flowable 依赖，建立轻量状态机和审批记录模型；
2. 如果需要动态流程，真正接入 Flowable，并补齐 BPMN 流程定义和版本、动态会签/或签、加签/转办/委托、超时催办、历史、变量和业务单号关联。

对于银行系统，建议保留业务状态机作为领域真相，Flowable 只负责审批编排，不让 BPMN 直接取代核心业务状态。

### 3.7 调度和批处理

XXL-JOB 已有 Executor 和 Handler，但 `ScheduleDomainService` 中的监管报送和汇率刷新任务仍返回演示结果，不能作为生产任务。

每个任务应补齐：

- `batchNo` 和 `businessDate`；
- 任务实例表和任务步骤表；
- 分片参数；
- 断点续跑；
- 单批次幂等；
- 每一步的成功、失败和跳过数量；
- 失败记录和人工重试；
- 重试次数和重试原因；
- 任务与业务单号、消息 ID、Trace ID 的关联；
- 日终、月结和监管报送的数据快照。

批处理应优先使用游标或分段查询，避免一次性加载全部数据。大批量写入应使用批量 SQL，但必须控制单批次大小和事务时间。

### 3.8 观测、日志和审计

建议引入 OpenTelemetry 或 SkyWalking、Micrometer、Prometheus 和 Grafana，至少建立以下指标：

| 指标 | 说明 |
| --- | --- |
| API P95/P99 | 入口和核心接口延迟 |
| DB slow query | 慢 SQL、锁等待和连接池占用 |
| Redis hit ratio | 缓存命中率、回源和热点 Key |
| MQ lag | Topic、Tag、消费者组延迟 |
| MQ failure/dead letter | 消费失败和死信数量 |
| Payment success rate | 支付成功、退回和失败比例 |
| Clearing timeout | 清算超时和未确认数量 |
| Reporting rejection | 报送退回、修正和重报数量 |
| Risk alert SLA | 风险告警响应时间 |
| Job success rate | 任务成功率、重试次数和耗时 |

日志建议统一使用结构化字段：

```text
traceId, requestId, tenantId, channelCode, operatorId,
service, module, action, businessNo, aggregateId,
eventId, result, errorCode, durationMs
```

中英文日志建议遵循以下格式：

```java
log.info(
    "Payment submitted successfully / 支付提交成功, paymentNo={}, customerId={}, traceId={}",
    paymentNo, customerId, traceId
);
```

注意：

- 日志键名保持英文，方便检索和监控；
- 业务描述可以中英文并列；
- 不打印完整身份证、账号、Token、密码、报文敏感字段；
- 使用参数化日志，禁止字符串拼接；
- 异常日志必须带业务单号和错误码；
- 关键状态变化必须产生审计日志；
- 审计日志与普通应用日志分开存储和授权。

## 4. 性能优化建议

### 4.1 数据库

第一阶段应对所有核心列表查询执行 `EXPLAIN`，重点检查：

- `customer_id + status + create_time`；
- `status + create_time`；
- `business_no`、`order_no`、`payment_no`、`trade_no`；
- `currency_pair + status + create_time`；
- `statement_date + reconciliation_status`；
- `job_id + create_time`。

建议：

- 避免 `SELECT *`；
- 避免对大表做无条件 `COUNT(*)`；
- 深分页改为基于游标或主键的 seek pagination；
- 对核心业务单号建立唯一索引；
- 用数据库唯一约束兜底幂等；
- 查询方法标记 `@Transactional(readOnly = true)`；
- 避免在循环中逐条查询，消除 N+1；
- 日志、审计、事件和明细表按时间或业务域归档；
- 交易流水、报文、审计和监管提交记录规划冷热分层。

### 4.2 服务和线程模型

- Controller 不执行复杂业务；
- Application Service 只做用例编排；
- Domain Service 保持业务规则独立；
- 长耗时 OCR、报表、批量转换和外部支付调用异步化；
- 对外部系统调用设置超时，不使用无限等待；
- 线程池按业务类型隔离，避免批处理拖垮在线请求；
- 使用批处理 + checkpoint，避免长事务；
- 对牌价查询使用 L1/L2 缓存；
- 对不一致数据采用事件刷新，不在请求线程同步刷新全部下游。

### 4.3 前端

- 用 OpenAPI 或统一契约生成 TypeScript 类型，减少 `any`；
- 统一处理 loading、empty、error、permission denied 和 retry；
- 列表分页、筛选、排序和导出走后端；
- 牌价和支付状态用 SSE/WebSocket 或增量轮询；
- 页面销毁时清理 `setInterval`、`setTimeout` 和订阅；
- 大表使用虚拟滚动；
- API 重复点击使用前端请求锁，同时由后端幂等兜底；
- 所有 mock 页面改为真实 API 或显式的演示环境开关；
- 权限控制不仅在菜单层，还要控制按钮、字段和数据范围。

## 5. 资深产品经理视角

### 5.1 目标用户

| 用户群体 | 核心任务 | 关键诉求 |
| --- | --- | --- |
| 外汇交易员 | 询价、交易、锁汇、平仓、头寸管理 | 低延迟、实时牌价、限额提示、交易可追溯 |
| 国际结算运营 | 信用证、托收、保函、单据审核 | 流程可配置、单据完整、异常可接管 |
| 跨境支付运营 | 制单、合规、发送、追踪、退汇 | 多渠道路由、状态透明、失败可重试 |
| 风控和合规 | KYC/KYB、AML、制裁筛查、抽查 | 规则版本、证据链、可解释、可审计 |
| 清算和对账人员 | 报文、清算、回执、对账、差错处理 | 自动匹配、异常队列、人工干预 |
| 财务和监管报送人员 | 簿记、重估、报送、退回修正 | 数据可追溯、口径一致、批量处理 |
| 企业财资用户 | 付款、收款、现金池、套保和额度 | 一站式、多币种、资金可视、API/银企直连 |
| 管理层 | 风险、收益、资金、运营效率 | 经营看板、预警、趋势和 SLA |

### 5.2 当前产品优势

1. 业务覆盖面宽，从牌价、交易到支付、清算、报送均有模块；
2. DDD 目录结构为后续领域治理提供了基础；
3. 已经包含 CIPS、SWIFT/GPI、OCR、AI、现金池和套期会计等方向；
4. 有较多明确的业务状态机和数据库唯一约束；
5. 有分布式锁、幂等、限流、安全响应头等工程意识；
6. 前端页面覆盖面广，便于快速形成运营工作台。

### 5.3 当前产品短板

#### 短板一：业务模块多，但缺少一条可演示、可验收的主链路

必须优先打通：

```text
客户建档
  -> 牌价
  -> 交易或结售汇
  -> 风险与额度
  -> 账户或支付
  -> 清算
  -> 会计
  -> 监管报送
  -> 通知和审计
```

目前各模块都有页面和 API，但部分页面是 mock，跨服务事件和外部连接还没有形成全链路产品体验。

#### 短板二：没有统一的异常优先运营中心

银行运营人员最关心的不是模块数量，而是：

- 哪些支付卡住了；
- 哪些报文未确认；
- 哪些交易命中风险；
- 哪些额度即将超限；
- 哪些对账未匹配；
- 哪些监管报送被退回；
- 哪些任务需要人工接管。

建议新增“国际业务运营控制台”，把支付、清算、风险、对账、报送和任务失败聚合成异常队列。

#### 短板三：外部网络和标准适配没有产品化

需要把 SWIFT、CIPS、CFETS、代理行、外部 AML/KYC、企业 ERP/财资系统抽象为可配置连接器，而不是在业务服务中写死转换逻辑。

建议建立：

```text
Connector
  -> Message Mapping
  -> Validation
  -> Routing
  -> Submit
  -> Acknowledgement
  -> Reconciliation
  -> Exception Handling
```

#### 短板四：AI 能力需要从助手演示升级为受控决策辅助

AI 不应直接执行资金动作。应采用：

```text
AI 建议
  -> 规则校验
  -> 风险评分
  -> 人工确认
  -> 幂等执行
  -> 全量审计
```

需要支持解释、引用依据、模型版本、提示词版本、人工覆盖和责任追踪。

#### 短板五：缺少企业财资产品的闭环

如果目标不仅是银行内部系统，还要服务企业，应补充：

- 企业多币种账户和余额视图；
- 收付款计划；
- 现金池和资金归集；
- FX 暴露和套期建议；
- 付款模板和批量付款；
- 银企直连、API、SFTP、文件导入；
- 多级授权和企业内部审批；
- 回执、对账单和可下载报表；
- 手续费、汇率点差和服务 SLA。

## 6. 市场和行业需求判断

### 6.1 ISO 20022 带来的结构化数据要求

SWIFT 将 ISO 20022 定义为开放的全球金融信息标准，并强调结构化数据、自动化、合规分析和端到端直通处理。SWIFT 页面显示，2026 年 11 月之后将只接受完全结构化或混合结构化地址。

对本项目的直接要求：

- 收付款人地址必须建模为结构化字段；
- MT 到 MX 的转换不能只做字符串拼接；
- 需要地址、国家、城市、邮编和街道的校验规则；
- 需要报文版本、字段映射版本和失败原因；
- 需要支持 message preview、validation、repair 和 replay；
- 需要把结构化数据同步给 AML、路由、对账和监管报送。

### 6.2 CIPS 和人民币跨境业务

CIPS 官方资料显示，其采用 RTGS 与 DNS 混合结算机制，支持人民币和港币相关业务，并覆盖跨境汇款、金融市场业务以及相关信息服务。系统当前已有 CIPS 路由、报文生成和参与者查询的基础模块，但还需要进一步补齐真实连接器、报文校验、回执、状态推进和异常队列。

产品建议：

- 建立 CIPS 参与者主数据；
- 路由策略按币种、金额、国家、截止时间、费用和可用性配置；
- 报文格式独立版本化；
- 回执、拒绝、退回和人工修复统一进入异常中心；
- 对 CIPS、SWIFT 和其他通道提供统一支付状态模型。

### 6.3 中国外汇管理的便利化和智能化

SAFE 在 2025 年上半年数据发布中披露，企业和个人等非银行部门跨境收支达到 7.6 万亿美元，同比增长 10.4%，人民币在跨境收支中的占比为 53%。SAFE 同时强调跨境贸易便利化、电子化审核、自动化批量处理，以及国内外币种业务的规则和流程整合。

对产品的启示：

- 不能只做人工逐笔审核；
- 需要规则可配置、资料可追溯、审核可解释；
- 需要支持高质量企业、跨境电商和中小企业的批量业务；
- 需要国内外币种统一的客户、额度、支付和报送视图；
- 需要“一个规则、一站式处理”的业务编排；
- 需要为监管规则变化保留版本和生效日期。

### 6.4 多轨支付、可编程支付和智能路由

McKinsey 的 2025 Global Payments Report 将支付行业描述为多轨、互联和可编程方向，并强调跨境支付中的互操作性、智能路由、可编程合规和嵌入式决策。BIS Project Agorá 在 2026 年继续探索基于代币化央行准备金和商业银行存款的多币种共享平台，目标包括原子结算、全天候处理和把合规条件嵌入支付流程。

本项目不建议现在直接建设数字货币或代币化结算生产能力，但应提前做好架构留位：

- 支付 Rail 抽象，不把通道写死为 SWIFT/CIPS；
- 路由、费用、时效和流动性作为可计算策略；
- 支持条件支付和可编排工作流；
- 对支付状态、最终性、回执和争议建立统一模型；
- 为未来的多币种、实时和 24x7 业务保留时间模型。

## 7. 产品路线图

### P0: 从原型闭环到可运行版本

- 清理前端 mock，所有核心列表和写操作连接真实 API；
- 打通“客户 -> 牌价 -> 交易 -> 风险 -> 支付 -> 清算 -> 会计 -> 报送”；
- 修复 `System.currentTimeMillis()` 幂等 Key；
- 建立统一业务单号、请求幂等号和 Trace ID；
- 增加核心状态机测试和跨模块集成测试；
- 把 XXL-JOB 的演示任务接入真实领域服务；
- 输出核心业务操作日志、审计日志和错误码；
- 建立本地一键启动、初始化数据和验收脚本。

### P1: 银行内部国际业务运营平台

- RocketMQ Outbox/Inbox；
- 支付、清算、对账、报送异常中心；
- CIPS/SWIFT 报文和回执模型；
- Flowable 或轻量状态机的最终选型和落地；
- 动态限流、熔断、重试和人工接管；
- OpenTelemetry/SkyWalking + Micrometer；
- 牌价 L1/L2 缓存和主动发布；
- 规则版本化、监管口径版本化；
- 任务分片、断点续跑和失败重放；
- 前端 RBAC、字段权限和数据范围。

### P2: 企业跨境财资平台

- 多币种账户、现金池和资金归集；
- 付款模板、批量付款、收款和对账单；
- 企业级 API、SFTP、Host-to-Host 和文件中心；
- 外汇敞口、套期建议和风险预算；
- 多级企业审批和授权；
- 实时费用、点差和到账时效展示；
- 企业管理驾驶舱和资金预测；
- 运营数据产品和开放平台。

### P3: 智能和未来支付能力

- 受控 AI Agent；
- 智能支付路由；
- 可编程合规；
- 多轨支付抽象；
- 代币化存款或实时支付试验环境；
- 面向监管沙盒的仿真和回放平台。

## 8. 技术任务清单

| 编号 | 优先级 | 工作项 | 验收标准 |
| --- | --- | --- | --- |
| ARCH-001 | P0 | 修复幂等 Key 和业务单号 | 重复请求只产生一笔业务；数据库唯一约束兜底 |
| ARCH-002 | P0 | 打通核心端到端链路 | 一套测试数据可完成交易到报送 |
| ARCH-003 | P0 | 清理核心前端 mock | 核心页面全部调用真实 API，并有异常态 |
| ARCH-004 | P0 | 接入真实调度服务 | 日终、对账、汇率刷新和报送有真实结果 |
| ARCH-005 | P0 | 统一日志和审计字段 | 可按 Trace ID、业务单号查询完整链路 |
| ARCH-006 | P1 | Outbox/Inbox + RocketMQ | 消息可重试、可补偿、可死信、可重放 |
| ARCH-007 | P1 | 牌价 L1/L2 缓存 | 多实例共享、可主动失效、可统计命中率 |
| ARCH-008 | P1 | 动态限流和熔断 | Nacos 可改规则，异常下游不拖垮主链路 |
| ARCH-009 | P1 | 工作流最终落地 | 流程有版本、审批历史、会签、超时和委托 |
| ARCH-010 | P1 | 观测平台 | P95/P99、MQ lag、Redis hit、任务失败率可看 |
| ARCH-011 | P1 | ISO 20022 数据模型 | 结构化地址、字段校验、映射版本和错误修复 |
| ARCH-012 | P1 | CIPS/SWIFT 连接器 | 提交、回执、退回、对账和人工接管可运行 |
| ARCH-013 | P1 | 异常运营中心 | 支付、清算、报送、风险异常统一进入队列 |
| ARCH-014 | P2 | 企业财资 API | 企业可批量付款、查询余额、下载回执 |
| ARCH-015 | P2 | 套保和资金预测 | 暴露、限额、策略和结果可追踪 |

## 9. 阿里巴巴 Java 开发手册治理建议

建议将 Alibaba Java Coding Guidelines 的 P3C 检查纳入 Maven/CI，而不是依靠开发人员记忆。

### 9.1 代码和命名

- Controller 方法保持短小，不直接写 SQL 和复杂业务；
- DTO、Command、Query、Response 不混用；
- 枚举替代状态字符串和魔法数字；
- 统一使用业务异常和错误码；
- 重写 `equals` 时必须同步重写 `hashCode`；
- 所有重写方法使用 `@Override`；
- 禁止使用废弃 API；
- 禁止用 `System.currentTimeMillis()` 作为幂等 Token；
- 禁止把用户输入直接拼接到 SQL、日志和 XML/JSON；
- 金额使用 `BigDecimal`，禁止 `double` 参与金额计算。

### 9.2 数据库和事务

- 不使用 `SELECT *`；
- 重要查询必须有索引和执行计划；
- 业务唯一性用数据库唯一索引兜底；
- 事务边界放在 Application Service；
- 只读查询使用 `readOnly = true`；
- 事务内避免远程调用、长时间等待和大批量循环；
- 避免大事务和跨服务伪分布式事务；
- 逻辑删除、版本号、审计字段保持统一。

### 9.3 日志和异常

- 日志使用参数化写法；
- 日志至少包含业务单号、Trace ID、结果和耗时；
- 禁止打印密码、Token、完整账号和敏感报文；
- 错误码稳定，错误消息面向用户，堆栈面向日志；
- INFO 记录关键业务状态，WARN 记录可恢复异常，ERROR 记录需要处理的故障；
- 中英文日志可以并列，但键名和错误码保持英文统一；
- 注释说明业务原因、边界条件和外部标准，不重复代码本身。

### 9.4 质量门禁

建议 CI 至少执行：

```text
mvn -DskipTests verify
mvn test
P3C / PMD
Checkstyle or Spotless
Dependency vulnerability scan
OpenAPI contract check
SQL migration check
Frontend typecheck and build
```

## 10. 建议的下一步执行顺序

1. 先刷新 `/understand`，确认新增和变更模块被纳入图谱；
2. 建立 P0 主链路验收用例和测试数据；
3. 优先修复幂等、业务单号、状态机和核心数据库唯一约束；
4. 清理核心页面 mock，建立前后端接口契约；
5. 补齐统一 Trace、日志、审计和错误码；
6. 真实接入调度任务；
7. 以支付和交易事件为第一批建设 RocketMQ Outbox/Inbox；
8. 再建设缓存分层、动态限流、熔断和监控；
9. 最后扩展 ISO 20022、CIPS/SWIFT 连接器和企业财资能力。

## 11. 外部参考

业务规则落地时应以正式监管文件、通道协议和银行内部制度为准：

1. [SWIFT ISO 20022](https://www.swift.com/standards/iso-20022)
2. [BIS Project Agorá](https://www.bis.org/about/bisih/topics/fmis/agora.htm)
3. [SAFE 2025 外汇领域改革和开放](https://www.safe.gov.cn/en/2025/0618/2310.html)
4. [SAFE 2025 年上半年外汇收支数据](https://www.safe.gov.cn/en/2025/0722/2328.html)
5. [CIPS Introduction](https://www.cips.com.cn/en/about_us/about_cips/introduction/index.html)
6. [McKinsey Global Payments Report](https://www.mckinsey.com/industries/financial-services/our-insights/global-payments-report)
7. [Alibaba Java Coding Guidelines / P3C](https://github.com/alibaba/p3c)

## 12. 结论

这个项目的模块广度和架构骨架已经超过普通 CRUD 系统，适合作为银行国际业务平台的原型和基础工程。但目前最核心的问题不是还缺一个服务，而是：

```text
业务模块很多
  但跨服务事件没有可靠化
  中间件没有全部业务化
  前端仍有 mock
  外部通道没有形成连接器闭环
  运营异常没有统一工作台
  观测、审计和补偿机制还不完整
```

因此建议先做 P0 的真实业务闭环和可靠性治理，再做 P1 的运营平台和通道适配，最后再扩展企业财资、AI Agent 和未来支付能力。这样产品会从“功能展示型系统”逐步变成“可运行、可运营、可审计、可扩展的银行国际业务平台”。
