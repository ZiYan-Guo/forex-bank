import fs from 'node:fs';
import path from 'node:path';

const projectRoot = process.cwd();
const uaDir = fs.existsSync(path.join(projectRoot, '.understand-anything'))
  ? path.join(projectRoot, '.understand-anything')
  : path.join(projectRoot, '.ua');
const batchesPath = path.join(uaDir, 'intermediate', 'batches.json');
const batchesData = JSON.parse(fs.readFileSync(batchesPath, 'utf8'));
const batches = Array.isArray(batchesData) ? batchesData : batchesData.batches;
const selected = [2, 4, 7, 8];

const P = {
  paymentController: 'forex-payment/src/main/java/com/forex/payment/adapter/controller/PaymentController.java',
  exchangeRateMapper: 'forex-rate/src/main/java/com/forex/rate/infrastructure/mapper/ExchangeRateMapper.java',
  exchangeRateRepository: 'forex-rate/src/main/java/com/forex/rate/infrastructure/repository/ExchangeRateRepositoryImpl.java',
  samplingController: 'forex-risk/src/main/java/com/forex/risk/adapter/controller/SamplingController.java',
  samplingRuleEngine: 'forex-risk/src/main/java/com/forex/risk/application/service/SamplingRuleEngine.java',
  samplingRuleEngineTest: 'forex-risk/src/test/java/com/forex/risk/application/service/SamplingRuleEngineTest.java',
  globalExceptionHandler: 'forex-common/forex-common-base/src/main/java/com/forex/common/base/exception/GlobalExceptionHandler.java',
};

const summaries = {
  [P.paymentController]: {
    summary: '提供跨境支付 REST API，覆盖汇入汇出创建、提交审批、AML 处理、发送、取消、GPI 状态更新以及 SWIFT、IBAN、费用和地址校验。该控制器把适配层 DTO 转换为应用层命令，并将领域聚合映射为统一响应。',
    tags: ['api-handler', '跨境支付', '权限控制', 'dto-mapping', '幂等控制'],
    languageNotes: 'Spring MVC 控制器结合 @RequirePermission、@RedisLock 和 @Idempotent 注解，把支付生命周期操作包裹为受控 HTTP endpoint。',
  },
  [P.exchangeRateMapper]: {
    summary: '定义汇率持久化查询的 MyBatis Mapper，提供最新汇率、全币种最新牌价、日期区间和分页历史查询。SQL 注解直接表达 t_exchange_rate 的过滤、排序和动态条件。',
    tags: ['data-access', 'mybatis', '汇率查询', 'sql-mapper'],
    languageNotes: 'Java interface 通过 MyBatis 注解声明 SQL，动态脚本条件用于可选查询参数。',
  },
  [P.exchangeRateRepository]: {
    summary: '实现汇率领域仓储，将 ExchangeRate 聚合与 ExchangeRatePO 持久化对象互相转换，并封装保存、按 ID 查询、最新牌价和分页查询。它把 MyBatis 分页结果转换为通用 PageResp 供应用层使用。',
    tags: ['repository', 'domain-mapping', '汇率', 'mybatis', 'pagination'],
  },
  [P.samplingController]: {
    summary: '提供资本项目便利化抽查的 REST API，管理抽查规则、评估交易抽查比例、生成和完成抽查任务，并计算覆盖率统计。控制器协调规则引擎与规则、任务仓储，将领域对象转换为前端可消费的 Map 视图。',
    tags: ['api-handler', '风险控制', '抽查规则', 'task-management', '权限控制'],
    languageNotes: 'Spring MVC endpoint 与领域转换逻辑集中在同一控制器中，包含多处中英双语业务日志。',
  },
  [P.samplingRuleEngine]: {
    summary: '实现资本项目便利化抽查规则引擎，根据活跃规则、交易属性、币种、国家和客户开户天数计算抽查比例。它还能基于候选交易生成可复现的抽查任务快照。',
    tags: ['service', 'rule-engine', '风险控制', 'json-condition', '抽样'],
    languageNotes: '规则条件以 JSON 解析为 Map 后进行匹配，稳定 hash bucket 让同一业务日期的抽样结果保持可复现。',
  },
  [P.samplingRuleEngineTest]: {
    summary: '验证 SamplingRuleEngine 在无规则、多规则取最高抽查率、开户天数条件和自动生成抽查任务场景下的行为。测试使用内存仓储桩，避免在受限 JDK 中依赖 Mockito runtime agent。',
    tags: ['test', 'unit-test', 'rule-engine', '风险控制', 'stub'],
  },
  [P.globalExceptionHandler]: {
    summary: '提供全局 REST 异常处理，将业务异常、参数错误、请求体解析失败、方法不支持、数据冲突和未知异常统一转换为 R 响应。每个处理器会记录 traceId，便于跨服务问题追踪。',
    tags: ['api-handler', 'exception-handling', '统一响应', 'trace-id', 'spring-advice'],
    languageNotes: '@RestControllerAdvice 与多个 @ExceptionHandler 方法组合，集中处理 Spring Web 和业务异常。',
  },
};

const functionSummaries = {
  [P.paymentController]: {
    createOutwardPayment: ['创建汇出支付请求，记录关键业务日志，将 CreatePaymentReq 转换为 CreatePaymentCmd 后调用 PaymentAppService 创建领域聚合。', ['endpoint', '跨境支付', '幂等控制']],
    createInwardPayment: ['创建汇入支付请求，沿用相同命令映射流程并调用应用服务生成汇入支付记录。', ['endpoint', '跨境支付', '幂等控制']],
    getPaymentDetail: ['按支付编号查询支付详情，并将 CrossBorderPayment 聚合映射为 PaymentResp。', ['endpoint', '查询', 'response-mapping']],
    pageQuery: ['将分页查询 DTO 转换为 PaymentQuery，调用应用服务分页检索并组装 PageResp 响应。', ['endpoint', 'pagination', 'dto-mapping']],
    submitPayment: ['提交指定支付单并重新读取详情，返回提交后的最新支付状态。', ['endpoint', '状态流转', 'redis-lock']],
    approvePayment: ['审批指定支付单并返回审批后的支付详情。', ['endpoint', '审批', 'redis-lock']],
    processAmlCheck: ['接收 AML 检查结果并把通过标记与原因传递给支付应用服务。', ['endpoint', 'aml', '状态流转']],
    sendPayment: ['发送支付并记录 SWIFT/CIPS 引用信息，使用锁和幂等键保护重复发送。', ['endpoint', '支付发送', '幂等控制']],
    cancelPayment: ['取消指定支付单并记录取消原因。', ['endpoint', '状态流转', 'redis-lock']],
    updateGpiStatus: ['根据请求体更新支付 GPI 状态和 trackingId，并返回最新支付视图。', ['endpoint', 'gpi', '状态同步']],
    toCmd: ['把创建支付请求中的付款人、收款人、银行、用途、费用承担和日期字段完整复制到 CreatePaymentCmd。', ['dto-mapping', 'adapter', '命令转换']],
    batchSubmit: ['将批量请求中的每笔支付转换为命令列表，并调用 BatchPaymentService 执行批量汇款处理。', ['endpoint', 'batch-processing', '幂等控制']],
    validateSwift: ['调用银行代码校验服务验证 SWIFT/BIC 并返回自动补全结果。', ['endpoint', 'validation', 'swift']],
    validateIban: ['调用银行代码校验服务验证 IBAN 并返回校验状态。', ['endpoint', 'validation', 'iban']],
    calculateFee: ['根据渠道、费用承担方式和金额调用应用服务计算跨境支付费用。', ['endpoint', 'fee-calculation', 'validation']],
    validateAddress: ['校验结构化地址中的国家、城市和街道或楼号字段，返回缺失字段列表与中文提示。', ['endpoint', 'validation', '地址校验']],
    toPaymentQuery: ['把分页查询请求映射为领域查询对象，保留支付编号、客户、状态、币种和日期区间等筛选项。', ['dto-mapping', 'query', 'adapter']],
    toResp: ['把 CrossBorderPayment 聚合的金额、汇率、银行、状态、GPI 和审计字段映射为 PaymentResp。', ['response-mapping', 'domain-mapping', 'adapter']],
  },
  [P.exchangeRateRepository]: {
    save: ['根据领域对象是否已有 ID 执行插入或更新，并返回重新构建的 ExchangeRate 聚合。', ['repository', 'persistence', 'domain-mapping']],
    findById: ['通过 Mapper 按主键读取持久化对象，并转换为 Optional<ExchangeRate>。', ['repository', '查询', 'domain-mapping']],
    findLatestByCurrencyPair: ['查询指定货币对的最新有效汇率，并转换为领域聚合。', ['repository', '汇率查询', 'domain-mapping']],
    findLatestRates: ['读取每个货币对的最新有效牌价列表，并批量转换为领域对象。', ['repository', '汇率查询', 'stream-mapping']],
    pageQuery: ['构造 MyBatis Plus Page，执行历史牌价分页查询并转换为 PageResp<ExchangeRate>。', ['repository', 'pagination', '汇率查询']],
    toDomain: ['将 ExchangeRatePO 的持久化字段重建为 ExchangeRate 聚合。', ['domain-mapping', 'reconstitution', '汇率']],
    toPO: ['将 ExchangeRate 聚合的汇率、日期、来源和状态字段复制到 ExchangeRatePO。', ['persistence-mapping', 'domain-mapping', '汇率']],
  },
  [P.samplingController]: {
    listRules: ['读取全部抽查规则并转换为列表视图。', ['endpoint', '规则管理', '查询']],
    createRule: ['根据请求体构造新的 SamplingRule，保存后返回规则视图。', ['endpoint', '规则管理', '权限控制']],
    updateRule: ['加载已有规则，合并请求字段后保存更新结果。', ['endpoint', '规则管理', '更新']],
    updateRuleStatus: ['在规则存在时合并状态字段并保存，用于启用或停用抽查规则。', ['endpoint', '规则管理', '状态切换']],
    deleteRule: ['按 ID 删除抽查规则。', ['endpoint', '规则管理', '删除']],
    evaluate: ['提取交易属性并调用 SamplingRuleEngine 计算适用抽查比例。', ['endpoint', 'rule-engine', '风险评估']],
    generateTasks: ['按请求日期生成抽查任务快照，转换为领域任务后批量持久化。', ['endpoint', 'task-generation', '规则引擎']],
    listTasks: ['读取全部抽查任务并组装总数和任务列表。', ['endpoint', 'task-management', '查询']],
    completeTask: ['按 taskId 查找抽查任务，写入检查结果并返回完成后的任务视图。', ['endpoint', 'task-management', '状态流转']],
    getStatistics: ['基于持久化抽查任务和固定业务分母计算近 30 日覆盖率、金额和模块分布。', ['endpoint', 'statistics', '风险控制']],
    toRule: ['把请求字段、默认值和既有规则合并为可保存的 SamplingRule。', ['domain-mapping', '规则管理', 'adapter']],
    toRuleReq: ['将已有 SamplingRule 反向转换为请求对象，便于局部更新状态。', ['dto-mapping', '规则管理', 'adapter']],
    toRuleView: ['把 SamplingRule 转为包含条件、比例、模块、日期和状态字段的 Map 视图。', ['response-mapping', '规则管理', 'adapter']],
    toTask: ['把规则引擎返回的任务快照转换为可持久化 SamplingTask，包含命中规则、业务日期和创建时间。', ['domain-mapping', 'task-management', 'adapter']],
    toTaskView: ['将 SamplingTask 的业务字段、抽查结果和审计时间转换为 API 响应视图。', ['response-mapping', 'task-management', 'adapter']],
  },
  [P.samplingRuleEngine]: {
    evaluateTransaction: ['评估交易命中的活跃抽查规则，并返回四舍五入后的最高抽查比例。', ['rule-engine', '风险评估', '抽样']],
    generateSamplingTasks: ['遍历候选交易，按规则命中结果和稳定抽样桶生成待审核任务快照。', ['task-generation', 'rule-engine', '抽样']],
    evaluateMatchedRules: ['筛选活跃或自动抽取规则，按优先级排序后匹配模块和条件并计算最高抽查率。', ['rule-engine', 'condition-matching', '优先级']],
    parseCondition: ['将规则条件 JSON 解析为 Map，解析失败时记录告警并返回空条件。', ['json-condition', '解析', '容错']],
    conditionMatches: ['按金额上下限、币种、国家、客户 ID 和开户天数判断交易是否满足规则条件。', ['condition-matching', '风险控制', 'validation']],
    matchesOne: ['支持单值或集合条件与实际字符串进行大小写无关匹配。', ['utility', 'condition-matching', 'collection']],
    buildCandidateTransactions: ['构造固定的候选交易快照，覆盖支付、结售汇、交易和结算等业务模块。', ['data-seed', 'task-generation', '业务样本']],
    tx: ['生成单笔候选交易 Map，包含业务编号、客户、币种、国家、金额和开户天数。', ['factory', 'data-seed', '业务样本']],
  },
  [P.samplingRuleEngineTest]: {
    testEvaluateTransaction_NoRules: ['验证没有活跃规则时抽查比例返回 0.00。', ['unit-test', 'rule-engine', '边界场景']],
    testEvaluateTransaction_WithSamplingRules: ['验证多条匹配规则时返回最高抽查比例。', ['unit-test', 'rule-engine', '优先级']],
    testEvaluateTransaction_AccountAgeCondition: ['验证开户天数条件能够命中并返回预期抽查比例。', ['unit-test', 'condition-matching', '客户画像']],
    testGenerateSamplingTasks: ['验证自动抽取规则能生成待处理任务并带有命中规则信息。', ['unit-test', 'task-generation', '抽样']],
    createRule: ['构造测试用 SamplingRule，统一设置条件、比例、模块和启用状态。', ['test-helper', 'factory', 'rule-engine']],
  },
  [P.globalExceptionHandler]: {
    handleBusinessException: ['处理业务异常，保留业务错误码、消息和 traceId。', ['exception-handling', '业务异常', 'trace-id']],
    handleIllegalArgumentException: ['将非法参数异常转换为参数校验失败响应。', ['exception-handling', 'validation', 'trace-id']],
    handleValidationException: ['汇总字段校验错误信息，并返回统一参数校验失败响应。', ['exception-handling', 'validation', 'spring-web']],
    handleHttpMessageNotReadable: ['处理请求体解析失败，返回请求格式错误提示。', ['exception-handling', 'request-body', 'spring-web']],
    handleMissingParam: ['处理缺失请求参数异常，并在响应中指出缺少的参数名。', ['exception-handling', 'validation', 'spring-web']],
    handleMethodNotSupported: ['处理不支持的 HTTP 方法并返回 405 语义的统一响应。', ['exception-handling', 'http-method', 'spring-web']],
    handleDataIntegrityViolation: ['处理数据库完整性冲突，记录错误并返回业务冲突提示。', ['exception-handling', 'database', '数据冲突']],
    handleException: ['兜底处理未知系统异常，隐藏内部细节并返回系统繁忙提示。', ['exception-handling', 'fallback', 'trace-id']],
  },
};

const classSummaries = {
  PaymentController: ['跨境支付 API 控制器，集中暴露支付生命周期、批量提交、银行代码校验和费用计算 endpoint。', ['api-handler', '跨境支付', 'spring-mvc', 'adapter']],
  ExchangeRateMapper: ['汇率表的 MyBatis Mapper interface，承载最新牌价、历史查询和分页查询 SQL。', ['data-access', 'mybatis', 'sql-mapper', '汇率']],
  ExchangeRateRepositoryImpl: ['汇率领域仓储实现，负责领域聚合与持久化对象之间的转换以及分页结果封装。', ['repository', 'domain-mapping', 'mybatis', '汇率']],
  SamplingController: ['抽查规则和任务 API 控制器，连接规则引擎、规则仓储和任务仓储。', ['api-handler', '风险控制', 'spring-mvc', 'adapter']],
  SamplingRuleEngine: ['资本项目便利化抽查规则引擎，执行 JSON 条件匹配、抽查比例计算和任务生成。', ['service', 'rule-engine', '风险控制', '抽样']],
  SamplingRuleEngineTest: ['SamplingRuleEngine 的 JUnit 单元测试类，使用内存仓储桩覆盖核心规则匹配和任务生成路径。', ['test', 'unit-test', 'rule-engine', 'stub']],
  GlobalExceptionHandler: ['Spring 全局异常处理器，把 Web、业务和数据库异常统一转换为带 traceId 的响应。', ['api-handler', 'exception-handling', 'spring-advice', 'trace-id']],
};

function complexity(result) {
  if (result.nonEmptyLines > 200) return 'complex';
  if (result.nonEmptyLines >= 50) return 'moderate';
  return 'simple';
}

function fileNode(result) {
  const meta = summaries[result.path];
  return {
    id: `file:${result.path}`,
    type: 'file',
    name: path.basename(result.path),
    filePath: result.path,
    summary: meta.summary,
    tags: meta.tags,
    complexity: complexity(result),
    ...(meta.languageNotes ? { languageNotes: meta.languageNotes } : {}),
  };
}

function isExported(result, name) {
  return (result.exports || []).some(e => e.name === name);
}

function functionSignificant(result, fn) {
  return isExported(result, fn.name) || (fn.endLine - fn.startLine + 1) >= 10;
}

function addFunctionNode(nodes, result, fn) {
  const byFile = functionSummaries[result.path] || {};
  const meta = byFile[fn.name];
  if (!meta) return;
  const id = `function:${result.path}:${fn.name}`;
  if (nodes.some(n => n.id === id)) {
    const existing = nodes.find(n => n.id === id);
    existing.lineRange = [
      Math.min(existing.lineRange[0], fn.startLine),
      Math.max(existing.lineRange[1], fn.endLine),
    ];
    return;
  }
  nodes.push({
    id,
    type: 'function',
    name: fn.name,
    filePath: result.path,
    lineRange: [fn.startLine, fn.endLine],
    summary: meta[0],
    tags: meta[1],
    complexity: (fn.endLine - fn.startLine + 1) > 30 ? 'complex' : (fn.endLine - fn.startLine + 1) >= 10 ? 'moderate' : 'simple',
  });
}

function addClassNode(nodes, result, cls) {
  const meta = classSummaries[cls.name];
  if (!meta) return;
  nodes.push({
    id: `class:${result.path}:${cls.name}`,
    type: 'class',
    name: cls.name,
    filePath: result.path,
    lineRange: [cls.startLine, cls.endLine],
    summary: meta[0],
    tags: meta[1],
    complexity: (cls.endLine - cls.startLine + 1) > 200 ? 'complex' : 'moderate',
  });
}

function edge(source, target, type, weight) {
  if (source === target) return null;
  return { source, target, type, direction: 'forward', weight };
}

function dedupeEdges(edges) {
  const seen = new Set();
  return edges.filter(e => {
    if (!e) return false;
    const key = `${e.source}\u0000${e.target}\u0000${e.type}`;
    if (seen.has(key)) return false;
    seen.add(key);
    return true;
  });
}

function addImports(edges, batchImportData) {
  for (const [sourcePath, targets] of Object.entries(batchImportData)) {
    for (const targetPath of targets || []) {
      edges.push(edge(`file:${sourcePath}`, `file:${targetPath}`, 'imports', 0.7));
    }
  }
}

function addContainsAndExports(nodes, edges, result) {
  const fileId = `file:${result.path}`;
  for (const node of nodes.filter(n => n.filePath === result.path && n.id !== fileId)) {
    edges.push(edge(fileId, node.id, 'contains', 1.0));
  }
  for (const exp of result.exports || []) {
    const fnNode = nodes.find(n => n.id === `function:${result.path}:${exp.name}`);
    const clsNode = nodes.find(n => n.id === `class:${result.path}:${exp.name}`);
    const target = fnNode?.id || clsNode?.id;
    if (target) edges.push(edge(fileId, target, 'exports', 0.8));
  }
}

function addSemanticEdges(edges) {
  const f = path => `file:${path}`;
  const fn = (file, name) => `function:${file}:${name}`;
  const cls = (file, name) => `class:${file}:${name}`;

  edges.push(
    edge(fn(P.paymentController, 'createOutwardPayment'), fn(P.paymentController, 'toCmd'), 'calls', 0.8),
    edge(fn(P.paymentController, 'createOutwardPayment'), fn('forex-payment/src/main/java/com/forex/payment/application/service/PaymentAppService.java', 'createOutwardPayment'), 'calls', 0.8),
    edge(fn(P.paymentController, 'createOutwardPayment'), fn(P.paymentController, 'toResp'), 'calls', 0.8),
    edge(fn(P.paymentController, 'createInwardPayment'), fn(P.paymentController, 'toCmd'), 'calls', 0.8),
    edge(fn(P.paymentController, 'createInwardPayment'), fn('forex-payment/src/main/java/com/forex/payment/application/service/PaymentAppService.java', 'createInwardPayment'), 'calls', 0.8),
    edge(fn(P.paymentController, 'createInwardPayment'), fn(P.paymentController, 'toResp'), 'calls', 0.8),
    edge(fn(P.paymentController, 'pageQuery'), fn(P.paymentController, 'toPaymentQuery'), 'calls', 0.8),
    edge(fn(P.paymentController, 'pageQuery'), fn('forex-payment/src/main/java/com/forex/payment/application/service/PaymentAppService.java', 'pageQuery'), 'calls', 0.8),
    edge(fn(P.paymentController, 'submitPayment'), fn('forex-payment/src/main/java/com/forex/payment/application/service/PaymentAppService.java', 'submitPayment'), 'calls', 0.8),
    edge(fn(P.paymentController, 'approvePayment'), fn('forex-payment/src/main/java/com/forex/payment/application/service/PaymentAppService.java', 'approvePayment'), 'calls', 0.8),
    edge(fn(P.paymentController, 'processAmlCheck'), fn('forex-payment/src/main/java/com/forex/payment/application/service/PaymentAppService.java', 'processAmlCheck'), 'calls', 0.8),
    edge(fn(P.paymentController, 'sendPayment'), fn('forex-payment/src/main/java/com/forex/payment/application/service/PaymentAppService.java', 'sendPayment'), 'calls', 0.8),
    edge(fn(P.paymentController, 'cancelPayment'), fn('forex-payment/src/main/java/com/forex/payment/application/service/PaymentAppService.java', 'cancelPayment'), 'calls', 0.8),
    edge(fn(P.paymentController, 'updateGpiStatus'), fn('forex-payment/src/main/java/com/forex/payment/application/service/PaymentAppService.java', 'updateGpiStatus'), 'calls', 0.8),
    edge(fn(P.paymentController, 'batchSubmit'), fn(P.paymentController, 'toCmd'), 'calls', 0.8),
    edge(fn(P.paymentController, 'batchSubmit'), fn('forex-payment/src/main/java/com/forex/payment/application/service/BatchPaymentService.java', 'processBatch'), 'calls', 0.8),
    edge(fn(P.paymentController, 'validateSwift'), fn('forex-payment/src/main/java/com/forex/payment/application/service/BankCodeValidationService.java', 'validateSwiftCode'), 'calls', 0.8),
    edge(fn(P.paymentController, 'validateSwift'), fn('forex-payment/src/main/java/com/forex/payment/application/service/BankCodeValidationService.java', 'autoCompleteBic'), 'calls', 0.8),
    edge(fn(P.paymentController, 'validateIban'), fn('forex-payment/src/main/java/com/forex/payment/application/service/BankCodeValidationService.java', 'validateIban'), 'calls', 0.8),
    edge(fn(P.paymentController, 'calculateFee'), fn('forex-payment/src/main/java/com/forex/payment/application/service/BankCodeValidationService.java', 'calculateFee'), 'calls', 0.8),
    edge(fn(P.exchangeRateRepository, 'pageQuery'), fn('forex-common/forex-common-base/src/main/java/com/forex/common/base/dto/PageResp.java', 'of'), 'calls', 0.8),
    edge(fn(P.exchangeRateRepository, 'toDomain'), fn('forex-rate/src/main/java/com/forex/rate/domain/model/aggregate/ExchangeRate.java', 'reconstitute'), 'calls', 0.8),
    edge(cls(P.exchangeRateRepository, 'ExchangeRateRepositoryImpl'), cls('forex-rate/src/main/java/com/forex/rate/domain/repository/ExchangeRateRepository.java', 'ExchangeRateRepository'), 'implements', 0.9),
    edge(cls(P.exchangeRateMapper, 'ExchangeRateMapper'), cls('forex-common/forex-common-mybatis/src/main/java/com/forex/common/mybatis/base/BaseMapperExt.java', 'BaseMapperExt'), 'inherits', 0.9),
    edge(fn(P.samplingController, 'evaluate'), fn(P.samplingRuleEngine, 'evaluateTransaction'), 'calls', 0.8),
    edge(fn(P.samplingController, 'generateTasks'), fn(P.samplingRuleEngine, 'generateSamplingTasks'), 'calls', 0.8),
    edge(f(P.samplingController), f(P.samplingRuleEngine), 'depends_on', 0.6),
    edge(fn(P.samplingRuleEngine, 'evaluateTransaction'), fn(P.samplingRuleEngine, 'evaluateMatchedRules'), 'calls', 0.8),
    edge(fn(P.samplingRuleEngine, 'generateSamplingTasks'), fn(P.samplingRuleEngine, 'evaluateMatchedRules'), 'calls', 0.8),
    edge(fn(P.samplingRuleEngine, 'generateSamplingTasks'), fn(P.samplingRuleEngine, 'buildCandidateTransactions'), 'calls', 0.8),
    edge(fn(P.samplingRuleEngine, 'evaluateMatchedRules'), fn(P.samplingRuleEngine, 'parseCondition'), 'calls', 0.8),
    edge(fn(P.samplingRuleEngine, 'evaluateMatchedRules'), fn(P.samplingRuleEngine, 'conditionMatches'), 'calls', 0.8),
    edge(fn(P.samplingRuleEngine, 'conditionMatches'), fn(P.samplingRuleEngine, 'matchesOne'), 'calls', 0.8),
    edge(fn(P.samplingRuleEngine, 'buildCandidateTransactions'), fn(P.samplingRuleEngine, 'tx'), 'calls', 0.8),
    edge(f(P.samplingRuleEngine), f(P.samplingRuleEngineTest), 'tested_by', 0.5),
    edge(fn(P.samplingRuleEngineTest, 'testEvaluateTransaction_NoRules'), fn(P.samplingRuleEngine, 'evaluateTransaction'), 'calls', 0.8),
    edge(fn(P.samplingRuleEngineTest, 'testEvaluateTransaction_WithSamplingRules'), fn(P.samplingRuleEngine, 'evaluateTransaction'), 'calls', 0.8),
    edge(fn(P.samplingRuleEngineTest, 'testEvaluateTransaction_AccountAgeCondition'), fn(P.samplingRuleEngine, 'evaluateTransaction'), 'calls', 0.8),
    edge(fn(P.samplingRuleEngineTest, 'testGenerateSamplingTasks'), fn(P.samplingRuleEngine, 'generateSamplingTasks'), 'calls', 0.8),
    edge(fn(P.globalExceptionHandler, 'handleBusinessException'), fn('forex-common/forex-common-base/src/main/java/com/forex/common/base/result/R.java', 'fail'), 'calls', 0.8),
    edge(fn(P.globalExceptionHandler, 'handleIllegalArgumentException'), fn('forex-common/forex-common-base/src/main/java/com/forex/common/base/result/R.java', 'fail'), 'calls', 0.8),
    edge(fn(P.globalExceptionHandler, 'handleValidationException'), fn('forex-common/forex-common-base/src/main/java/com/forex/common/base/result/R.java', 'fail'), 'calls', 0.8),
    edge(fn(P.globalExceptionHandler, 'handleException'), fn('forex-common/forex-common-base/src/main/java/com/forex/common/base/result/R.java', 'fail'), 'calls', 0.8),
  );
}

function neighborSymbolAllowed(neighborMap, target) {
  const match = /^(function|class):(.+):([^:]+)$/.exec(target);
  if (!match) return false;
  const [, , filePath, symbol] = match;
  return Object.values(neighborMap).flat().some(n => n.path === filePath && (n.symbols || []).includes(symbol));
}

function validate(idx, fragment, batch, expectedImportCount) {
  const ids = new Set(fragment.nodes.map(n => n.id));
  if (ids.size !== fragment.nodes.length) throw new Error(`batch ${idx}: duplicate node IDs`);
  const actualImports = fragment.edges.filter(e => e.type === 'imports').length;
  if (actualImports !== expectedImportCount) {
    throw new Error(`batch ${idx}: expected ${expectedImportCount} import edges, got ${actualImports}`);
  }
  const batchImportData = batch.batchImportData || {};
  const importFiles = new Set([
    ...Object.keys(batchImportData),
    ...Object.values(batchImportData).flat(),
  ]);
  const neighborFiles = new Set(Object.values(batch.neighborMap || {}).flat().map(n => n.path));
  for (const e of fragment.edges) {
    for (const side of ['source', 'target']) {
      const id = e[side];
      if (ids.has(id)) continue;
      if (id.startsWith('file:')) {
        const p = id.slice('file:'.length);
        if (importFiles.has(p) || neighborFiles.has(p)) continue;
      }
      if ((id.startsWith('function:') || id.startsWith('class:')) && neighborSymbolAllowed(batch.neighborMap || {}, id)) continue;
      throw new Error(`batch ${idx}: ${side} ${id} is not locally present or allowed by import/neighbor context`);
    }
  }
}

const allResults = new Map();
for (const idx of selected) {
  const resultPath = path.join(uaDir, 'tmp', `ua-file-extract-results-${idx}.json`);
  const extraction = JSON.parse(fs.readFileSync(resultPath, 'utf8'));
  if (!extraction.scriptCompleted || extraction.filesSkipped?.length) {
    throw new Error(`batch ${idx}: extraction incomplete or skipped files present`);
  }
  for (const result of extraction.results) allResults.set(result.path, result);
}

const report = [];
for (const idx of selected) {
  const batch = batches.find(b => b.batchIndex === idx) || batches[idx];
  const batchFiles = (batch.files || batch.batchFiles || []).map(f => f.path);
  const nodes = [];
  const edges = [];

  for (const filePath of batchFiles) {
    const result = allResults.get(filePath);
    nodes.push(fileNode(result));
    for (const cls of result.classes || []) {
      if ((cls.methods?.length || 0) >= 2 || (cls.endLine - cls.startLine + 1) >= 20 || isExported(result, cls.name)) {
        addClassNode(nodes, result, cls);
      }
    }
    for (const fn of result.functions || []) {
      if (functionSignificant(result, fn)) addFunctionNode(nodes, result, fn);
    }
  }

  addImports(edges, batch.batchImportData || {});
  for (const filePath of batchFiles) addContainsAndExports(nodes, edges, allResults.get(filePath));
  addSemanticEdges(edges);

  const localFileSet = new Set(batchFiles);
  const localNodeIds = new Set(nodes.map(n => n.id));
  const filteredEdges = dedupeEdges(edges).filter(e => {
    const sourceFileMatch = /^file:(.+)$/.exec(e.source) || /^(?:function|class):(.+):[^:]+$/.exec(e.source);
    return sourceFileMatch && localFileSet.has(sourceFileMatch[1]) && (localNodeIds.has(e.source) || e.type === 'imports');
  });

  const fragment = { nodes, edges: filteredEdges };
  const expectedImportCount = Object.values(batch.batchImportData || {}).reduce((n, arr) => n + (arr || []).length, 0);
  validate(idx, fragment, batch, expectedImportCount);

  const outPath = path.join(uaDir, 'intermediate', `batch-${idx}.json`);
  fs.writeFileSync(outPath, JSON.stringify(fragment, null, 2));
  report.push({ file: outPath, nodes: fragment.nodes.length, edges: fragment.edges.length });
}

for (const row of report) {
  console.log(`${path.relative(projectRoot, row.file)} nodes=${row.nodes} edges=${row.edges}`);
}
