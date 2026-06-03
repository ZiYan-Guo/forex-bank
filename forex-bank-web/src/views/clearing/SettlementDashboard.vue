<template>
  <div class="settlement-dashboard">
    <h2>结算仪表板</h2>

    <a-row :gutter="16" class="stat-row">
      <a-col :span="6">
        <a-card>
          <a-statistic title="今日结算笔数" :value="128" suffix="笔" />
        </a-card>
      </a-col>
      <a-col :span="6">
        <a-card>
          <a-statistic title="今日结算金额" :value="3500" :precision="0" suffix="万" />
        </a-card>
      </a-col>
      <a-col :span="6">
        <a-card>
          <a-statistic title="PVP配对率" :value="92" suffix="%" :value-style="{ color: '#52c41a' }" />
        </a-card>
      </a-col>
      <a-col :span="6">
        <a-card>
          <a-statistic title="异常笔数" :value="3" suffix="笔" :value-style="{ color: '#f5222d' }" />
        </a-card>
      </a-col>
    </a-row>

    <a-row :gutter="16" style="margin-top: 16px">
      <a-col :span="12">
        <a-card title="CLS场次">
          <div class="session-countdown">
            <a-statistic-countdown title="下一场次倒计时" :value="nextSessionTime" format="HH:mm:ss" />
          </div>
          <a-divider />
          <a-timeline>
            <a-timeline-item v-for="(sessionItem, idx) in sessionTimeline" :key="idx"
              :color="sessionItem.status === 'COMPLETED' ? '#52c41a' : sessionItem.status === 'ACTIVE' ? '#1677ff' : '#8c8c8c'">
              <template #dot>
                <clock-circle-outlined v-if="sessionItem.status === 'ACTIVE'" style="font-size:16px" />
              </template>
              {{ sessionItem.label }}
              <a-tag :color="sessionItem.status === 'COMPLETED' ? 'green' : sessionItem.status === 'ACTIVE' ? 'blue' : ''" style="margin-left:8px">
                {{ sessionItem.status }}
              </a-tag>
            </a-timeline-item>
          </a-timeline>
        </a-card>
      </a-col>
      <a-col :span="12">
        <a-card title="实时PVP配对">
          <a-table :columns="pvpColumns" :data-source="pvpData" :pagination="false" :scroll="{ y: 300 }" row-key="pairId" size="small">
            <template #bodyCell="{ column, record }">
              <template v-if="column.key === 'status'">
                <a-tag :color="record.status === 'PAIRED' ? '#52c41a' : '#fa8c16'">{{ record.status }}</a-tag>
              </template>
              <template v-if="column.key === 'action'">
                <a-button size="small" type="link" @click="handlePvpPair(record)">配对</a-button>
              </template>
            </template>
          </a-table>
        </a-card>
      </a-col>
    </a-row>

    <a-card title="异常处理" style="margin-top: 16px">
      <a-table :columns="exceptionColumns" :data-source="exceptionData" :pagination="false" row-key="exceptionId">
        <template #bodyCell="{ column, record }">
          <template v-if="column.key === 'severity'">
            <a-tag :color="record.severity === 'HIGH' ? '#f5222d' : record.severity === 'MEDIUM' ? '#fa8c16' : '#1677ff'">{{ record.severity }}</a-tag>
          </template>
          <template v-if="column.key === 'operation'">
            <a-space>
              <a-button size="small" type="link" @click="handleInvestigate(record)">调查</a-button>
              <a-button size="small" type="link" @click="handleResolve(record)">处理</a-button>
            </a-space>
          </template>
        </template>
      </a-table>
    </a-card>
  </div>
</template>

<script setup lang="ts">
import { ref, reactive, onMounted } from 'vue'
import { message } from 'ant-design-vue'

const nextSessionTime = ref(Date.now() + 1000 * 3600 * 2)

interface SessionItem {
  label: string
  status: string
}

interface PvpRecord {
  pairId: string
  buyCcy: string
  sellCcy: string
  amount: string
  counterparty: string
  status: string
}

interface ExceptionRecord {
  exceptionId: string
  type: string
  description: string
  severity: string
  time: string
}

const sessionTimeline = ref<SessionItem[]>([
  { label: 'SCHEDULED - 场次已排定', status: 'COMPLETED' },
  { label: 'PAY_IN - 付款入金中', status: 'COMPLETED' },
  { label: 'CALCULATING - 净额计算中', status: 'ACTIVE' },
  { label: 'COMPLETED - 结算完成', status: 'SCHEDULED' }
])

const pvpColumns = [
  { title: '配对编号', dataIndex: 'pairId', key: 'pairId' },
  { title: '买入币种', dataIndex: 'buyCcy', key: 'buyCcy' },
  { title: '卖出币种', dataIndex: 'sellCcy', key: 'sellCcy' },
  { title: '金额', dataIndex: 'amount', key: 'amount' },
  { title: '对手方', dataIndex: 'counterparty', key: 'counterparty' },
  { title: '状态', dataIndex: 'status', key: 'status' },
  { title: '操作', key: 'action', width: 80 }
]

const pvpData = ref<PvpRecord[]>([
  { pairId: 'PVP001', buyCcy: 'USD', sellCcy: 'EUR', amount: '5,000,000', counterparty: 'Deutsche Bank', status: 'PAIRED' },
  { pairId: 'PVP002', buyCcy: 'GBP', sellCcy: 'JPY', amount: '3,200,000', counterparty: 'Barclays', status: 'PAIRED' },
  { pairId: 'PVP003', buyCcy: 'USD', sellCcy: 'CNY', amount: '8,000,000', counterparty: 'ICBC', status: 'PENDING' },
  { pairId: 'PVP004', buyCcy: 'EUR', sellCcy: 'CHF', amount: '2,500,000', counterparty: 'UBS', status: 'PENDING' }
])

const exceptionColumns = [
  { title: '异常编号', dataIndex: 'exceptionId', key: 'exceptionId' },
  { title: '异常类型', dataIndex: 'type', key: 'type' },
  { title: '描述', dataIndex: 'description', key: 'description' },
  { title: '严重等级', dataIndex: 'severity', key: 'severity' },
  { title: '时间', dataIndex: 'time', key: 'time' },
  { title: '操作', key: 'operation', width: 120 }
]

const exceptionData = ref<ExceptionRecord[]>([
  { exceptionId: 'EX001', type: '金额不符', description: 'SWIFT对账金额与内部记录不一致', severity: 'HIGH', time: '2024-01-20 10:30' },
  { exceptionId: 'EX002', type: '延迟', description: 'CIPS指令超过预计到账时间', severity: 'MEDIUM', time: '2024-01-20 11:15' },
  { exceptionId: 'EX003', type: 'PVP失败', description: 'PVP配对超时未完成', severity: 'HIGH', time: '2024-01-20 09:45' }
])

function handlePvpPair(record: PvpRecord) {
  message.success(`已触发 ${record.pairId} 配对操作`)
}

function handleInvestigate(record: ExceptionRecord) {
  message.info(`正在调查异常 ${record.exceptionId}`)
}

function handleResolve(record: ExceptionRecord) {
  message.success(`异常 ${record.exceptionId} 已标记为已处理`)
}

onMounted(() => {
  message.info('结算仪表板加载完成')
})
</script>

<style scoped>
.settlement-dashboard h2 { margin-bottom: 16px; }
.stat-row { margin-bottom: 16px; }
.session-countdown { text-align: center; padding: 16px 0; }
</style>
