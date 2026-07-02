import fs from 'fs';

const results = JSON.parse(fs.readFileSync('/Users/guoziyan/Desktop/java-project/project-ai/forex-bank-system/.understand-anything/tmp/ua-file-extract-results-3.json','utf8')).results;
const input = JSON.parse(fs.readFileSync('/Users/guoziyan/Desktop/java-project/project-ai/forex-bank-system/.understand-anything/tmp/ua-file-analyzer-input-3.json','utf8'));
const importData = input.batchImportData;

const nodes = [];
const edges = [];
const edgeSet = new Set();
function addEdge(source, target, type, weight){
  const k = source+'|'+target+'|'+type;
  if(edgeSet.has(k)) return;
  edgeSet.add(k);
  edges.push({source, target, type, direction:'forward', weight});
}

// resolve java package import to file path
function resolveImport(imp){
  const m = imp.match(/import\s+(?:static\s+)?([\w.]+);/);
  if(!m) return null;
  const fqn = m[1];
  if(fqn.startsWith('com.forex.account.')){
    return 'forex-account/src/main/java/'+fqn.replace(/\./g,'/')+'.java';
  }
  if(fqn.startsWith('com.forex.common.')){
    return 'forex-common/src/main/java/'+fqn.replace(/\./g,'/')+'.java';
  }
  return null;
}

// per-file chinese summaries + tags
const fileMeta = {
  'docker/docker-compose.yml': {s:'Docker Compose 编排文件，定义 MySQL、Redis、Nacos 等基础设施服务及网络、数据卷。', t:['docker','部署','基础设施'], c:'medium'},
  'docker/nacos/conf/application.properties': {s:'Nacos 配置中心的应用配置文件，设置数据源等启动参数。', t:['nacos','配置','注册中心'], c:'low'},
  'forex-account/pom.xml': {s:'forex-account 账户微服务的 Maven 构建配置，声明依赖与父模块。', t:['maven','构建','依赖管理'], c:'low'},
  'forex-account/src/main/java/com/forex/account/AccountApplication.java': {s:'账户服务 Spring Boot 启动类，启用服务发现与 MyBatis Mapper 扫描。', t:['启动类','SpringBoot','微服务'], c:'low'},
  'forex-account/src/main/java/com/forex/account/adapter/controller/AccountController.java': {s:'账户 REST 控制器，提供开户、销户、存取款、冻结解冻、查询交易等 HTTP 接口，并将领域对象转换为响应 DTO。', t:['controller','REST','适配层'], c:'high'},
  'forex-account/src/main/java/com/forex/account/adapter/dto/AccountOperationReq.java': {s:'账户操作（存款/取款/冻结）请求 DTO，含金额与业务关联号校验。', t:['dto','请求','校验'], c:'low'},
  'forex-account/src/main/java/com/forex/account/adapter/dto/AccountResp.java': {s:'账户信息响应 DTO，返回账户余额、状态、利率等完整字段。', t:['dto','响应'], c:'low'},
  'forex-account/src/main/java/com/forex/account/adapter/dto/OpenAccountReq.java': {s:'开户请求 DTO，包含客户号、账户类型、币种等字段及校验。', t:['dto','请求','开户'], c:'low'},
  'forex-account/src/main/java/com/forex/account/adapter/dto/TransactionResp.java': {s:'交易流水响应 DTO，返回交易类型、金额、前后余额等信息。', t:['dto','响应','交易'], c:'low'},
  'forex-account/src/main/java/com/forex/account/application/command/AccountOperationCmd.java': {s:'账户操作应用层命令对象，封装存取款/冻结等参数。', t:['command','应用层','CQRS'], c:'low'},
  'forex-account/src/main/java/com/forex/account/application/command/OpenAccountCmd.java': {s:'开户应用层命令对象，封装开户所需参数。', t:['command','应用层','CQRS'], c:'low'},
  'forex-account/src/main/java/com/forex/account/application/query/AccountDetailDTO.java': {s:'账户详情查询 DTO，用于查询侧返回账户明细数据。', t:['query','dto','CQRS'], c:'low'},
  'forex-account/src/main/java/com/forex/account/application/query/AccountQuery.java': {s:'账户分页查询条件对象，继承分页请求，含客户号、状态、币种过滤。', t:['query','分页','CQRS'], c:'low'},
  'forex-account/src/main/java/com/forex/account/application/query/TransactionDTO.java': {s:'交易流水查询 DTO，用于查询侧返回交易明细数据。', t:['query','dto','CQRS'], c:'low'},
  'forex-account/src/main/java/com/forex/account/application/service/AccountAppService.java': {s:'账户应用服务，编排领域服务、仓储与事件发布，实现开户、销户、存取款、冻结解冻及交易查询等用例，并管理事务。', t:['应用服务','用例编排','事务','事件发布'], c:'high'},
  'forex-account/src/main/java/com/forex/account/domain/event/AccountOpenedEvent.java': {s:'账户已开户领域事件，携带账户号、客户号、币种等信息。', t:['领域事件','DDD'], c:'low'},
  'forex-account/src/main/java/com/forex/account/domain/event/BalanceChangedEvent.java': {s:'余额变动领域事件，携带交易金额、类型与变动后余额。', t:['领域事件','DDD'], c:'low'},
  'forex-account/src/main/java/com/forex/account/domain/model/aggregate/ForexAccount.java': {s:'外汇账户聚合根，封装账户核心业务规则：存取款、冻结解冻、销户、可用余额计算与校验。', t:['聚合根','DDD','领域模型','业务规则'], c:'high'},
  'forex-account/src/main/java/com/forex/account/domain/model/entity/AccountTransaction.java': {s:'账户交易流水实体，通过工厂方法 record 创建交易记录。', t:['实体','DDD','交易'], c:'medium'},
  'forex-account/src/main/java/com/forex/account/domain/model/valueobject/AccountNumber.java': {s:'账号值对象，保证账号非空并提供相等性语义。', t:['值对象','DDD'], c:'low'},
  'forex-account/src/main/java/com/forex/account/domain/model/valueobject/Money.java': {s:'金额值对象，封装金额与币种，提供加减、比较等运算并校验币种一致性。', t:['值对象','DDD','金额'], c:'medium'},
  'forex-account/src/main/java/com/forex/account/domain/repository/AccountRepository.java': {s:'账户仓储接口，定义账户持久化与查询契约。', t:['仓储','接口','DDD'], c:'low'},
  'forex-account/src/main/java/com/forex/account/domain/repository/TransactionRepository.java': {s:'交易流水仓储接口，定义交易持久化与查询契约。', t:['仓储','接口','DDD'], c:'low'},
  'forex-account/src/main/java/com/forex/account/domain/service/AccountDomainService.java': {s:'账户领域服务，处理跨聚合逻辑：开户、销户、冻结解冻、记录交易与账号生成。', t:['领域服务','DDD','业务逻辑'], c:'high'},
  'forex-account/src/main/java/com/forex/account/infrastructure/mapper/AccountTransactionMapper.java': {s:'账户交易 MyBatis-Plus Mapper，提供交易流水的持久化查询。', t:['mapper','持久化','MyBatis'], c:'low'},
};

// class -> file map (batch) and methods
const classToFile = {};
const classMethods = {};
for(const r of results){
  if(!r.classes) continue;
  for(const c of r.classes){
    classToFile[c.name] = r.path;
    classMethods[c.name] = new Set(c.methods||[]);
  }
}

// receiver variable -> class name
const receiverMap = {
  accountAppService:'AccountAppService',
  accountDomainService:'AccountDomainService',
  accountRepository:'AccountRepository',
  transactionRepository:'TransactionRepository',
  account:'ForexAccount',
  saved:'ForexAccount',
};

function fileTypeFor(cat){ return cat==='config'?'config':'file'; }

// build nodes
for(const r of results){
  const meta = fileMeta[r.path] || {s:r.path, t:['代码'], c:'low'};
  const type = fileTypeFor(r.fileCategory);
  const name = r.path.split('/').pop();
  nodes.push({
    id:'file:'+r.path,
    type,
    name,
    filePath:r.path,
    summary:meta.s,
    tags:meta.t,
    complexity:meta.c
  });

  if(r.language!=='java') continue;

  const funcNames = new Set((r.functions||[]).map(f=>f.name));
  // class nodes
  for(const c of (r.classes||[])){
    const cid = 'class:'+r.path+':'+c.name;
    nodes.push({
      id:cid,
      type:'class',
      name:c.name,
      filePath:r.path,
      summary:(fileMeta[r.path]?fileMeta[r.path].s:c.name),
      tags:meta.t,
      complexity:(c.methods&&c.methods.length>6)?'high':(c.methods&&c.methods.length>2?'medium':'low')
    });
    addEdge('file:'+r.path, cid, 'contains', 1.0);
    // contains edges to functions
    for(const m of (c.methods||[])){
      if(funcNames.has(m)){
        addEdge(cid, 'function:'+r.path+':'+m, 'contains', 1.0);
      }
    }
  }
  // function nodes
  for(const f of (r.functions||[])){
    const fid='function:'+r.path+':'+f.name;
    nodes.push({
      id:fid,
      type:'function',
      name:f.name,
      filePath:r.path,
      summary:'方法 '+f.name+'('+(f.params||[]).join(', ')+')',
      tags:['方法'],
      complexity:'low'
    });
    // file contains function (if not already via class)
    addEdge('file:'+r.path, fid, 'contains', 1.0);
  }
}

// imports edges
for(const [fp, imps] of Object.entries(importData)){
  for(const imp of imps){
    const target = resolveImport(imp);
    if(!target) continue;
    addEdge('file:'+fp, 'file:'+target, 'imports', 0.7);
  }
}

// calls edges
for(const r of results){
  if(!r.callGraph) continue;
  const funcNames = new Set((r.functions||[]).map(f=>f.name));
  for(const e of r.callGraph){
    const caller = e.caller;
    if(!funcNames.has(caller)) continue;
    const callerId='function:'+r.path+':'+caller;
    let callee = e.callee;
    let targetClass=null, method=null;
    if(callee.startsWith('new ')){
      const cn = callee.slice(4).split(/[^\w]/)[0];
      if(classToFile[cn]){ targetClass=cn; method=null; }
    } else if(callee.includes('.')){
      const recv = callee.split('.')[0];
      let rest = callee.split('.')[1]||'';
      method = rest.split('(')[0];
      if(receiverMap[recv]){ targetClass=receiverMap[recv]; }
      else if(classToFile[recv]){ targetClass=recv; } // static call e.g. ForexAccount.create
    } else {
      // bare name -> same file method
      const m = callee.split('(')[0];
      if(funcNames.has(m) && m!==caller){
        addEdge(callerId, 'function:'+r.path+':'+m, 'calls', 0.8);
      }
      continue;
    }
    if(!targetClass) continue;
    const tf = classToFile[targetClass];
    if(!tf) continue;
    if(method && classMethods[targetClass] && classMethods[targetClass].has(method)){
      addEdge(callerId, 'function:'+tf+':'+method, 'calls', 0.8);
    } else if(!method){
      // constructor -> link to class node
      addEdge(callerId, 'class:'+tf+':'+targetClass, 'depends_on', 0.6);
    }
  }
}

fs.writeFileSync('/Users/guoziyan/Desktop/java-project/project-ai/forex-bank-system/.understand-anything/intermediate/batch-3.json', JSON.stringify({nodes, edges}, null, 2));
console.log('nodes:', nodes.length, 'edges:', edges.length);
const byType={}; for(const n of nodes) byType[n.type]=(byType[n.type]||0)+1;
const eByType={}; for(const e of edges) eByType[e.type]=(eByType[e.type]||0)+1;
console.log('nodeTypes:', JSON.stringify(byType));
console.log('edgeTypes:', JSON.stringify(eByType));
