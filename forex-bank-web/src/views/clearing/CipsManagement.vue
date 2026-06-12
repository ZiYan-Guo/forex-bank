<template>
  <div class="cips-management">
    <h2>CIPS 管理</h2>

    <a-row :gutter="16" class="stat-row">
      <a-col :span="6">
        <a-card><a-statistic title="参与行总数 Total" :value="8" /></a-card>
      </a-col>
      <a-col :span="6">
        <a-card><a-statistic title="直接参与 Direct" :value="5" :value-style="{ color: '#1890ff' }" /></a-card>
      </a-col>
      <a-col :span="6">
        <a-card><a-statistic title="间接参与 Indirect" :value="3" :value-style="{ color: '#722ed1' }" /></a-card>
      </a-col>
      <a-col :span="6">
        <a-card><a-statistic title="今日报文数 Messages" :value="42" :value-style="{ color: '#52c41a' }" /></a-card>
      </a-col>
    </a-row>

    <a-card style="margin-top: 16px">
      <a-tabs v-model:activeKey="activeTab">
        <a-tab-pane key="message" tab="报文生成">
          <a-form layout="vertical" style="max-width: 800px">
            <a-form-item label="报文类型 Message Type">
              <a-select v-model:value="msgForm.messageType" placeholder="选择报文类型">
                <a-select-option value="CIPS.111">CIPS.111 - 客户汇款请求 Customer Transfer</a-select-option>
                <a-select-option value="CIPS.112">CIPS.112 - 金融机构汇款请求 FI Transfer</a-select-option>
                <a-select-option value="CIPS.113">CIPS.113 - 汇款状态查询 Status Inquiry</a-select-option>
              </a-select>
            </a-form-item>
            <a-form-item label="发送方 Sender BIC">
              <a-input v-model:value="msgForm.senderBIC" placeholder="e.g. ICBKCNBJ" />
            </a-form-item>
            <a-form-item label="接收方 Receiver BIC">
              <a-input v-model:value="msgForm.receiverBIC" placeholder="e.g. HSBCHKHH" />
            </a-form-item>
            <a-form-item label="金额 Amount">
              <a-input-number v-model:value="msgForm.amount" style="width: 200px" :min="0" :step="1000" />
            </a-form-item>
            <a-form-item label="币种 Currency">
              <a-select v-model:value="msgForm.currency" style="width: 120px">
                <a-select-option value="CNY">CNY</a-select-option>
                <a-select-option value="USD">USD</a-select-option>
                <a-select-option value="EUR">EUR</a-select-option>
              </a-select>
            </a-form-item>
            <a-form-item>
              <a-button type="primary" @click="generateMessage">生成 Generate</a-button>
            </a-form-item>
          </a-form>
          <a-textarea
            v-if="generatedXml"
            v-model:value="generatedXml"
            :rows="12"
            readonly
            style="font-family: monospace; margin-top: 8px"
          />
        </a-tab-pane>

        <a-tab-pane key="participant" tab="参与者管理">
          <div style="margin-bottom: 12px">
            <a-button type="primary" @click="showParticipantModal = true">新增参与者 Add Participant</a-button>
          </div>
          <a-table :columns="participantColumns" :data-source="participants" row-key="id" :pagination="false">
            <template #bodyCell="{ column, record }">
              <template v-if="column.key === 'type'">
                <a-tag :color="record.type === 'DIRECT' ? 'blue' : 'purple'">
                  {{ record.type === 'DIRECT' ? '直接 Direct' : '间接 Indirect' }}
                </a-tag>
              </template>
              <template v-else-if="column.key === 'status'">
                <a-badge :status="record.status === 'ACTIVE' ? 'success' : 'default'"
                  :text="record.status === 'ACTIVE' ? '活跃 Active' : '停用 Inactive'" />
              </template>
              <template v-else-if="column.key === 'operation'">
                <a-space>
                  <a @click="editParticipant(record)">编辑 Edit</a>
                  <a-popconfirm title="确认停用该参与者？" @confirm="toggleParticipant(record)">
                    <a>{{ record.status === 'ACTIVE' ? '停用' : '启用' }}</a>
                  </a-popconfirm>
                </a-space>
              </template>
            </template>
          </a-table>
        </a-tab-pane>

        <a-tab-pane key="stats" tab="业务统计">
          <a-table :columns="statsColumns" :data-source="statsData" row-key="date" :pagination="false" />
        </a-tab-pane>
      </a-tabs>
    </a-card>

    <a-modal v-model:open="showParticipantModal" title="新增参与者 Add Participant" @ok="addParticipant">
      <a-form layout="vertical">
        <a-form-item label="BIC 代码 BIC Code"><a-input v-model:value="newParticipant.bicCode" /></a-form-item>
        <a-form-item label="CIPS ID"><a-input v-model:value="newParticipant.cipsId" /></a-form-item>
        <a-form-item label="银行名称 Bank Name"><a-input v-model:value="newParticipant.bankName" /></a-form-item>
        <a-form-item label="类型 Type">
          <a-select v-model:value="newParticipant.type">
            <a-select-option value="DIRECT">直接参与 Direct</a-select-option>
            <a-select-option value="INDIRECT">间接参与 Indirect</a-select-option>
          </a-select>
        </a-form-item>
      </a-form>
    </a-modal>
  </div>
</template>

<script setup lang="ts">
import { ref, reactive } from 'vue'
import { message } from 'ant-design-vue'

const activeTab = ref('message')

const msgForm = reactive({
  messageType: 'CIPS.111',
  senderBIC: 'ICBKCNBJ',
  receiverBIC: 'HSBCHKHH',
  amount: 100000,
  currency: 'CNY'
})

const generatedXml = ref('')

function generateMessage() {
  const now = new Date().toISOString()
  const msgId = 'CIPS' + Date.now()
  generatedXml.value = `<CipsMsg>
  <GrpHdr>
    <MsgId>${msgId}</MsgId>
    <CreDtTm>${now}</CreDtTm>
    <MsgType>${msgForm.messageType}</MsgType>
  </GrpHdr>
  <PmtInf>
    <InstrId>INST${Date.now()}</InstrId>
    <IntrBkSttlmAmt Ccy="${msgForm.currency}">${msgForm.amount}</IntrBkSttlmAmt>
    <IntrBkSttlmDt>${new Date().toISOString().split('T')[0]}</IntrBkSttlmDt>
    <InstgAgt>
      <FinInstnId><BICFI>${msgForm.senderBIC}</BICFI></FinInstnId>
    </InstgAgt>
    <InstdAgt>
      <FinInstnId><BICFI>${msgForm.receiverBIC}</BICFI></FinInstnId>
    </InstdAgt>
  </PmtInf>
</CipsMsg>`
  message.success('CIPS报文已生成 CIPS message generated')
}

const participants = ref([
  { id: 1, bicCode: 'ICBKCNBJ', cipsId: 'CIPS001', bankName: '中国工商银行 ICBC', type: 'DIRECT', status: 'ACTIVE' },
  { id: 2, bicCode: 'ABOCCNBJ', cipsId: 'CIPS002', bankName: '中国农业银行 ABC', type: 'DIRECT', status: 'ACTIVE' },
  { id: 3, bicCode: 'BKCHCNBJ', cipsId: 'CIPS003', bankName: '中国银行 BOC', type: 'DIRECT', status: 'ACTIVE' },
  { id: 4, bicCode: 'PCBCCNBJ', cipsId: 'CIPS004', bankName: '中国建设银行 CCB', type: 'DIRECT', status: 'ACTIVE' },
  { id: 5, bicCode: 'COMMCNSH', cipsId: 'CIPS005', bankName: '交通银行 BoCom', type: 'INDIRECT', status: 'ACTIVE' },
  { id: 6, bicCode: 'MSBCCNBJ', cipsId: 'CIPS006', bankName: '中国民生银行 CMBC', type: 'INDIRECT', status: 'ACTIVE' },
  { id: 7, bicCode: 'HSBCHKHH', cipsId: 'CIPS007', bankName: '汇丰银行 HSBC Hong Kong', type: 'DIRECT', status: 'ACTIVE' },
  { id: 8, bicCode: 'SCBLHKHH', cipsId: 'CIPS008', bankName: '渣打银行 SCB Hong Kong', type: 'INDIRECT', status: 'INACTIVE' },
])

const participantColumns = [
  { title: 'BIC 代码 BIC Code', dataIndex: 'bicCode', key: 'bicCode' },
  { title: 'CIPS ID', dataIndex: 'cipsId', key: 'cipsId' },
  { title: '银行名称 Bank Name', dataIndex: 'bankName', key: 'bankName' },
  { title: '类型 Type', key: 'type' },
  { title: '状态 Status', key: 'status' },
  { title: '操作 Operations', key: 'operation', width: 140 }
]

const statsColumns = [
  { title: '日期 Date', dataIndex: 'date', key: 'date' },
  { title: '交易笔数 Tx Count', dataIndex: 'txCount', key: 'txCount' },
  { title: '交易金额 Amount (CNY)', dataIndex: 'amount', key: 'amount' },
  { title: '市场份额 Share %', dataIndex: 'share', key: 'share' }
]

const statsData = ref([
  { date: '2026-06-11', txCount: 38, amount: '¥ 125,800,000', share: '28.5%' },
  { date: '2026-06-10', txCount: 42, amount: '¥ 156,200,000', share: '30.1%' },
  { date: '2026-06-09', txCount: 35, amount: '¥ 98,500,000', share: '25.8%' },
  { date: '2026-06-08', txCount: 45, amount: '¥ 189,300,000', share: '32.4%' },
  { date: '2026-06-07', txCount: 40, amount: '¥ 142,000,000', share: '29.7%' },
])

const showParticipantModal = ref(false)
const newParticipant = reactive({
  bicCode: '',
  cipsId: '',
  bankName: '',
  type: 'DIRECT'
})

function addParticipant() {
  participants.value.push({
    id: participants.value.length + 1,
    bicCode: newParticipant.bicCode || 'N/A',
    cipsId: newParticipant.cipsId || 'CIPS' + (participants.value.length + 1),
    bankName: newParticipant.bankName || '新建银行',
    type: newParticipant.type,
    status: 'ACTIVE'
  })
  newParticipant.bicCode = ''
  newParticipant.cipsId = ''
  newParticipant.bankName = ''
  showParticipantModal.value = false
  message.success('参与者已添加 Participant added')
}

function editParticipant(record: any) {
  message.info('编辑参与者: ' + record.bankName)
}

function toggleParticipant(record: any) {
  record.status = record.status === 'ACTIVE' ? 'INACTIVE' : 'ACTIVE'
  message.success(`参与者状态已更新 Status updated to ${record.status}`)
}
</script>

<style scoped>
.cips-management h2 { margin-bottom: 16px; }
.stat-row { margin-bottom: 8px; }
</style>
