import json, os

ROOT = "/Users/guoziyan/Desktop/java-project/project-ai/forex-bank-system"
res = json.load(open(f"{ROOT}/.understand-anything/intermediate/ua-file-extract-results-20.json")) if os.path.exists(f"{ROOT}/.understand-anything/intermediate/ua-file-extract-results-20.json") else json.load(open(f"{ROOT}/.understand-anything/tmp/ua-file-extract-results-20.json"))
inp = json.load(open(f"{ROOT}/.understand-anything/intermediate/input-20.json"))
importData = inp["batchImportData"]

def resolve(imp):
    q = imp.replace("import ", "").replace(";", "").strip()
    if not q.startswith("com.forex."):
        return None
    parts = q.split(".")
    fqcn = "/".join(parts) + ".java"
    if q.startswith("com.forex.common.base."):
        p = "forex-common/forex-common-base/src/main/java/" + fqcn
    elif q.startswith("com.forex.common.mybatis."):
        p = "forex-common/forex-common-mybatis/src/main/java/" + fqcn
    elif q.startswith("com.forex.common.security."):
        p = "forex-common/forex-common-security/src/main/java/" + fqcn
    elif q.startswith("com.forex.notification."):
        p = "forex-notification/src/main/java/" + fqcn
    elif q.startswith("com.forex.ocr."):
        p = "forex-ocr/src/main/java/" + fqcn
    else:
        return None
    return p if os.path.exists(f"{ROOT}/{p}") else None

# Chinese file summaries and tags keyed by path basename+dir hint
meta = {
"forex-notification/src/main/java/com/forex/notification/domain/model/aggregate/Notification.java":
 ("通知聚合根，封装通知的标题、内容、通知方式、目标用户及发送状态，提供创建、发送、标记成功/失败等领域行为。", ["data-model","aggregate","domain","notification"], "complex"),
"forex-notification/src/main/java/com/forex/notification/domain/repository/NoticeRepository.java":
 ("公告仓储接口，定义公告的保存、按 ID 查询与分页查询等持久化契约。", ["repository","domain","interface","notification"], "simple"),
"forex-notification/src/main/java/com/forex/notification/domain/repository/NotificationRepository.java":
 ("通知仓储接口，定义通知的保存、按 ID/业务号查询与分页查询等持久化契约。", ["repository","domain","interface","notification"], "simple"),
"forex-notification/src/main/java/com/forex/notification/domain/service/NotificationDomainService.java":
 ("通知领域服务，负责发送通知、创建并发布公告，并在发送成功后发布通知事件。", ["service","domain","event-handler","notification"], "moderate"),
"forex-notification/src/main/java/com/forex/notification/infrastructure/mapper/NoticeMapper.java":
 ("公告 MyBatis-Plus Mapper，提供公告持久化对象的分页查询能力。", ["data-access","mybatis","mapper","notification"], "simple"),
"forex-notification/src/main/java/com/forex/notification/infrastructure/mapper/NotificationMapper.java":
 ("通知 MyBatis-Plus Mapper，提供按业务号查询及分页查询通知持久化对象的能力。", ["data-access","mybatis","mapper","notification"], "simple"),
"forex-notification/src/main/java/com/forex/notification/infrastructure/persistence/NoticePO.java":
 ("公告持久化对象，映射公告数据库表字段。", ["data-model","persistence","po","notification"], "simple"),
"forex-notification/src/main/java/com/forex/notification/infrastructure/persistence/NotificationPO.java":
 ("通知持久化对象，映射通知数据库表字段。", ["data-model","persistence","po","notification"], "simple"),
"forex-notification/src/main/java/com/forex/notification/infrastructure/repository/NoticeRepositoryImpl.java":
 ("公告仓储实现，完成公告领域对象与持久化对象的转换及数据库读写。", ["repository","persistence","mapper-impl","notification"], "moderate"),
"forex-notification/src/main/java/com/forex/notification/infrastructure/repository/NotificationRepositoryImpl.java":
 ("通知仓储实现，完成通知领域对象与持久化对象的转换及数据库读写。", ["repository","persistence","mapper-impl","notification"], "moderate"),
"forex-notification/src/main/java/com/forex/notification/NotificationApplication.java":
 ("通知服务 Spring Boot 启动类，作为 forex-notification 微服务的入口点。", ["entry-point","bootstrap","spring-boot","notification"], "simple"),
"forex-notification/src/main/resources/application.yml":
 ("通知服务的 Spring Boot 配置文件，定义端口、数据源、注册中心等运行时参数。", ["configuration","spring-boot","yaml","notification"], "simple"),
"forex-ocr/pom.xml":
 ("forex-ocr 模块的 Maven 构建配置，声明 OCR 服务的依赖与打包方式。", ["configuration","build-system","maven","ocr"], "simple"),
"forex-ocr/src/main/java/com/forex/ocr/adapter/controller/OcrController.java":
 ("OCR 识别 REST 控制器，提供单据上传、OCR 处理、结果查询与分页查询接口。", ["api-handler","controller","rest","ocr"], "moderate"),
"forex-ocr/src/main/java/com/forex/ocr/adapter/dto/OcrTaskResp.java":
 ("OCR 任务响应 DTO，向前端返回任务状态、识别结果与字段等信息。", ["dto","serialization","response","ocr"], "simple"),
"forex-ocr/src/main/java/com/forex/ocr/adapter/dto/OcrUploadReq.java":
 ("OCR 上传请求 DTO，封装单据类型等上传参数。", ["dto","request","validation","ocr"], "simple"),
"forex-ocr/src/main/java/com/forex/ocr/application/query/OcrQuery.java":
 ("OCR 任务分页查询条件对象，封装状态、单据类型等过滤参数。", ["query","dto","pagination","ocr"], "simple"),
"forex-ocr/src/main/java/com/forex/ocr/application/service/OcrAppService.java":
 ("OCR 应用服务，编排上传、处理、结果查询与分页查询等用例，协调领域服务与仓储。", ["service","application","orchestration","ocr"], "simple"),
"forex-ocr/src/main/java/com/forex/ocr/domain/event/OcrCompletedEvent.java":
 ("OCR 完成领域事件，携带任务 ID 与单据类型，在识别完成时对外发布。", ["event","domain","event-handler","ocr"], "simple"),
"forex-ocr/src/main/java/com/forex/ocr/domain/model/aggregate/OcrTask.java":
 ("OCR 任务聚合根，管理单据识别任务的生命周期与状态流转，提供创建、开始处理、完成、失败等领域行为。", ["data-model","aggregate","domain","ocr"], "complex"),
"forex-ocr/src/main/java/com/forex/ocr/domain/repository/OcrTaskRepository.java":
 ("OCR 任务仓储接口，定义任务的保存、按 ID/任务号查询与分页查询契约。", ["repository","domain","interface","ocr"], "simple"),
"forex-ocr/src/main/java/com/forex/ocr/domain/service/OcrDomainService.java":
 ("OCR 领域服务，实现单据上传、OCR 识别处理（含模拟识别）与结果查询等核心业务逻辑。", ["service","domain","business-logic","ocr"], "moderate"),
"forex-ocr/src/main/java/com/forex/ocr/infrastructure/mapper/OcrTaskMapper.java":
 ("OCR 任务 MyBatis-Plus Mapper，提供按任务号查询、条件查询及分页查询能力。", ["data-access","mybatis","mapper","ocr"], "simple"),
"forex-ocr/src/main/java/com/forex/ocr/infrastructure/persistence/OcrTaskPO.java":
 ("OCR 任务持久化对象，映射 OCR 任务数据库表字段。", ["data-model","persistence","po","ocr"], "simple"),
"forex-ocr/src/main/java/com/forex/ocr/infrastructure/repository/OcrTaskRepositoryImpl.java":
 ("OCR 任务仓储实现，完成任务领域对象与持久化对象的转换及数据库读写。", ["repository","persistence","mapper-impl","ocr"], "moderate"),
}

# per-class Chinese summaries
cls_sum = {
"Notification":"通知聚合根，维护通知内容与发送状态并封装领域行为。",
"NoticeRepository":"公告仓储接口，声明公告持久化操作契约。",
"NotificationRepository":"通知仓储接口，声明通知持久化操作契约。",
"NotificationDomainService":"通知领域服务，编排通知发送与公告发布逻辑。",
"NoticeMapper":"公告 MyBatis-Plus Mapper 接口。",
"NotificationMapper":"通知 MyBatis-Plus Mapper 接口。",
"NoticePO":"公告持久化对象，映射数据库表。",
"NotificationPO":"通知持久化对象，映射数据库表。",
"NoticeRepositoryImpl":"公告仓储实现，负责领域对象与持久化对象转换及读写。",
"NotificationRepositoryImpl":"通知仓储实现，负责领域对象与持久化对象转换及读写。",
"NotificationApplication":"通知微服务 Spring Boot 启动类。",
"OcrController":"OCR 识别 REST 控制器，暴露上传、处理与查询接口。",
"OcrTaskResp":"OCR 任务响应 DTO。",
"OcrUploadReq":"OCR 上传请求 DTO。",
"OcrQuery":"OCR 任务分页查询条件对象。",
"OcrAppService":"OCR 应用服务，编排 OCR 相关用例。",
"OcrCompletedEvent":"OCR 完成领域事件。",
"OcrTask":"OCR 任务聚合根，管理任务生命周期与状态。",
"OcrTaskRepository":"OCR 任务仓储接口。",
"OcrDomainService":"OCR 领域服务，实现识别处理核心逻辑。",
"OcrTaskMapper":"OCR 任务 MyBatis-Plus Mapper 接口。",
"OcrTaskPO":"OCR 任务持久化对象，映射数据库表。",
"OcrTaskRepositoryImpl":"OCR 任务仓储实现，负责对象转换及读写。",
}

def fn_summary(name):
    m = {
    "create":"工厂方法，创建并初始化一个新的聚合实例。",
    "reconstitute":"重建方法，从持久化数据还原聚合实例。",
    "send":"执行通知发送逻辑并推进状态。",
    "markSent":"将通知标记为发送成功。",
    "markFailed":"将通知标记为发送失败并记录原因。",
    "validate":"校验聚合字段的合法性。",
    "sendNotification":"发送通知并在成功后发布通知事件。",
    "createNotice":"创建并保存公告。",
    "publishNotice":"发布公告使其对用户可见。",
    "save":"保存领域对象到数据库。",
    "findById":"按主键查询并重建领域对象。",
    "findByBizNo":"按业务号查询通知。",
    "findByTaskId":"按任务号查询 OCR 任务。",
    "pageQuery":"分页查询并封装分页结果。",
    "toDomain":"将持久化对象转换为领域对象。",
    "toPO":"将领域对象转换为持久化对象。",
    "main":"应用程序入口，启动 Spring Boot 容器。",
    "upload":"上传单据接口，创建 OCR 任务。",
    "process":"触发 OCR 识别处理接口。",
    "getResult":"查询 OCR 识别结果。",
    "getOcrResult":"查询 OCR 识别结果。",
    "toResp":"将领域对象转换为响应 DTO。",
    "uploadDocument":"上传单据并创建 OCR 任务。",
    "processOcr":"执行 OCR 识别处理流程。",
    "startProcessing":"将任务状态置为处理中。",
    "complete":"标记任务识别完成并保存结果。",
    "fail":"标记任务识别失败并记录错误。",
    "simulateOcr":"模拟 OCR 识别过程生成结果。",
    "eventName":"返回事件名称。",
    "OcrCompletedEvent":"构造 OCR 完成事件。",
    }
    return m.get(name, f"{name} 方法。")

nodes = []
edges = []
byname = {r["path"]: r for r in res["results"]}

for f in inp["batchFiles"]:
    p = f["path"]
    cat = f["fileCategory"]
    r = byname[p]
    name = p.split("/")[-1]
    summary, tags, cx = meta[p]
    if cat == "config":
        ntype = "config"; nid = f"config:{p}"
    else:
        ntype = "file"; nid = f"file:{p}"
    nodes.append({"id":nid,"type":ntype,"name":name,"filePath":p,"summary":summary,"tags":tags,"complexity":cx})

    if cat != "code":
        continue

    # class nodes
    for c in r.get("classes", []):
        cname = c["name"]
        methods = c.get("methods", [])
        lines = c.get("endLine",0)-c.get("startLine",0)+1
        if len(methods) >= 2 or lines >= 20:
            cid = f"class:{p}:{cname}"
            ctags = tags[:1]+["class","domain"] if "domain" in "/".join([str(t) for t in tags]) else tags[:3]
            nodes.append({"id":cid,"type":"class","name":cname,"filePath":p,
                          "lineRange":[c.get("startLine"),c.get("endLine")],
                          "summary":cls_sum.get(cname, f"{cname} 类。"),
                          "tags":list(dict.fromkeys((tags[:2]+["class"]))),
                          "complexity":"moderate" if lines>=50 else "simple"})
            edges.append({"source":nid,"target":cid,"type":"contains","direction":"forward","weight":1.0})
    # function nodes (10+ lines)
    for fn in r.get("functions", []):
        ln = fn.get("endLine",0)-fn.get("startLine",0)+1
        if ln >= 10:
            fname = fn["name"]
            fid = f"function:{p}:{fname}"
            nodes.append({"id":fid,"type":"function","name":fname,"filePath":p,
                          "lineRange":[fn.get("startLine"),fn.get("endLine")],
                          "summary":fn_summary(fname),
                          "tags":list(dict.fromkeys((tags[:2]+["method"]))),
                          "complexity":"simple"})
            edges.append({"source":nid,"target":fid,"type":"contains","direction":"forward","weight":1.0})

    # imports edges
    for imp in importData.get(p, []):
        tgt = resolve(imp)
        if tgt:
            edges.append({"source":f"file:{p}","target":f"file:{tgt}","type":"imports","direction":"forward","weight":0.7})

# config -> configures edges
edges.append({"source":"config:forex-notification/src/main/resources/application.yml","target":"file:forex-notification/src/main/java/com/forex/notification/NotificationApplication.java","type":"configures","direction":"forward","weight":0.6})
edges.append({"source":"config:forex-ocr/pom.xml","target":"file:forex-ocr/src/main/java/com/forex/ocr/adapter/controller/OcrController.java","type":"configures","direction":"forward","weight":0.6})

# dedupe edges
seen=set(); dedup=[]
for e in edges:
    k=(e["source"],e["target"],e["type"])
    if k in seen or e["source"]==e["target"]: continue
    seen.add(k); dedup.append(e)

out={"nodes":nodes,"edges":dedup}
json.dump(out, open(f"{ROOT}/.understand-anything/intermediate/batch-20.json","w"), indent=2, ensure_ascii=False)
imports_edges=[e for e in dedup if e["type"]=="imports"]
print("nodes:",len(nodes))
print("  file:",sum(1 for n in nodes if n["type"]=="file"))
print("  config:",sum(1 for n in nodes if n["type"]=="config"))
print("  class:",sum(1 for n in nodes if n["type"]=="class"))
print("  function:",sum(1 for n in nodes if n["type"]=="function"))
print("edges:",len(dedup))
from collections import Counter
print("  by type:",dict(Counter(e["type"] for e in dedup)))
