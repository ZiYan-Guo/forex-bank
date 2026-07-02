import fs from 'fs';

const PROJECT_ROOT = '/Users/guoziyan/Desktop/java-project/project-ai/forex-bank-system';

const files = [
  { path: "forex-exchange/src/main/java/com/forex/exchange/infrastructure/repository/ExchangeQuoteRepositoryImpl.java", lang: "java", cat: "code" },
  { path: "forex-exchange/src/main/resources/application.yml", lang: "yaml", cat: "config" },
  { path: "forex-gateway/pom.xml", lang: "xml", cat: "config" },
  { path: "forex-gateway/src/main/java/com/forex/gateway/config/CorsConfig.java", lang: "java", cat: "code" },
  { path: "forex-gateway/src/main/java/com/forex/gateway/config/RateLimitConfig.java", lang: "java", cat: "code" },
  { path: "forex-gateway/src/main/java/com/forex/gateway/filter/AuthGlobalFilter.java", lang: "java", cat: "code" },
  { path: "forex-gateway/src/main/java/com/forex/gateway/filter/SecurityHeadersFilter.java", lang: "java", cat: "code" },
  { path: "forex-gateway/src/main/java/com/forex/gateway/GatewayApplication.java", lang: "java", cat: "code" },
  { path: "forex-gateway/src/main/resources/application.yml", lang: "yaml", cat: "config" },
  { path: "forex-hedge-accounting/pom.xml", lang: "xml", cat: "config" },
  { path: "forex-hedge-accounting/src/main/java/com/forex/hedge/accounting/adapter/controller/HedgeAccountingController.java", lang: "java", cat: "code" },
  { path: "forex-hedge-accounting/src/main/java/com/forex/hedge/accounting/domain/model/aggregate/HedgeRelationship.java", lang: "java", cat: "code" },
  { path: "forex-hedge-accounting/src/main/java/com/forex/hedge/accounting/domain/model/entity/HedgeEffectivenessTest.java", lang: "java", cat: "code" },
  { path: "forex-hedge-accounting/src/main/java/com/forex/hedge/accounting/domain/service/HedgeAccountingService.java", lang: "java", cat: "code" },
  { path: "forex-hedge-accounting/src/main/java/com/forex/hedge/accounting/HedgeAccountingApplication.java", lang: "java", cat: "code" },
  { path: "forex-hedge-accounting/src/main/resources/application.yml", lang: "yaml", cat: "config" },
  { path: "forex-margin/pom.xml", lang: "xml", cat: "config" },
  { path: "forex-margin/src/main/java/com/forex/margin/adapter/controller/MarginController.java", lang: "java", cat: "code" },
  { path: "forex-margin/src/main/java/com/forex/margin/adapter/dto/MarginResp.java", lang: "java", cat: "code" },
  { path: "forex-margin/src/main/java/com/forex/margin/application/command/CreateMarginCmd.java", lang: "java", cat: "code" },
  { path: "forex-margin/src/main/java/com/forex/margin/application/service/MarginAppService.java", lang: "java", cat: "code" },
  { path: "forex-margin/src/main/java/com/forex/margin/domain/event/MarginCalledEvent.java", lang: "java", cat: "code" },
  { path: "forex-margin/src/main/java/com/forex/margin/domain/model/aggregate/MarginAccount.java", lang: "java", cat: "code" },
  { path: "forex-margin/src/main/java/com/forex/margin/domain/model/entity/MarginCall.java", lang: "java", cat: "code" },
  { path: "forex-margin/src/main/java/com/forex/margin/domain/model/query/MarginQuery.java", lang: "java", cat: "code" },
];

const fileMeta = {
  "forex-exchange/src/main/java/com/forex/exchange/infrastructure/repository/ExchangeQuoteRepositoryImpl.java": {
    s: "外汇报价仓储实现类，将 ExchangeQuote 领域对象与 ExchangeQuotePO 持久化对象相互转换，提供报价保存和最新报价查询功能。",
    t: ["仓储实现", "持久化", "报价"],
  },
  "forex-exchange/src/main/resources/application.yml": {
    s: "forex-exchange 外汇交易服务的应用配置，定义端口8201、MySQL数据源、Redis、Nacos服务发现与配置中心、MyBatis-Plus逻辑删除和Knife4j文档。",
    t: ["配置", "外汇交易"],
  },
  "forex-gateway/pom.xml": {
    s: "网关微服务 Maven 构建配置，依赖 Spring Cloud Gateway 路由网关、Nacos 服务发现/配置、Sentinel 熔断限流、forex-common-security 安全模块和 Redis Reactive 响应式连接。",
    t: ["maven", "网关", "依赖管理"],
  },
  "forex-gateway/src/main/java/com/forex/gateway/config/CorsConfig.java": {
    s: "CORS 跨域配置类，使用响应式 WebFilter 实现跨域请求处理，定义允许的请求头、HTTP方法和来源，支持 OPTIONS 预检请求。",
    t: ["配置", "CORS", "跨域"],
  },
  "forex-gateway/src/main/java/com/forex/gateway/config/RateLimitConfig.java": {
    s: "网关限流键解析器配置，基于客户端 IP 地址(RemoteAddress)生成限流键，供 Sentinel Gateway 限流规则使用。",
    t: ["配置", "限流", "IP解析"],
  },
  "forex-gateway/src/main/java/com/forex/gateway/filter/AuthGlobalFilter.java": {
    s: "网关全局认证过滤器，实现 GlobalFilter 接口，从请求头提取 Bearer JWT 令牌、校验有效性后将用户ID/用户名/角色/权限注入下游请求头，支持配置白名单 URL 跳过认证。",
    t: ["过滤器", "全局认证", "JWT", "网关"],
  },
  "forex-gateway/src/main/java/com/forex/gateway/filter/SecurityHeadersFilter.java": {
    s: "网关安全响应头过滤器，为每个响应注入 XSS 防护(nosniff)、点击劫持防护(DENY)、内容安全策略(CSP)、HSTS 强制 HTTPS、Referrer-Policy 和缓存控制等安全头。",
    t: ["过滤器", "安全", "响应头", "网关"],
  },
  "forex-gateway/src/main/java/com/forex/gateway/GatewayApplication.java": {
    s: "网关微服务启动类，使用 @EnableDiscoveryClient 启用 Nacos 服务发现，基于 Spring Cloud Gateway 提供 API 网关入口。",
    t: ["启动类", "SpringBoot", "网关"],
  },
  "forex-gateway/src/main/resources/application.yml": {
    s: "网关核心配置文件，定义 18 个微服务路由规则(含 auth/customer/exchange/trading/valuation/margin/position/bookkeeping/payment/settlement/clearing/risk/reporting/account/rate/workflow/notification/ocr)，各路由配置 Sentinel 限流参数，设置 JWT 密钥、Redis 连接和认证白名单 URL。",
    t: ["配置", "网关", "路由", "限流"],
  },
  "forex-hedge-accounting/pom.xml": {
    s: "套保会计微服务 Maven 构建配置，依赖 forex-common-base/security/mybatis 公共模块、Spring Boot Web、Nacos 服务发现和 Knife4j API 文档。",
    t: ["maven", "套保会计", "依赖管理"],
  },
  "forex-hedge-accounting/src/main/java/com/forex/hedge/accounting/adapter/controller/HedgeAccountingController.java": {
    s: "套保会计 REST 控制器，提供套期关系创建/查询/指定、预期/追溯有效性测试、会计分录生成和客户套期有效性报告等接口，所有写操作需权限校验。",
    t: ["controller", "套保会计", "REST"],
  },
  "forex-hedge-accounting/src/main/java/com/forex/hedge/accounting/domain/model/aggregate/HedgeRelationship.java": {
    s: "套期关系聚合根，遵循 IFRS9/ASC815 准则，封装公允价值/现金流量/净投资三类套期，支持指定(designate)、标记有效/无效(markEffective/markIneffective)、取消指定(deDesignate)等业务操作和内变校验。",
    t: ["聚合根", "DDD", "套期关系", "IFRS9"],
  },
  "forex-hedge-accounting/src/main/java/com/forex/hedge/accounting/domain/model/entity/HedgeEffectivenessTest.java": {
    s: "套期有效性测试实体，记录测试类型(预期/追溯)、测试方法(美元抵补法/回归/变动减少)、结果比率和 PASS/FAIL 状态，提供 isPassed() 判断方法。",
    t: ["实体", "DDD", "有效性测试"],
  },
  "forex-hedge-accounting/src/main/java/com/forex/hedge/accounting/domain/service/HedgeAccountingService.java": {
    s: "套保会计领域服务，实现预期和追溯有效性测试(美元抵补法比较公允价值变动)、套保会计分录生成(公允价值变动借记/套期工具重估贷记)。",
    t: ["领域服务", "DDD", "有效性测试", "会计分录"],
  },
  "forex-hedge-accounting/src/main/java/com/forex/hedge/accounting/HedgeAccountingApplication.java": {
    s: "套保会计微服务 Spring Boot 启动类，启用 Nacos 服务发现和 MyBatis Mapper 扫描。",
    t: ["启动类", "SpringBoot", "套保会计"],
  },
  "forex-hedge-accounting/src/main/resources/application.yml": {
    s: "forex-hedge-accounting 套保会计服务的应用配置，定义端口8220、MySQL 数据源、Redis、Nacos 服务发现与配置中心、MyBatis-Plus 逻辑删除和 Knife4j API 文档。",
    t: ["配置", "套保会计"],
  },
  "forex-margin/pom.xml": {
    s: "保证金微服务 Maven 构建配置，依赖 forex-common-base/security/mybatis 公共模块、Spring Boot Web、Nacos 服务发现和 Knife4j API 文档。",
    t: ["maven", "保证金", "依赖管理"],
  },
  "forex-margin/src/main/java/com/forex/margin/adapter/controller/MarginController.java": {
    s: "保证金 REST 控制器，提供初始保证金创建(带幂等)、追加/释放/存入保证金(带Redis分布式锁)和分页查询接口，集成 @RequirePermission 权限校验、@Idempotent 幂等和 @RedisLock 分布式锁注解。",
    t: ["controller", "保证金", "REST"],
  },
  "forex-margin/src/main/java/com/forex/margin/adapter/dto/MarginResp.java": {
    s: "保证金响应 DTO，包含保证金编号、客户/交易ID、保证金类型/币种、应交/已存/差额金额、保证金率、催缴/到期时间、状态、抵押品类型和释放原因等完整字段。",
    t: ["dto", "响应", "保证金"],
  },
  "forex-margin/src/main/java/com/forex/margin/application/command/CreateMarginCmd.java": {
    s: "创建保证金应用命令，含客户ID、交易ID、名义本金、保证金率和币种字段，全部字段 @NotNull 校验。",
    t: ["command", "应用层", "CQRS"],
  },
  "forex-margin/src/main/java/com/forex/margin/application/service/MarginAppService.java": {
    s: "保证金应用服务，编排 MarginAccountRepository 和 MarginDomainService，实现初始保证金创建、追加(call)/释放(release)/存入(deposit)保证金、详情查询和分页查询等用例，所有写操作加 @Transactional 事务。",
    t: ["应用服务", "用例编排", "事务"],
  },
  "forex-margin/src/main/java/com/forex/margin/domain/event/MarginCalledEvent.java": {
    s: "保证金追缴领域事件，继承 BaseDomainEvent，携带 marginId 和 callAmount 追缴金额，事件名称 MarginCalled。",
    t: ["领域事件", "DDD"],
  },
  "forex-margin/src/main/java/com/forex/margin/domain/model/aggregate/MarginAccount.java": {
    s: "保证金账户聚合根，封装初始保证金计算(含期限和波动率调整)、存入/释放/追缴保证金、水位线检查(WaterLevel)、差额计算(calculateShortfall)、部分支付和取消等完整业务规则和内变校验。",
    t: ["聚合根", "DDD", "保证金", "业务规则"],
  },
  "forex-margin/src/main/java/com/forex/margin/domain/model/entity/MarginCall.java": {
    s: "保证金追缴实体，记录追缴类型(callType)、金额、追缴日期和响应状态，提供 respond() 方法记录响应。",
    t: ["实体", "DDD", "追缴"],
  },
  "forex-margin/src/main/java/com/forex/margin/domain/model/query/MarginQuery.java": {
    s: "保证金分页查询条件，继承 PageReq，支持按客户ID、交易ID、保证金编号、保证金类型和状态过滤。",
    t: ["query", "分页", "CQRS"],
  },
};

// class info: name + methods
const classInfo = {
  "forex-exchange/src/main/java/com/forex/exchange/infrastructure/repository/ExchangeQuoteRepositoryImpl.java": {
    classes: [{
      name: "ExchangeQuoteRepositoryImpl",
      methods: ["save", "findLatestQuote", "toDomain", "toPO"],
      summary: "外汇报价仓储实现，提供 save 持久化和 findLatestQuote 最新报价查询，内部进行领域对象与 PO 双向映射。",
      tags: ["仓储实现", "持久化"],
    }]
  },
  "forex-gateway/src/main/java/com/forex/gateway/config/CorsConfig.java": {
    classes: [{
      name: "CorsConfig",
      methods: ["corsFilter"],
      summary: "CORS 跨域配置类，通过 WebFilter Bean 为所有响应添加跨域访问控制头。",
      tags: ["配置", "CORS"],
    }]
  },
  "forex-gateway/src/main/java/com/forex/gateway/config/RateLimitConfig.java": {
    classes: [{
      name: "RateLimitConfig",
      methods: ["ipKeyResolver"],
      summary: "限流键解析器配置，提供基于客户端 IP 的 KeyResolver Bean。",
      tags: ["配置", "限流"],
    }]
  },
  "forex-gateway/src/main/java/com/forex/gateway/filter/AuthGlobalFilter.java": {
    classes: [{
      name: "AuthGlobalFilter",
      methods: ["filter", "isIgnoreUrl", "extractToken", "setIgnoreAuthUrls", "getOrder"],
      summary: "全局 JWT 认证过滤器，提取 Bearer 令牌、校验解析后注入用户信息头到下游。",
      tags: ["过滤器", "认证", "JWT"],
    }]
  },
  "forex-gateway/src/main/java/com/forex/gateway/filter/SecurityHeadersFilter.java": {
    classes: [{
      name: "SecurityHeadersFilter",
      methods: ["filter", "getOrder"],
      summary: "安全响应头过滤器，注入 XSS/点击劫持/CSP/HSTS 等安全防护头。",
      tags: ["过滤器", "安全"],
    }]
  },
  "forex-gateway/src/main/java/com/forex/gateway/GatewayApplication.java": {
    classes: [{
      name: "GatewayApplication",
      methods: ["main"],
      summary: "网关微服务 Spring Boot 启动类。",
      tags: ["启动类", "网关"],
    }]
  },
  "forex-hedge-accounting/src/main/java/com/forex/hedge/accounting/adapter/controller/HedgeAccountingController.java": {
    classes: [{
      name: "HedgeAccountingController",
      methods: ["createRelationship", "getRelationship", "designate", "prospectiveTest", "retrospectiveTest", "generateEntries", "generateReport", "toMap"],
      summary: "套保会计控制器，管理套期关系全生命周期、有效性测试和分录生成。",
      tags: ["controller", "套保会计"],
    }]
  },
  "forex-hedge-accounting/src/main/java/com/forex/hedge/accounting/domain/model/aggregate/HedgeRelationship.java": {
    classes: [{
      name: "HedgeRelationship",
      methods: ["create", "reconstitute", "designate", "markEffective", "markIneffective", "deDesignate", "recordIneffectiveness", "validate"],
      summary: "套期关系聚合根，IFRS9 准则下的套期业务核心模型。",
      tags: ["聚合根", "套期", "IFRS9"],
    }]
  },
  "forex-hedge-accounting/src/main/java/com/forex/hedge/accounting/domain/model/entity/HedgeEffectivenessTest.java": {
    classes: [{
      name: "HedgeEffectivenessTest",
      methods: ["isPassed"],
      summary: "套期有效性测试实体，记录测试结果比率和 PASS/FAIL 判断。",
      tags: ["实体", "有效性测试"],
    }]
  },
  "forex-hedge-accounting/src/main/java/com/forex/hedge/accounting/domain/service/HedgeAccountingService.java": {
    classes: [{
      name: "HedgeAccountingService",
      methods: ["performProspectiveTest", "performRetrospectiveTest", "generateHedgeEntries"],
      summary: "套保会计领域服务，执行有效性测试和生成套保分录。",
      tags: ["领域服务", "有效性测试"],
    }]
  },
  "forex-hedge-accounting/src/main/java/com/forex/hedge/accounting/HedgeAccountingApplication.java": {
    classes: [{
      name: "HedgeAccountingApplication",
      methods: ["main"],
      summary: "套保会计微服务启动类。",
      tags: ["启动类", "套保会计"],
    }]
  },
  "forex-margin/src/main/java/com/forex/margin/adapter/controller/MarginController.java": {
    classes: [{
      name: "MarginController",
      methods: ["createInitialMargin", "callMargin", "releaseMargin", "depositMargin", "getMarginDetail", "pageQuery", "toMarginResp"],
      summary: "保证金管理控制器，提供创建/追缴/释放/存入保证金及分页查询 REST API。",
      tags: ["controller", "保证金"],
    }]
  },
  "forex-margin/src/main/java/com/forex/margin/adapter/dto/MarginResp.java": {
    classes: [{
      name: "MarginResp",
      methods: [],
      summary: "保证金响应 DTO，返回保证金账户完整业务字段。",
      tags: ["dto", "响应"],
    }]
  },
  "forex-margin/src/main/java/com/forex/margin/application/command/CreateMarginCmd.java": {
    classes: [{
      name: "CreateMarginCmd",
      methods: [],
      summary: "创建保证金命令对象。",
      tags: ["command", "CQRS"],
    }]
  },
  "forex-margin/src/main/java/com/forex/margin/application/service/MarginAppService.java": {
    classes: [{
      name: "MarginAppService",
      methods: ["createInitialMargin", "callMargin", "releaseMargin", "depositMargin", "getMarginDetail", "pageQuery"],
      summary: "保证金应用服务，编排仓储与领域服务实现保证金全流程用例。",
      tags: ["应用服务", "事务"],
    }]
  },
  "forex-margin/src/main/java/com/forex/margin/domain/event/MarginCalledEvent.java": {
    classes: [{
      name: "MarginCalledEvent",
      methods: ["eventName"],
      summary: "保证金追缴领域事件。",
      tags: ["领域事件"],
    }]
  },
  "forex-margin/src/main/java/com/forex/margin/domain/model/aggregate/MarginAccount.java": {
    classes: [{
      name: "MarginAccount",
      methods: ["create", "calculateMarginRate", "calculateRequiredAmount", "pay", "partialPay", "cancel", "checkWaterLevel", "getDepositRatio", "reconstitute", "deposit", "release", "call", "calculateShortfall", "assignMarginNo", "setWaterLevel", "validate"],
      summary: "保证金账户聚合根，封装保证金计算、存入、释放、追缴、水位线评估等核心业务规则。",
      tags: ["聚合根", "保证金", "业务规则"],
    }]
  },
  "forex-margin/src/main/java/com/forex/margin/domain/model/entity/MarginCall.java": {
    classes: [{
      name: "MarginCall",
      methods: ["respond"],
      summary: "保证金追缴实体，记录追缴详情和响应。",
      tags: ["实体", "追缴"],
    }]
  },
  "forex-margin/src/main/java/com/forex/margin/domain/model/query/MarginQuery.java": {
    classes: [{
      name: "MarginQuery",
      methods: [],
      summary: "保证金分页查询条件对象。",
      tags: ["query", "分页"],
    }]
  },
};

// Resolve import to file path (only for classes in this batch)
function resolveImportToFile(imp) {
  const m = imp.match(/import\s+(?:static\s+)?([\w.]+);/);
  if (!m) return null;
  const fqn = m[1];

  // forex-exchange within batch
  if (fqn.startsWith("com.forex.exchange.")) {
    return "forex-exchange/src/main/java/" + fqn.replace(/\./g, '/') + ".java";
  }
  // forex-gateway within batch
  if (fqn.startsWith("com.forex.gateway.")) {
    return "forex-gateway/src/main/java/" + fqn.replace(/\./g, '/') + ".java";
  }
  // forex-hedge-accounting within batch
  if (fqn.startsWith("com.forex.hedge.accounting.")) {
    return "forex-hedge-accounting/src/main/java/" + fqn.replace(/\./g, '/') + ".java";
  }
  // forex-margin within batch
  if (fqn.startsWith("com.forex.margin.")) {
    return "forex-margin/src/main/java/" + fqn.replace(/\./g, '/') + ".java";
  }
  return null;
}

// Build nodes
const nodes = [];
const edgeSet = new Set();
const edges = [];

function addEdge(source, target, type, weight, summary) {
  const k = source + '|' + target + '|' + type;
  if (edgeSet.has(k)) return;
  edgeSet.add(k);
  edges.push({ source, target, type, weight, summary });
}

const fileSet = new Set(files.map(f => f.path));

for (const f of files) {
  const meta = fileMeta[f.path] || { s: f.path, t: ["代码"] };
  const type = f.cat === "config" ? "config" : "file";
  const name = f.path.split("/").pop();
  const complexity = f.lang === "java"
    ? (classInfo[f.path]?.classes[0]?.methods?.length > 6 ? "high" : classInfo[f.path]?.classes[0]?.methods?.length > 2 ? "medium" : "low")
    : "low";

  nodes.push({
    id: type + ":" + f.path,
    type,
    name,
    filePath: f.path,
    summary: meta.s,
    tags: meta.t,
    complexity,
  });

  // Class nodes for Java files
  if (classInfo[f.path]) {
    for (const c of classInfo[f.path].classes) {
      const cid = "class:" + f.path + ":" + c.name;
      nodes.push({
        id: cid,
        type: "class",
        name: c.name,
        filePath: f.path,
        summary: c.summary,
        tags: c.tags,
        complexity: c.methods.length > 6 ? "high" : c.methods.length > 2 ? "medium" : "low",
      });
      addEdge(type + ":" + f.path, cid, "contains", 1.0, `文件包含 ${c.name} 类`);

      // Class contains method edges
      for (const m of c.methods) {
        const fid = "function:" + f.path + ":" + m;
        nodes.push({
          id: fid,
          type: "function",
          name: m,
          filePath: f.path,
          summary: c.name + " 的方法 " + m,
          tags: ["方法"],
          complexity: "low",
        });
        addEdge(cid, fid, "contains", 1.0, `类包含 ${m} 方法`);
      }
    }
  }
}

// Import edges (from batchImportData)
const batchImportData = {
  "forex-exchange/src/main/java/com/forex/exchange/infrastructure/repository/ExchangeQuoteRepositoryImpl.java": [
    "import com.forex.exchange.domain.model.entity.ExchangeQuote;",
    "import com.forex.exchange.domain.repository.ExchangeQuoteRepository;",
    "import com.forex.exchange.infrastructure.mapper.ExchangeQuoteMapper;",
    "import com.forex.exchange.infrastructure.persistence.ExchangeQuotePO;",
    "import lombok.RequiredArgsConstructor;",
    "import org.springframework.stereotype.Repository;",
    "import java.util.Optional;"
  ],
  "forex-exchange/src/main/resources/application.yml": [],
  "forex-gateway/pom.xml": [],
  "forex-gateway/src/main/java/com/forex/gateway/config/CorsConfig.java": [
    "import org.springframework.context.annotation.Bean;",
    "import org.springframework.context.annotation.Configuration;",
    "import org.springframework.http.HttpHeaders;",
    "import org.springframework.http.HttpMethod;",
    "import org.springframework.http.HttpStatus;",
    "import org.springframework.http.server.reactive.ServerHttpRequest;",
    "import org.springframework.http.server.reactive.ServerHttpResponse;",
    "import org.springframework.web.cors.reactive.CorsUtils;",
    "import org.springframework.web.server.ServerWebExchange;",
    "import org.springframework.web.server.WebFilter;",
    "import org.springframework.web.server.WebFilterChain;",
    "import reactor.core.publisher.Mono;"
  ],
  "forex-gateway/src/main/java/com/forex/gateway/config/RateLimitConfig.java": [
    "import org.springframework.cloud.gateway.filter.ratelimit.KeyResolver;",
    "import org.springframework.context.annotation.Bean;",
    "import org.springframework.context.annotation.Configuration;",
    "import reactor.core.publisher.Mono;"
  ],
  "forex-gateway/src/main/java/com/forex/gateway/filter/AuthGlobalFilter.java": [
    "import com.forex.common.security.jwt.JwtUtil;",
    "import io.jsonwebtoken.Claims;",
    "import lombok.extern.slf4j.Slf4j;",
    "import org.springframework.boot.context.properties.ConfigurationProperties;",
    "import org.springframework.cloud.gateway.filter.GatewayFilterChain;",
    "import org.springframework.cloud.gateway.filter.GlobalFilter;",
    "import org.springframework.core.Ordered;",
    "import org.springframework.http.HttpStatus;",
    "import org.springframework.http.server.reactive.ServerHttpRequest;",
    "import org.springframework.stereotype.Component;",
    "import org.springframework.util.AntPathMatcher;",
    "import org.springframework.web.server.ServerWebExchange;",
    "import reactor.core.publisher.Mono;",
    "import java.util.ArrayList;",
    "import java.util.List;"
  ],
  "forex-gateway/src/main/java/com/forex/gateway/filter/SecurityHeadersFilter.java": [
    "import org.springframework.cloud.gateway.filter.GatewayFilterChain;",
    "import org.springframework.cloud.gateway.filter.GlobalFilter;",
    "import org.springframework.core.Ordered;",
    "import org.springframework.http.HttpHeaders;",
    "import org.springframework.stereotype.Component;",
    "import org.springframework.web.server.ServerWebExchange;",
    "import reactor.core.publisher.Mono;"
  ],
  "forex-gateway/src/main/java/com/forex/gateway/GatewayApplication.java": [
    "import org.springframework.boot.SpringApplication;",
    "import org.springframework.boot.autoconfigure.SpringBootApplication;",
    "import org.springframework.cloud.client.discovery.EnableDiscoveryClient;"
  ],
  "forex-gateway/src/main/resources/application.yml": [],
  "forex-hedge-accounting/pom.xml": [],
  "forex-hedge-accounting/src/main/java/com/forex/hedge/accounting/adapter/controller/HedgeAccountingController.java": [
    "import com.forex.common.base.result.R;",
    "import com.forex.hedge.accounting.domain.model.aggregate.HedgeRelationship;",
    "import com.forex.hedge.accounting.domain.model.entity.HedgeEffectivenessTest;",
    "import com.forex.hedge.accounting.domain.service.HedgeAccountingService;",
    "import io.swagger.v3.oas.annotations.Operation;",
    "import io.swagger.v3.oas.annotations.tags.Tag;",
    "import lombok.RequiredArgsConstructor;",
    "import lombok.extern.slf4j.Slf4j;",
    "import org.springframework.web.bind.annotation.*;",
    "import java.math.BigDecimal;",
    "import java.math.RoundingMode;",
    "import java.time.LocalDate;",
    "import java.util.*;",
    "import com.forex.common.security.annotation.RequirePermission;"
  ],
  "forex-hedge-accounting/src/main/java/com/forex/hedge/accounting/domain/model/aggregate/HedgeRelationship.java": [
    "import com.forex.common.base.domain.BaseAggregate;",
    "import lombok.Getter;",
    "import java.math.BigDecimal;",
    "import java.time.LocalDate;",
    "import com.forex.common.base.exception.BusinessException;",
    "import com.forex.common.base.result.ResultCode;"
  ],
  "forex-hedge-accounting/src/main/java/com/forex/hedge/accounting/domain/model/entity/HedgeEffectivenessTest.java": [
    "import com.forex.common.base.domain.BaseEntity;",
    "import lombok.AllArgsConstructor;",
    "import lombok.Getter;",
    "import lombok.NoArgsConstructor;",
    "import java.math.BigDecimal;",
    "import java.time.LocalDate;"
  ],
  "forex-hedge-accounting/src/main/java/com/forex/hedge/accounting/domain/service/HedgeAccountingService.java": [
    "import com.forex.hedge.accounting.domain.model.aggregate.HedgeRelationship;",
    "import com.forex.hedge.accounting.domain.model.entity.HedgeEffectivenessTest;",
    "import lombok.extern.slf4j.Slf4j;",
    "import org.springframework.stereotype.Service;",
    "import java.math.BigDecimal;",
    "import java.math.RoundingMode;",
    "import java.time.LocalDate;",
    "import java.util.ArrayList;",
    "import java.util.List;",
    "import java.util.Map;",
    "import org.springframework.transaction.annotation.Transactional;"
  ],
  "forex-hedge-accounting/src/main/java/com/forex/hedge/accounting/HedgeAccountingApplication.java": [
    "import org.mybatis.spring.annotation.MapperScan;",
    "import org.springframework.boot.SpringApplication;",
    "import org.springframework.boot.autoconfigure.SpringBootApplication;",
    "import org.springframework.cloud.client.discovery.EnableDiscoveryClient;"
  ],
  "forex-hedge-accounting/src/main/resources/application.yml": [],
  "forex-margin/pom.xml": [],
  "forex-margin/src/main/java/com/forex/margin/adapter/controller/MarginController.java": [
    "import com.forex.common.base.annotation.Idempotent;",
    "import com.forex.common.base.annotation.RedisLock;",
    "import com.forex.common.base.dto.PageResp;",
    "import com.forex.common.base.result.R;",
    "import com.forex.margin.adapter.dto.MarginResp;",
    "import com.forex.margin.application.command.CreateMarginCmd;",
    "import com.forex.margin.application.service.MarginAppService;",
    "import com.forex.margin.domain.model.aggregate.MarginAccount;",
    "import com.forex.margin.adapter.dto.MarginPageQuery;",
    "import com.forex.margin.domain.model.query.MarginQuery;",
    "import io.swagger.v3.oas.annotations.Operation;",
    "import io.swagger.v3.oas.annotations.tags.Tag;",
    "import jakarta.validation.Valid;",
    "import lombok.RequiredArgsConstructor;",
    "import org.springframework.web.bind.annotation.GetMapping;",
    "import org.springframework.web.bind.annotation.PathVariable;",
    "import org.springframework.web.bind.annotation.PostMapping;",
    "import org.springframework.web.bind.annotation.RequestBody;",
    "import org.springframework.web.bind.annotation.RequestMapping;",
    "import org.springframework.web.bind.annotation.RequestParam;",
    "import org.springframework.web.bind.annotation.RestController;",
    "import java.math.BigDecimal;",
    "import java.util.List;",
    "import com.forex.common.security.annotation.RequirePermission;"
  ],
  "forex-margin/src/main/java/com/forex/margin/adapter/dto/MarginResp.java": [
    "import io.swagger.v3.oas.annotations.media.Schema;",
    "import lombok.Data;",
    "import java.math.BigDecimal;",
    "import java.time.LocalDateTime;"
  ],
  "forex-margin/src/main/java/com/forex/margin/application/command/CreateMarginCmd.java": [
    "import jakarta.validation.constraints.NotNull;",
    "import lombok.Data;",
    "import java.math.BigDecimal;"
  ],
  "forex-margin/src/main/java/com/forex/margin/application/service/MarginAppService.java": [
    "import com.forex.common.base.dto.PageResp;",
    "import com.forex.margin.application.command.CreateMarginCmd;",
    "import com.forex.margin.domain.model.aggregate.MarginAccount;",
    "import com.forex.margin.domain.model.query.MarginQuery;",
    "import com.forex.margin.domain.repository.MarginAccountRepository;",
    "import com.forex.margin.domain.service.MarginDomainService;",
    "import lombok.RequiredArgsConstructor;",
    "import org.springframework.stereotype.Service;",
    "import org.springframework.transaction.annotation.Transactional;",
    "import java.math.BigDecimal;",
    "import com.forex.common.base.exception.BusinessException;",
    "import com.forex.common.base.result.ResultCode;"
  ],
  "forex-margin/src/main/java/com/forex/margin/domain/event/MarginCalledEvent.java": [
    "import com.forex.common.base.domain.BaseDomainEvent;",
    "import lombok.Getter;",
    "import java.math.BigDecimal;"
  ],
  "forex-margin/src/main/java/com/forex/margin/domain/model/aggregate/MarginAccount.java": [
    "import com.forex.common.base.domain.BaseAggregate;",
    "import com.forex.margin.domain.model.valueobject.WaterLevel;",
    "import lombok.Getter;",
    "import java.math.BigDecimal;",
    "import java.time.LocalDateTime;",
    "import com.forex.common.base.exception.BusinessException;",
    "import com.forex.common.base.result.ResultCode;"
  ],
  "forex-margin/src/main/java/com/forex/margin/domain/model/entity/MarginCall.java": [
    "import com.forex.common.base.domain.BaseEntity;",
    "import lombok.AllArgsConstructor;",
    "import lombok.Getter;",
    "import lombok.NoArgsConstructor;",
    "import java.math.BigDecimal;",
    "import java.time.LocalDateTime;"
  ],
  "forex-margin/src/main/java/com/forex/margin/domain/model/query/MarginQuery.java": [
    "import com.forex.common.base.dto.PageReq;",
    "import lombok.Data;",
    "import lombok.EqualsAndHashCode;"
  ]
};

// Process import edges
for (const [srcPath, imps] of Object.entries(batchImportData)) {
  if (!classInfo[srcPath] || !classInfo[srcPath].classes) continue;
  const srcClass = classInfo[srcPath].classes[0].name;
  const srcClassId = "class:" + srcPath + ":" + srcClass;

  for (const imp of imps) {
    const targetPath = resolveImportToFile(imp);
    if (!targetPath || !fileSet.has(targetPath)) continue;
    if (!classInfo[targetPath] || !classInfo[targetPath].classes) continue;

    const targetClass = classInfo[targetPath].classes[0].name;
    const targetClassId = "class:" + targetPath + ":" + targetClass;

    if (srcClassId === targetClassId) continue; // skip self-imports

    const simpleName = imp.match(/import\s+[\w.]+\.(\w+);/)?.[1] || '';
    addEdge(srcClassId, targetClassId, "imports", 0.7, `${srcClass} 引用 ${targetClass}`);
  }
}

// Config edges: pom.xml configures the module's files
const modulePomMap = {
  "forex-gateway/pom.xml": ["forex-gateway/"],
  "forex-hedge-accounting/pom.xml": ["forex-hedge-accounting/"],
  "forex-margin/pom.xml": ["forex-margin/"],
};

for (const [pomPath, prefixes] of Object.entries(modulePomMap)) {
  for (const f of files) {
    if (f.path === pomPath) continue;
    for (const prefix of prefixes) {
      if (f.path.startsWith(prefix)) {
        addEdge("config:" + pomPath, "file:" + f.path, "configures", 0.6,
          pomPath.replace('/pom.xml', '') + " pom.xml 管理 " + f.path.split('/').pop() + " 所属模块依赖");
        break;
      }
    }
  }
}

// Configures: application.yml configures files in the same module
const appConfigMap = {
  "forex-exchange/src/main/resources/application.yml": "forex-exchange",
  "forex-gateway/src/main/resources/application.yml": "forex-gateway",
  "forex-hedge-accounting/src/main/resources/application.yml": "forex-hedge-accounting",
};

for (const [ymlPath, modulePrefix] of Object.entries(appConfigMap)) {
  for (const f of files) {
    if (f.path === ymlPath) continue;
    if (f.path.startsWith(modulePrefix) && f.lang === "java") {
      addEdge("config:" + ymlPath, "file:" + f.path, "configures", 0.6,
        ymlPath.split('/').pop() + " 配置 " + modulePrefix + " 模块的服务参数");
    }
  }
}

// Depends_on: pom.xml depends on common modules (via file nodes in the module)
const pomDependencies = {
  "forex-gateway/pom.xml": { desc: "forex-gateway 依赖 forex-common-security 安全模块" },
  "forex-hedge-accounting/pom.xml": { desc: "forex-hedge-accounting 依赖公共模块(common-base/security/mybatis)" },
  "forex-margin/pom.xml": { desc: "forex-margin 依赖公共模块(common-base/security/mybatis)" },
};

// Since common modules aren't in this batch, we'd skip depends_on edges for external modules.
// But we can add edges between modules within the batch.

// Cross-module depends_on within batch: (if any)
// forex-margin pom -> forex-gateway? No direct dependency...
// forex-hedge-accounting pom -> forex-margin? No...

// Write output
const output = { nodes, edges };
fs.writeFileSync(
  PROJECT_ROOT + '/.understand-anything/intermediate/batch-18.json',
  JSON.stringify(output, null, 2)
);

console.log('batch-18.json generated');
console.log('nodes:', nodes.length, 'edges:', edges.length);

const byType = {};
for (const n of nodes) byType[n.type] = (byType[n.type] || 0) + 1;
const eByType = {};
for (const e of edges) eByType[e.type] = (eByType[e.type] || 0) + 1;
console.log('nodeTypes:', JSON.stringify(byType));
console.log('edgeTypes:', JSON.stringify(eByType));
