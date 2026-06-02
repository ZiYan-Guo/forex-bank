<template>
  <div class="reconciliation-board">
    <h2>对账管理</h2>

    <a-card title="CFETS数据导入" style="margin-bottom: 16px">
      <a-upload-dragger
        v-model:fileList="fileList"
        name="file"
        :multiple="false"
        :before-upload="beforeUpload"
        accept=".xml,.csv"
        style="margin-bottom: 12px"
      >
        <p class="ant-upload-drag-icon">
          <inbox-outlined />
        </p>
        <p class="ant-upload-text">点击或拖拽文件到此区域上传</p>
        <p class="ant-upload-hint">支持 .xml / .csv 格式的 CFETS 交易确认文件</p>
      </a-upload-dragger>
      <a-button type="primary" :disabled="fileList.length === 0" @click="handleImport" :loading="importing">
        导入
      </a-button>
    </a-card>

    <a-card title="匹配结果" style="margin-bottom: 16px">
      <a-table
        :columns="matchColumns"
        :data-source="matchData"
        :pagination="pagination"
        row-key="id"
        @change="handleTableChange"
      >
        <template #bodyCell="{ column, record }">
          <template v-if="column.key === 'status'">
            <a-tag :color="record.status === 'MATCHED' ? '#52c41a' : '#f5222d'">
              {{ record.status === 'MATCHED' ? '已匹配' : '未匹配' }}
            </a-tag>
          </template>
          <template v-else-if="column.key === 'operation'">
            <a-space v-if="record.status === 'UNMATCHED'">
              <a-button type="link" size="small" @click="handleManualMatch(record)">手动匹配</a-button>
              <a-button type="link" size="small" danger @click="handleIgnore(record)">忽略</a-button>
            </a-space>
            <span v-else style="color: #8c8c8c">-</span>
          </template>
        </template>
      </a-table>
    </a-card>

    <a-card title="SWIFT报文生成">
      <a-form layout="inline" style="margin-bottom: 12px">
        <a-form-item label="报文类型">
          <a-select v-model:value="swiftMsgType" style="width: 160px">
            <a-select-option value="MT300">MT300 - 外汇确认</a-select-option>
            <a-select-option value="MT202">MT202 - 头寸调拨</a-select-option>
            <a-select-option value="pacs008">pacs.008 - 客户汇款</a-select-option>
          </a-select>
        </a-form-item>
        <a-form-item>
          <a-button type="primary" @click="handlePreviewMessage">预览</a-button>
          <a-button style="margin-left: 8px" @click="handleSendMessage">发送</a-button>
        </a-form-item>
      </a-form>
      <a-textarea
        v-model:value="swiftMessageContent"
        :rows="10"
        readonly
        style="font-family: 'Courier New', monospace; font-size: 12px"
      />
    </a-card>

    <a-modal v-model:open="manualMatchVisible" title="手动匹配" @ok="handleConfirmManualMatch" @cancel="manualMatchVisible = false">
      <a-form layout="vertical">
        <a-form-item label="外部参考号">
          <a-input :value="manualMatchRecord?.externalRef" disabled />
        </a-form-item>
        <a-form-item label="选择内部交易">
          <a-select v-model:value="selectedInternalTrade" style="width: 100%" placeholder="搜索内部交易号">
            <a-select-option v-for="t in internalTradeOptions" :key="t.tradeNo" :value="t.tradeNo">
              {{ t.tradeNo }} ({{ t.currencyPair }} {{ t.amount }})
            </a-select-option>
          </a-select>
        </a-form-item>
      </a-form>
    </a-modal>
  </div>
</template>

<script setup lang="ts">
import { ref, reactive } from 'vue'
import { message } from 'ant-design-vue'
import { InboxOutlined } from '@ant-design/icons-vue'
import type { UploadProps, TablePaginationConfig } from 'ant-design-vue'

interface MatchRecord {
  id: string
  status: string
  externalRef: string
  internalRef: string
  amount: string
  reason: string
}

interface InternalTradeOption {
  tradeNo: string
  currencyPair: string
  amount: string
}

const fileList = ref<any[]>([])
const importing = ref(false)

const matchColumns = [
  { title: '状态', dataIndex: 'status', key: 'status' },
  { title: '外部参考号', dataIndex: 'externalRef', key: 'externalRef' },
  { title: '内部交易号', dataIndex: 'internalRef', key: 'internalRef' },
  { title: '金额', dataIndex: 'amount', key: 'amount' },
  { title: '差异原因', dataIndex: 'reason', key: 'reason' },
  { title: '操作', key: 'operation', width: 160 }
]

const matchData = ref<MatchRecord[]>([
  { id: '1', status: 'MATCHED', externalRef: 'CFX20260601001', internalRef: 'FX20240001', amount: 'USD 100,000', reason: '-' },
  { id: '2', status: 'MATCHED', externalRef: 'CFX20260601002', internalRef: 'FX20240002', amount: 'EUR 50,000', reason: '-' },
  { id: '3', status: 'UNMATCHED', externalRef: 'CFX20260601003', internalRef: '-', amount: 'GBP 75,000', reason: '无匹配内部交易' },
  { id: '4', status: 'UNMATCHED', externalRef: '-', internalRef: 'FX20240005', amount: 'JPY 10,000,000', reason: '无匹配CFETS确认' },
  { id: '5', status: 'UNMATCHED', externalRef: 'CFX20260601006', internalRef: 'FX20240008', amount: 'USD 200,000', reason: '金额差异超阈值(>0.01)' }
])

const pagination = reactive({ current: 1, pageSize: 10, total: matchData.value.length })

const swiftMsgType = ref('MT300')
const swiftMessageContent = ref('')

const manualMatchVisible = ref(false)
const manualMatchRecord = ref<MatchRecord | null>(null)
const selectedInternalTrade = ref<string | undefined>(undefined)

const internalTradeOptions = ref<InternalTradeOption[]>([
  { tradeNo: 'FX20240001', currencyPair: 'USD/CNY', amount: '100,000' },
  { tradeNo: 'FX20240002', currencyPair: 'EUR/CNY', amount: '50,000' },
  { tradeNo: 'FX20240005', currencyPair: 'JPY/CNY', amount: '10,000,000' },
  { tradeNo: 'FX20240008', currencyPair: 'USD/CNY', amount: '199,999.80' }
])

const beforeUpload: UploadProps['beforeUpload'] = (file) => {
  const isAllowed = file.type === 'text/xml' || file.type === 'text/csv' || file.name.endsWith('.xml') || file.name.endsWith('.csv')
  if (!isAllowed) {
    message.error('只支持 .xml 和 .csv 格式文件')
  }
  return false
}

function handleTableChange(pg: TablePaginationConfig) {
  pagination.current = pg.current!
  pagination.pageSize = pg.pageSize!
}

function handleImport() {
  importing.value = true
  setTimeout(() => {
    importing.value = false
    message.success('CFETS数据导入成功')
    fileList.value = []
  }, 1500)
}

function handleManualMatch(record: MatchRecord) {
  manualMatchRecord.value = record
  selectedInternalTrade.value = undefined
  manualMatchVisible.value = true
}

function handleIgnore(record: MatchRecord) {
  matchData.value = matchData.value.filter(r => r.id !== record.id)
  message.success(`已忽略 ${record.externalRef || record.internalRef}`)
}

function handleConfirmManualMatch() {
  if (!selectedInternalTrade.value || !manualMatchRecord.value) {
    message.warning('请选择内部交易')
    return
  }
  const record = manualMatchRecord.value
  record.status = 'MATCHED'
  record.internalRef = selectedInternalTrade.value
  record.reason = '手动匹配'
  manualMatchVisible.value = false
  message.success('手动匹配成功')
}

function handlePreviewMessage() {
  const mockMessages: Record<string, string> = {
    MT300: '{1:F01YOURBANKHKAXFXXXX0000000000}\n{2:O3001130240602OURBANKHKAXFXXXX0000000000240602N}\n{4:\n:15A:NEW CONFIRMATION\n:20:FX20240001\n:22A:NEW\n:32B:USD0000000100000.00\n:33B:CNY0000000725360.00\n:36:7.2536\n:30V:240602\n:57A:BKCHCNBJ\n-}',
    MT202: '{1:F01BANKHKAXFXXXX0000000000}\n{2:O202240602BKCHCNBJXXXX0000000000240602}\n{4:\n:20:CFX20240001\n:21:CFX20240001\n:32A:240602USD0000000100000.00\n:57A:BKCHCNBJ\n-}',
    pacs008: '<?xml version="1.0" encoding="UTF-8"?>\n<Document xmlns="urn:iso:std:iso:20022:tech:xsd:pacs.008.001.08">\n  <FIToFICstmrCdtTrf>\n    <GrpHdr>\n      <MsgId>MSG20240001</MsgId>\n      <CreDtTm>2024-06-02T10:30:00</CreDtTm>\n    </GrpHdr>\n    <CdtTrfTxInf>\n      <PmtId><EndToEndId>MSG20240001</EndToEndId></PmtId>\n      <IntrBkSttlmAmt Ccy="USD">100000.00</IntrBkSttlmAmt>\n      <Dbtr><Nm>ABC Corp</Nm></Dbtr>\n      <Cdtr><Nm>XYZ Ltd</Nm></Cdtr>\n      <CdtrAgt><FinInstnId><BICFI>BKCHCNBJ</BICFI></FinInstnId></CdtrAgt>\n      <RmtInf><Ustrd>INVOICE 2024-001</Ustrd>\n    </CdtTrfTxInf>\n  </FIToFICstmrCdtTrf>\n</Document>'
  }
  swiftMessageContent.value = mockMessages[swiftMsgType.value] || ''
}

function handleSendMessage() {
  if (!swiftMessageContent.value) {
    message.warning('请先生成报文')
    return
  }
  message.success(`${swiftMsgType.value} 报文已发送`)
}
</script>

<style scoped>
.reconciliation-board h2 { margin-bottom: 16px; }
</style>
