<template>
  <!-- 套保会计 Hedge Accounting -->
  <div class="hedge-accounting">
    <h3 class="page-title">套保会计</h3>

    <!-- Tabs: 套期关系 / 有效性测试 / 会计分录 / 套保报告 -->
    <a-tabs v-model:activeKey="activeTab">
      <!-- Tab1: Hedge Relationships 套期关系 -->
      <a-tab-pane key="relationships" tab="套期关系 Relationships">
        <a-card title="套期关系管理" size="small">
          <template #extra>
            <a-button type="primary" size="small" @click="showRelModal = true">
              <plus-outlined /> 新建关系
            </a-button>
          </template>
          <a-table :columns="relColumns" :data-source="relList" :pagination="false" row-key="id" size="small">
            <template #bodyCell="{ column, record }">
              <template v-if="column.key === 'hedgeType'">
                <a-tag :color="record.hedgeType === 'FAIR_VALUE' ? 'blue' : 'green'">
                  {{ record.hedgeType === 'FAIR_VALUE' ? '公允价值' : '现金流量' }}
                </a-tag>
              </template>
              <template v-if="column.key === 'status'">
                <a-tag :color="record.status === 'ACTIVE' ? 'green' : 'default'">
                  {{ record.status === 'ACTIVE' ? '有效' : '已终止' }}
                </a-tag>
              </template>
              <template v-if="column.key === 'actions'">
                <a-space>
                  <a-button type="link" size="small">编辑</a-button>
                  <a-popconfirm title="确认删除？" @confirm="delRelationship(record.id)">
                    <a-button type="link" size="small" danger>删除</a-button>
                  </a-popconfirm>
                </a-space>
              </template>
            </template>
          </a-table>
        </a-card>

        <!-- New relationship modal 新建关系弹窗 -->
        <a-modal v-model:open="showRelModal" title="新建套期关系" @ok="addRelationship" @cancel="showRelModal = false">
          <a-form layout="vertical">
            <a-form-item label="被套期项目 Hedged Item">
              <a-input v-model:value="newRel.hedgedItem" placeholder="如：USD应收账款" />
            </a-form-item>
            <a-form-item label="套期工具 Hedging Instrument">
              <a-input v-model:value="newRel.hedgingInstrument" placeholder="如：USD远期合约" />
            </a-form-item>
            <a-form-item label="套期类型 Hedge Type">
              <a-select v-model:value="newRel.hedgeType">
                <a-select-option value="FAIR_VALUE">公允价值套期 Fair Value</a-select-option>
                <a-select-option value="CASH_FLOW">现金流量套期 Cash Flow</a-select-option>
              </a-select>
            </a-form-item>
          </a-form>
        </a-modal>
      </a-tab-pane>

      <!-- Tab2: Effectiveness Tests 有效性测试 -->
      <a-tab-pane key="effectiveness" tab="有效性测试 Testing">
        <a-card title="有效性测试记录" size="small">
          <template #extra>
            <a-button type="primary" size="small" @click="runEffectivenessTest">
              <play-circle-outlined /> 执行测试
            </a-button>
          </template>
          <a-table :columns="testColumns" :data-source="testList" :pagination="false" row-key="id" size="small">
            <template #bodyCell="{ column, record }">
              <template v-if="column.key === 'result'">
                <a-tag :color="record.result === 'PASS' ? 'green' : 'red'">
                  {{ record.result === 'PASS' ? '通过' : '未通过' }}
                </a-tag>
              </template>
              <template v-if="column.key === 'hedgeRatio'">
                <a-progress :percent="Math.round(record.hedgeRatio * 100)" :size="16"
                  :stroke-color="record.hedgeRatio >= 0.8 ? '#52c41a' : '#f5222d'" />
              </template>
            </template>
          </a-table>
        </a-card>
      </a-tab-pane>

      <!-- Tab3: Journal Entries 会计分录 -->
      <a-tab-pane key="journals" tab="会计分录 Entries">
        <a-card title="套保会计分录" size="small">
          <a-table :columns="journalColumns" :data-source="journalList" :pagination="false" row-key="id" size="small">
            <template #bodyCell="{ column, record }">
              <template v-if="column.key === 'drCr'">
                <a-tag :color="record.drCr === 'DR' ? 'blue' : 'orange'">
                  {{ record.drCr === 'DR' ? '借 DR' : '贷 CR' }}
                </a-tag>
              </template>
            </template>
          </a-table>
        </a-card>
      </a-tab-pane>

      <!-- Tab4: Hedge Report 套保报告 -->
      <a-tab-pane key="report" tab="套保报告 Report">
        <a-row :gutter="16">
          <a-col :span="24">
            <a-card title="套保有效性报告 Hedge Effectiveness Report" size="small">
              <a-descriptions bordered size="small" :column="3">
                <a-descriptions-item label="套保关系数 Relationships">3</a-descriptions-item>
                <a-descriptions-item label="有效关系数 Effective">2</a-descriptions-item>
                <a-descriptions-item label="总体有效比率 Overall Ratio">
                  <a-tag color="green">92.5%</a-tag>
                </a-descriptions-item>
                <a-descriptions-item label="套保公允价值变动 FV Change">¥+1,250,000</a-descriptions-item>
                <a-descriptions-item label="被套期项目变动 Item Change">¥-1,180,000</a-descriptions-item>
                <a-descriptions-item label="套保无效部分 Ineffectiveness">¥70,000</a-descriptions-item>
              </a-descriptions>
            </a-card>
          </a-col>
        </a-row>
        <a-row :gutter="16" style="margin-top:16px">
          <a-col :span="24">
            <a-card title="历史有效性比率趋势 Historical Effectiveness Ratio" size="small">
              <div ref="lineChartRef" style="width:100%;height:320px"></div>
            </a-card>
          </a-col>
        </a-row>
      </a-tab-pane>
    </a-tabs>
  </div>
</template>

<script setup lang="ts">
import { ref, onMounted, nextTick } from 'vue'
import { message } from 'ant-design-vue'
import * as echarts from 'echarts'
import dayjs from 'dayjs'

const activeTab = ref('relationships')

// --- Tab1: Hedge Relationships 套期关系 ---
const relColumns = [
  { title: 'ID', dataIndex: 'id', key: 'id' },
  { title: '被套期项目 Hedged Item', dataIndex: 'hedgedItem', key: 'hedgedItem' },
  { title: '套期工具 Hedging Instrument', dataIndex: 'hedgingInstrument', key: 'hedgingInstrument' },
  { title: '套期类型 Type', dataIndex: 'hedgeType', key: 'hedgeType' },
  { title: '开始日期 Start', dataIndex: 'startDate', key: 'startDate' },
  { title: '状态 Status', dataIndex: 'status', key: 'status' },
  { title: '操作 Actions', key: 'actions' }
]

const relList = ref([
  { id: 1, hedgedItem: 'USD应收账款 5,000,000', hedgingInstrument: 'USD/CNY远期合约', hedgeType: 'FAIR_VALUE', startDate: '2026-01-15', status: 'ACTIVE' },
  { id: 2, hedgedItem: 'EUR应付账款 3,000,000', hedgingInstrument: 'EUR/CNY期权', hedgeType: 'CASH_FLOW', startDate: '2026-02-20', status: 'ACTIVE' },
  { id: 3, hedgedItem: 'JPY预期采购 200,000,000', hedgingInstrument: 'JPY/CNY远期合约', hedgeType: 'CASH_FLOW', startDate: '2026-03-10', status: 'INACTIVE' }
])

const showRelModal = ref(false)
const newRel = ref({ hedgedItem: '', hedgingInstrument: '', hedgeType: 'FAIR_VALUE' })

function addRelationship() {
  relList.value.push({
    id: relList.value.length + 1,
    hedgedItem: newRel.value.hedgedItem || '新套期项目',
    hedgingInstrument: newRel.value.hedgingInstrument || '新套期工具',
    hedgeType: newRel.value.hedgeType,
    startDate: dayjs().format('YYYY-MM-DD'),
    status: 'ACTIVE'
  })
  showRelModal.value = false
  message.success('套期关系创建成功')
}

function delRelationship(id: number) {
  relList.value = relList.value.filter(r => r.id !== id)
  message.success('删除成功')
}

// --- Tab2: Effectiveness Tests 有效性测试 ---
const testColumns = [
  { title: '测试编号 Test No', dataIndex: 'testNo', key: 'testNo' },
  { title: '套期关系ID Rel ID', dataIndex: 'relId', key: 'relId' },
  { title: '测试日期 Date', dataIndex: 'testDate', key: 'testDate' },
  { title: '套期比率 Hedge Ratio', dataIndex: 'hedgeRatio', key: 'hedgeRatio' },
  { title: '测试结果 Result', dataIndex: 'result', key: 'result' },
  { title: '方法 Method', dataIndex: 'method', key: 'method' }
]

const testList = ref([
  { id: 1, testNo: 'T-20260601-001', relId: 1, testDate: '2026-03-31', hedgeRatio: 0.95, result: 'PASS', method: '回归分析 Regression' },
  { id: 2, testNo: 'T-20260601-002', relId: 2, testDate: '2026-03-31', hedgeRatio: 0.88, result: 'PASS', method: '回归分析 Regression' },
  { id: 3, testNo: 'T-20260601-003', relId: 3, testDate: '2026-03-31', hedgeRatio: 0.72, result: 'FAIL', method: '美元抵消法 Dollar Offset' }
])

function runEffectivenessTest() {
  const testNo = `T-${dayjs().format('YYYYMMDD')}-${String(testList.value.length + 1).padStart(3, '0')}`
  testList.value.push({
    id: testList.value.length + 1,
    testNo,
    relId: 1,
    testDate: dayjs().format('YYYY-MM-DD'),
    hedgeRatio: Math.round(Math.random() * 40 + 60) / 100,
    result: Math.random() > 0.3 ? 'PASS' : 'FAIL',
    method: '回归分析 Regression'
  })
  message.success('有效性测试已执行')
}

// --- Tab3: Journal Entries 会计分录 ---
const journalColumns = [
  { title: '分录编号 Entry No', dataIndex: 'entryNo', key: 'entryNo' },
  { title: '日期 Date', dataIndex: 'entryDate', key: 'entryDate' },
  { title: '科目 Account', dataIndex: 'account', key: 'account' },
  { title: '借贷 Dr/Cr', dataIndex: 'drCr', key: 'drCr' },
  { title: '金额 Amount(¥)', dataIndex: 'amount', key: 'amount' },
  { title: '摘要 Summary', dataIndex: 'summary', key: 'summary' }
]

const journalList = ref([
  { id: 1, entryNo: 'JE-20260601-001', entryDate: '2026-06-01', account: '套期工具—远期合约', drCr: 'DR', amount: '1,250,000', summary: '远期合约公允价值变动' },
  { id: 2, entryNo: 'JE-20260601-002', entryDate: '2026-06-01', account: '套期损益', drCr: 'CR', amount: '1,180,000', summary: '被套期项目重估' },
  { id: 3, entryNo: 'JE-20260601-003', entryDate: '2026-06-01', account: '财务费用—套期无效', drCr: 'DR', amount: '70,000', summary: '套期无效部分确认' },
  { id: 4, entryNo: 'JE-20260531-001', entryDate: '2026-05-31', account: '其他综合收益—现金流量套期', drCr: 'CR', amount: '520,000', summary: '现金流量套期有效部分' }
])

// --- Tab4: Hedge Report ECharts 历史有效性比率折线图 ---
const lineChartRef = ref<HTMLElement | null>(null)
let lineChart: echarts.ECharts | null = null

function initLineChart() {
  if (!lineChartRef.value) return
  lineChart = echarts.init(lineChartRef.value)
  const months = ['Jan', 'Feb', 'Mar', 'Apr', 'May', 'Jun']
  lineChart.setOption({
    tooltip: { trigger: 'axis' },
    legend: { data: ['Rel-1 USD远期', 'Rel-2 EUR期权', 'Rel-3 JPY远期', '阈值80%'], bottom: 0 },
    grid: { left: 50, right: 30, top: 30, bottom: 40 },
    xAxis: { type: 'category', data: months },
    yAxis: { type: 'value', min: 0.5, max: 1.0, axisLabel: { formatter: '{value}' } },
    series: [
      { name: 'Rel-1 USD远期', type: 'line', data: [0.92, 0.94, 0.95, 0.93, 0.96, 0.95], smooth: true, lineStyle: { color: '#1677ff' }, itemStyle: { color: '#1677ff' } },
      { name: 'Rel-2 EUR期权', type: 'line', data: [0.85, 0.87, 0.88, 0.86, 0.90, 0.88], smooth: true, lineStyle: { color: '#52c41a' }, itemStyle: { color: '#52c41a' } },
      { name: 'Rel-3 JPY远期', type: 'line', data: [0.78, 0.76, 0.72, 0.74, 0.70, 0.72], smooth: true, lineStyle: { color: '#fa8c16' }, itemStyle: { color: '#fa8c16' } },
      {
        name: '阈值80%',
        type: 'line',
        data: [0.8, 0.8, 0.8, 0.8, 0.8, 0.8],
        lineStyle: { type: 'dashed', color: '#f5222d' },
        itemStyle: { color: '#f5222d' },
        symbol: 'none'
      }
    ]
  })
}

onMounted(() => {
  nextTick(() => initLineChart())
})
</script>

<style scoped>
.hedge-accounting { background: #fff; border-radius: 8px; padding: 24px; }
.page-title { margin: 0 0 20px; font-size: 18px; color: #333; font-weight: 600; }
</style>
