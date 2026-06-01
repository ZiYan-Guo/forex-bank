<template>
  <div class="risk-monitor">
    <h2>风险监测</h2>

    <a-row :gutter="16" class="stat-row">
      <a-col :span="6">
        <a-card><a-statistic title="今日预警数" :value="statData.todayAlerts" /></a-card>
      </a-col>
      <a-col :span="6">
        <a-card><a-statistic title="高危预警" :value="statData.highRiskAlerts" :value-style="{ color: '#f5222d' }" /></a-card>
      </a-col>
      <a-col :span="6">
        <a-card><a-statistic title="待处理" :value="statData.pendingAlerts" :value-style="{ color: '#fa8c16' }" /></a-card>
      </a-col>
      <a-col :span="6">
        <a-card><a-statistic title="已处理" :value="statData.processedAlerts" :value-style="{ color: '#52c41a' }" /></a-card>
      </a-col>
    </a-row>

    <a-card style="margin-top: 16px">
      <a-table
        :columns="columns"
        :data-source="tableData"
        :pagination="pagination"
        :loading="loading"
        row-key="logNo"
        @change="handleTableChange"
      >
        <template #bodyCell="{ column, record }">
          <template v-if="column.key === 'riskCategory'">
            <a-tag>{{ record.riskCategory }}</a-tag>
          </template>
          <template v-else-if="column.key === 'riskLevel'">
            <a-tag :color="RiskLevelMap[record.riskLevel]?.color">{{ RiskLevelMap[record.riskLevel]?.label }}</a-tag>
          </template>
          <template v-else-if="column.key === 'checkResult'">
            <a-tag :color="checkResultMap[record.checkResult]?.color">{{ checkResultMap[record.checkResult]?.label }}</a-tag>
          </template>
          <template v-else-if="column.key === 'operation'">
            <a-space>
              <a @click="showDetail(record)">查看</a>
              <a-popconfirm title="确定要上报该预警吗？" @confirm="handleEscalate(record)">
                <a>上报</a>
              </a-popconfirm>
            </a-space>
          </template>
        </template>
      </a-table>
    </a-card>

    <a-drawer title="预警详情" :open="detailVisible" :width="600" @close="detailVisible = false">
      <a-descriptions :column="1" bordered size="small">
        <a-descriptions-item label="日志编号">{{ detailData.logNo }}</a-descriptions-item>
        <a-descriptions-item label="客户ID">{{ detailData.customerId }}</a-descriptions-item>
        <a-descriptions-item label="业务类型">{{ detailData.bizType }}</a-descriptions-item>
        <a-descriptions-item label="交易金额">{{ detailData.transactionAmount }}</a-descriptions-item>
        <a-descriptions-item label="风险类别">
          <a-tag>{{ detailData.riskCategory }}</a-tag>
        </a-descriptions-item>
        <a-descriptions-item label="风险等级">
          <a-tag :color="RiskLevelMap[detailData.riskLevel]?.color">{{ RiskLevelMap[detailData.riskLevel]?.label }}</a-tag>
        </a-descriptions-item>
        <a-descriptions-item label="检查结果">
          <a-tag :color="checkResultMap[detailData.checkResult]?.color">{{ checkResultMap[detailData.checkResult]?.label }}</a-tag>
        </a-descriptions-item>
        <a-descriptions-item label="交易时间">{{ detailData.transactionTime }}</a-descriptions-item>
      </a-descriptions>
    </a-drawer>
  </div>
</template>

<script setup lang="ts">
import { ref, reactive, onMounted } from 'vue'
import { message } from 'ant-design-vue'
import { riskApi } from '@/api/business'
import { RiskLevelMap } from '@/types/api'
import type { TablePaginationConfig } from 'ant-design-vue'

const checkResultMap: Record<string, { label: string; color: string }> = {
  PASS: { label: '通过', color: '#52c41a' },
  REVIEW: { label: '待审核', color: '#fa8c16' },
  REJECT: { label: '拒绝', color: '#f5222d' }
}

interface RiskLog {
  logNo: string
  customerId: number
  bizType: string
  transactionAmount: number
  riskCategory: string
  riskLevel: number
  checkResult: string
  transactionTime: string
}

const columns = [
  { title: '日志编号', dataIndex: 'logNo', key: 'logNo' },
  { title: '客户ID', dataIndex: 'customerId', key: 'customerId' },
  { title: '业务类型', dataIndex: 'bizType', key: 'bizType' },
  { title: '交易金额', dataIndex: 'transactionAmount', key: 'transactionAmount' },
  { title: '风险类别', dataIndex: 'riskCategory', key: 'riskCategory' },
  { title: '风险等级', dataIndex: 'riskLevel', key: 'riskLevel' },
  { title: '检查结果', dataIndex: 'checkResult', key: 'checkResult' },
  { title: '交易时间', dataIndex: 'transactionTime', key: 'transactionTime' },
  { title: '操作', key: 'operation', width: 120 }
]

const tableData = ref<RiskLog[]>([])
const loading = ref(false)
const pagination = reactive({ current: 1, pageSize: 10, total: 0 })

const statData = reactive({ todayAlerts: 0, highRiskAlerts: 0, pendingAlerts: 0, processedAlerts: 0 })

const detailVisible = ref(false)
const detailData = reactive<RiskLog>({} as RiskLog)

async function fetchData() {
  loading.value = true
  try {
    const res = await riskApi.logPageQuery({ pageNum: pagination.current, pageSize: pagination.pageSize })
    const data = res.data.data
    tableData.value = data.records
    pagination.total = data.total
    const records = data.records || []
    statData.todayAlerts = records.length
    statData.highRiskAlerts = records.filter((r: RiskLog) => r.riskLevel === 3).length
    statData.pendingAlerts = records.filter((r: RiskLog) => r.checkResult === 'REVIEW').length
    statData.processedAlerts = records.filter((r: RiskLog) => r.checkResult !== 'REVIEW').length
  } catch { } finally { loading.value = false }
}

function handleTableChange(pg: TablePaginationConfig) { pagination.current = pg.current!; pagination.pageSize = pg.pageSize!; fetchData() }

function showDetail(record: RiskLog) { Object.assign(detailData, record); detailVisible.value = true }

async function handleEscalate(record: RiskLog) {
  try { await riskApi.submitReport(record.logNo); message.success('上报成功'); fetchData() } catch { }
}

onMounted(() => fetchData())
</script>

<style scoped>
.risk-monitor h2 { margin-bottom: 16px; }
.stat-row { margin-bottom: 16px; }
</style>
