import{I as Q}from"./InboxOutlined-Cq7Ro1K-.js";import{k as Z,x as j,g,d as I,i as t,Q as n,y as r,A as o,w as u,K as q,h as s,e as k,H as N,F as b,f as h,z as ee,b as i,_ as te}from"./index-DzyEO5hM.js";const ne={class:"reconciliation-board"},ae={class:"ant-upload-drag-icon"},le={key:1,style:{color:"#8c8c8c"}},oe=Z({__name:"ReconciliationBoard",setup(se){const c=r([]),F=r(!1),B=[{title:"状态",dataIndex:"status",key:"status"},{title:"外部参考号",dataIndex:"externalRef",key:"externalRef"},{title:"内部交易号",dataIndex:"internalRef",key:"internalRef"},{title:"金额",dataIndex:"amount",key:"amount"},{title:"差异原因",dataIndex:"reason",key:"reason"},{title:"操作",key:"operation",width:160}],C=r([{id:"1",status:"MATCHED",externalRef:"CFX20260601001",internalRef:"FX20240001",amount:"USD 100,000",reason:"-"},{id:"2",status:"MATCHED",externalRef:"CFX20260601002",internalRef:"FX20240002",amount:"EUR 50,000",reason:"-"},{id:"3",status:"UNMATCHED",externalRef:"CFX20260601003",internalRef:"-",amount:"GBP 75,000",reason:"无匹配内部交易"},{id:"4",status:"UNMATCHED",externalRef:"-",internalRef:"FX20240005",amount:"JPY 10,000,000",reason:"无匹配CFETS确认"},{id:"5",status:"UNMATCHED",externalRef:"CFX20260601006",internalRef:"FX20240008",amount:"USD 200,000",reason:"金额差异超阈值(>0.01)"}]),X=j({current:1,pageSize:10,total:C.value.length}),v=r("MT300"),_=r(""),d=r(!1),y=r(null),m=r(void 0),D=r([{tradeNo:"FX20240001",currencyPair:"USD/CNY",amount:"100,000"},{tradeNo:"FX20240002",currencyPair:"EUR/CNY",amount:"50,000"},{tradeNo:"FX20240005",currencyPair:"JPY/CNY",amount:"10,000,000"},{tradeNo:"FX20240008",currencyPair:"USD/CNY",amount:"199,999.80"}]),U=l=>(l.type==="text/xml"||l.type==="text/csv"||l.name.endsWith(".xml")||l.name.endsWith(".csv")||i.error("只支持 .xml 和 .csv 格式文件"),!1);function E(l){X.current=l.current,X.pageSize=l.pageSize}function S(){F.value=!0,setTimeout(()=>{F.value=!1,i.success("CFETS数据导入成功"),c.value=[]},1500)}function H(l){y.value=l,m.value=void 0,d.value=!0}function w(l){C.value=C.value.filter(e=>e.id!==l.id),i.success(`已忽略 ${l.externalRef||l.internalRef}`)}function K(){if(!m.value||!y.value){i.warning("请选择内部交易");return}const l=y.value;l.status="MATCHED",l.internalRef=m.value,l.reason="手动匹配",d.value=!1,i.success("手动匹配成功")}function P(){const l={MT300:`{1:F01YOURBANKHKAXFXXXX0000000000}
{2:O3001130240602OURBANKHKAXFXXXX0000000000240602N}
{4:
:15A:NEW CONFIRMATION
:20:FX20240001
:22A:NEW
:32B:USD0000000100000.00
:33B:CNY0000000725360.00
:36:7.2536
:30V:240602
:57A:BKCHCNBJ
-}`,MT202:`{1:F01BANKHKAXFXXXX0000000000}
{2:O202240602BKCHCNBJXXXX0000000000240602}
{4:
:20:CFX20240001
:21:CFX20240001
:32A:240602USD0000000100000.00
:57A:BKCHCNBJ
-}`,pacs008:`<?xml version="1.0" encoding="UTF-8"?>
<Document xmlns="urn:iso:std:iso:20022:tech:xsd:pacs.008.001.08">
  <FIToFICstmrCdtTrf>
    <GrpHdr>
      <MsgId>MSG20240001</MsgId>
      <CreDtTm>2024-06-02T10:30:00</CreDtTm>
    </GrpHdr>
    <CdtTrfTxInf>
      <PmtId><EndToEndId>MSG20240001</EndToEndId></PmtId>
      <IntrBkSttlmAmt Ccy="USD">100000.00</IntrBkSttlmAmt>
      <Dbtr><Nm>ABC Corp</Nm></Dbtr>
      <Cdtr><Nm>XYZ Ltd</Nm></Cdtr>
      <CdtrAgt><FinInstnId><BICFI>BKCHCNBJ</BICFI></FinInstnId></CdtrAgt>
      <RmtInf><Ustrd>INVOICE 2024-001</Ustrd>
    </CdtTrfTxInf>
  </FIToFICstmrCdtTrf>
</Document>`};_.value=l[v.value]||""}function O(){if(!_.value){i.warning("请先生成报文");return}i.success(`${v.value} 报文已发送`)}return(l,e)=>{const Y=o("a-upload-dragger"),f=o("a-button"),M=o("a-card"),z=o("a-tag"),V=o("a-space"),J=o("a-table"),x=o("a-select-option"),A=o("a-select"),T=o("a-form-item"),R=o("a-form"),L=o("a-textarea"),G=o("a-input"),W=o("a-modal");return u(),g("div",ne,[e[16]||(e[16]=I("h2",null,"对账管理",-1)),t(M,{title:"CFETS数据导入",style:{"margin-bottom":"16px"}},{default:n(()=>[t(Y,{fileList:c.value,"onUpdate:fileList":e[0]||(e[0]=a=>c.value=a),name:"file",multiple:!1,"before-upload":U,accept:".xml,.csv",style:{"margin-bottom":"12px"}},{default:n(()=>[I("p",ae,[t(q(Q))]),e[6]||(e[6]=I("p",{class:"ant-upload-text"},"点击或拖拽文件到此区域上传",-1)),e[7]||(e[7]=I("p",{class:"ant-upload-hint"},"支持 .xml / .csv 格式的 CFETS 交易确认文件",-1))]),_:1},8,["fileList"]),t(f,{type:"primary",disabled:c.value.length===0,onClick:S,loading:F.value},{default:n(()=>[...e[8]||(e[8]=[s(" 导入 ",-1)])]),_:1},8,["disabled","loading"])]),_:1}),t(M,{title:"匹配结果",style:{"margin-bottom":"16px"}},{default:n(()=>[t(J,{columns:B,"data-source":C.value,pagination:X,"row-key":"id",onChange:E},{bodyCell:n(({column:a,record:p})=>[a.key==="status"?(u(),k(z,{key:0,color:p.status==="MATCHED"?"#52c41a":"#f5222d"},{default:n(()=>[s(N(p.status==="MATCHED"?"已匹配":"未匹配"),1)]),_:2},1032,["color"])):a.key==="operation"?(u(),g(b,{key:1},[p.status==="UNMATCHED"?(u(),k(V,{key:0},{default:n(()=>[t(f,{type:"link",size:"small",onClick:$=>H(p)},{default:n(()=>[...e[9]||(e[9]=[s("手动匹配",-1)])]),_:1},8,["onClick"]),t(f,{type:"link",size:"small",danger:"",onClick:$=>w(p)},{default:n(()=>[...e[10]||(e[10]=[s("忽略",-1)])]),_:1},8,["onClick"])]),_:2},1024)):(u(),g("span",le,"-"))],64)):h("",!0)]),_:1},8,["data-source","pagination"])]),_:1}),t(M,{title:"SWIFT报文生成"},{default:n(()=>[t(R,{layout:"inline",style:{"margin-bottom":"12px"}},{default:n(()=>[t(T,{label:"报文类型"},{default:n(()=>[t(A,{value:v.value,"onUpdate:value":e[1]||(e[1]=a=>v.value=a),style:{width:"160px"}},{default:n(()=>[t(x,{value:"MT300"},{default:n(()=>[...e[11]||(e[11]=[s("MT300 - 外汇确认",-1)])]),_:1}),t(x,{value:"MT202"},{default:n(()=>[...e[12]||(e[12]=[s("MT202 - 头寸调拨",-1)])]),_:1}),t(x,{value:"pacs008"},{default:n(()=>[...e[13]||(e[13]=[s("pacs.008 - 客户汇款",-1)])]),_:1})]),_:1},8,["value"])]),_:1}),t(T,null,{default:n(()=>[t(f,{type:"primary",onClick:P},{default:n(()=>[...e[14]||(e[14]=[s("预览",-1)])]),_:1}),t(f,{style:{"margin-left":"8px"},onClick:O},{default:n(()=>[...e[15]||(e[15]=[s("发送",-1)])]),_:1})]),_:1})]),_:1}),t(L,{value:_.value,"onUpdate:value":e[2]||(e[2]=a=>_.value=a),rows:10,readonly:"",style:{"font-family":"'Courier New', monospace","font-size":"12px"}},null,8,["value"])]),_:1}),t(W,{open:d.value,"onUpdate:open":e[4]||(e[4]=a=>d.value=a),title:"手动匹配",onOk:K,onCancel:e[5]||(e[5]=a=>d.value=!1)},{default:n(()=>[t(R,{layout:"vertical"},{default:n(()=>[t(T,{label:"外部参考号"},{default:n(()=>{var a;return[t(G,{value:(a=y.value)==null?void 0:a.externalRef,disabled:""},null,8,["value"])]}),_:1}),t(T,{label:"选择内部交易"},{default:n(()=>[t(A,{value:m.value,"onUpdate:value":e[3]||(e[3]=a=>m.value=a),style:{width:"100%"},placeholder:"搜索内部交易号"},{default:n(()=>[(u(!0),g(b,null,ee(D.value,a=>(u(),k(x,{key:a.tradeNo,value:a.tradeNo},{default:n(()=>[s(N(a.tradeNo)+" ("+N(a.currencyPair)+" "+N(a.amount)+") ",1)]),_:2},1032,["value"]))),128))]),_:1},8,["value"])]),_:1})]),_:1})]),_:1},8,["open"])])}}}),ie=te(oe,[["__scopeId","data-v-b502e4bd"]]);export{ie as default};
