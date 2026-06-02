<template>
  <div class="pnl-attribution">
    <h2>损益归因分析</h2>

    <a-card class="filter-card">
      <a-form layout="inline" :model="filterForm">
        <a-form-item label="交易编号">
          <a-input v-model:value="filterForm.tradeId" placeholder="输入交易编号" style="width: 180px" />
        </a-form-item>
        <a-form-item label="估值日期">
          <a-date-picker v-model:value="filterForm.valuationDate" style="width: 160px" />
        </a-form-item>
        <a-form-item>
          <a-button type="primary" @click="handleAnalyze">分析</a-button>
        </a-form-item>
      </a-form>
    </a-card>

    <a-row :gutter="16" style="margin-top: 16px">
      <a-col :span="16">
        <a-card title="损益分解">
          <v-chart :option="barOption" style="height: 350px" autoresize />
        </a-card>
      </a-col>
      <a-col :span="8">
        <a-card title="成分明细" size="small">
          <div class="pnl-detail" v-for="item in pnlComponents" :key="item.key">
            <span class="pnl-label">{{ item.label }}</span>
            <span :class="['pnl-value', item.value >= 0 ? 'positive' : 'negative']">
              {{ item.value >= 0 ? '+' : '' }}{{ item.value.toFixed(2) }}
            </span>
          </div>
        </a-card>
      </a-col>
    </a-row>

    <a-card title="归因历史" style="margin-top: 16px">
      <a-table
        :columns="columns"
        :data-source="historyData"
        :pagination="pagination"
        row-key="date"
        @change="handleTableChange"
      >
        <template #bodyCell="{ column, record }">
          <template v-if="column.key === 'tradePnl'">
            <span :style="{ color: record.tradePnl >= 0 ? '#52c41a' : '#f5222d' }">
              {{ record.tradePnl >= 0 ? '+' : '' }}{{ record.tradePnl }}
            </span>
          </template>
          <template v-else-if="column.key === 'deltaPnl' || column.key === 'gammaPnl' || column.key === 'vegaPnl' || column.key === 'thetaPnl' || column.key === 'carryPnl'">
            <span :style="{ color: record[column.key] >= 0 ? '#52c41a' : '#f5222d' }">
              {{ record[column.key] >= 0 ? '+' : '' }}{{ record[column.key] }}
            </span>
          </template>
        </template>
      </a-table>
    </a-card>
  </div>
</template>

<script setup lang="ts">
import { ref, reactive, computed } from 'vue'
import VChart from 'vue-echarts'
import * as echarts from 'echarts/core'
import { BarChart } from 'echarts/charts'
import { TitleComponent, TooltipComponent, GridComponent, LegendComponent } from 'echarts/components'
import { CanvasRenderer } from 'echarts/renderers'
import type { TablePaginationConfig } from 'ant-design-vue'

echarts.use([BarChart, TitleComponent, TooltipComponent, GridComponent, LegendComponent, CanvasRenderer])

interface PnlComponent {
  key: string
  label: string
  value: number
}

interface PnlHistory {
  date: string
  tradePnl: number
  deltaPnl: number
  gammaPnl: number
  vegaPnl: number
  thetaPnl: number
  carryPnl: number
}

const filterForm = reactive({ tradeId: '', valuationDate: null as string | null })

const pnlComponents = ref<PnlComponent[]>([
  { key: 'deltaPnl', label: 'Delta PnL', value: 12500.50 },
  { key: 'gammaPnl', label: 'Gamma PnL', value: -2300.75 },
  { key: 'vegaPnl', label: 'Vega PnL', value: 4100.20 },
  { key: 'thetaPnl', label: 'Theta PnL', value: -850.30 },
  { key: 'carryPnl', label: 'Carry PnL', value: 1600.00 },
  { key: 'tradePnl', label: '累计 Trade PnL', value: 15049.65 }
])

const barOption = computed(() => ({
  title: { text: '损益归因分解', left: 'center', textStyle: { fontSize: 14 } },
  tooltip: { trigger: 'axis' },
  legend: { data: ['deltaPnl', 'thetaPnl', 'gammaPnl', 'vegaPnl', 'carryPnl', 'tradePnl'], top: 30 },
  grid: { left: 60, right: 30, top: 70, bottom: 40 },
  xAxis: { type: 'category', data: ['Trade 001', 'Trade 002', 'Trade 003', 'Trade 004', 'Trade 005'] },
  yAxis: { type: 'value' },
  series: [
    { name: 'deltaPnl', type: 'bar', data: [12500, 8200, -3400, 6700, 9100], itemStyle: { color: '#1677ff' } },
    { name: 'thetaPnl', type: 'bar', data: [-850, -620, 410, -310, -540], itemStyle: { color: '#52c41a' } },
    { name: 'gammaPnl', type: 'bar', data: [-2300, 1500, -980, 2100, -1200], itemStyle: { color: '#fa8c16' } },
    { name: 'vegaPnl', type: 'bar', data: [4100, -1800, 2600, -900, 3400], itemStyle: { color: '#722ed1' } },
    { name: 'carryPnl', type: 'bar', data: [1600, 1200, 2000, 1300, 1800], itemStyle: { color: '#eb2f96' } },
    { name: 'tradePnl', type: 'bar', data: [15050, 8480, 630, 8890, 12560], itemStyle: { color: '#13c2c2' } }
  ]
}))

const columns = [
  { title: '日期', dataIndex: 'date', key: 'date' },
  { title: 'Delta PnL', dataIndex: 'deltaPnl', key: 'deltaPnl' },
  { title: 'Gamma PnL', dataIndex: 'gammaPnl', key: 'gammaPnl' },
  { title: 'Vega PnL', dataIndex: 'vegaPnl', key: 'vegaPnl' },
  { title: 'Theta PnL', dataIndex: 'thetaPnl', key: 'thetaPnl' },
  { title: 'Carry PnL', dataIndex: 'carryPnl', key: 'carryPnl' },
  { title: '累计 PnL', dataIndex: 'tradePnl', key: 'tradePnl' }
]

const historyData = ref<PnlHistory[]>([
  { date: '2024-01-15', tradePnl: 15050, deltaPnl: 12500, gammaPnl: -2300, vegaPnl: 4100, thetaPnl: -850, carryPnl: 1600 },
  { date: '2024-01-16', tradePnl: 8480, deltaPnl: 8200, gammaPnl: 1500, vegaPnl: -1800, thetaPnl: -620, carryPnl: 1200 },
  { date: '2024-01-17', tradePnl: 630, deltaPnl: -3400, gammaPnl: -980, vegaPnl: 2600, thetaPnl: 410, carryPnl: 2000 },
  { date: '2024-01-18', tradePnl: 8890, deltaPnl: 6700, gammaPnl: 2100, vegaPnl: -900, thetaPnl: -310, carryPnl: 1300 },
  { date: '2024-01-19', tradePnl: 12560, deltaPnl: 9100, gammaPnl: -1200, vegaPnl: 3400, thetaPnl: -540, carryPnl: 1800 }
])

const pagination = reactive({ current: 1, pageSize: 10, total: historyData.value.length })
const pagination2 = reactive({ current: 1, pageSize: 10, total: historyData.value.length })

function handleAnalyze() {
  const vals = [Math.random() * 20000 - 5000, Math.random() * 3000 - 1500, Math.random() * 5000 - 2000, Math.random() * 6000 - 2000, Math.random() * 2000, 0]
  vals[5] = vals[0] + vals[1] + vals[2] + vals[3] + vals[4]
  pnlComponents.value = [
    { key: 'deltaPnl', label: 'Delta PnL', value: vals[0] },
    { key: 'gammaPnl', label: 'Gamma PnL', value: vals[1] },
    { key: 'vegaPnl', label: 'Vega PnL', value: vals[2] },
    { key: 'thetaPnl', label: 'Theta PnL', value: vals[3] },
    { key: 'carryPnl', label: 'Carry PnL', value: vals[4] },
    { key: 'tradePnl', label: '累计 Trade PnL', value: vals[5] }
  ]
}

function handleTableChange(pg: TablePaginationConfig) {
  pagination2.current = pg.current!
  pagination2.pageSize = pg.pageSize!
}
</script>

<style scoped>
.pnl-attribution h2 { margin-bottom: 16px; }
.filter-card { margin-bottom: 16px; }
.pnl-detail { display: flex; justify-content: space-between; padding: 10px 0; border-bottom: 1px solid #f0f0f0; }
.pnl-detail:last-child { border-bottom: none; }
.pnl-label { color: #666; font-size: 13px; }
.pnl-value { font-weight: 600; font-size: 15px; }
.pnl-value.positive { color: #52c41a; }
.pnl-value.negative { color: #f5222d; }
</style>
