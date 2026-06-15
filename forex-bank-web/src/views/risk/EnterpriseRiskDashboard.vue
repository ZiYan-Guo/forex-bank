<template>
  <!-- 企业风险仪表板 Enterprise Risk Dashboard -->
  <div class="enterprise-risk-dashboard">
    <h3 class="page-title">企业风险仪表板</h3>

    <!-- Customer selector 客户选择器 -->
    <a-row :gutter="16" class="selector-row">
      <a-col :span="8">
        <a-card size="small">
          <a-form layout="inline">
            <a-form-item label="选择客户 Customer">
              <a-select v-model:value="selectedCustomer" style="width:240px" @change="onCustomerChange">
                <a-select-option value="C001">企业A (出口型) Enterprise A</a-select-option>
                <a-select-option value="C002">企业B (进口型) Enterprise B</a-select-option>
                <a-select-option value="C003">企业C (综合型) Enterprise C</a-select-option>
              </a-select>
            </a-form-item>
          </a-form>
        </a-card>
      </a-col>
    </a-row>

    <!-- Mid row: FX exposure bar chart + VaR gauge 中间行：外汇敞口柱状图+VaR仪表盘 -->
    <a-row :gutter="16" class="chart-row">
      <a-col :span="12">
        <a-card title="外汇敞口 - FX Exposure by Currency" size="small">
          <div ref="barChartRef" style="width:100%;height:300px"></div>
        </a-card>
      </a-col>
      <a-col :span="12">
        <a-card title="风险价值 - VaR (Value at Risk)" size="small">
          <div ref="gaugeChartRef" style="width:100%;height:300px"></div>
        </a-card>
      </a-col>
    </a-row>

    <!-- Bottom row: stress test + hedge recommendation 底部：情景压力测试+套保建议 -->
    <a-row :gutter="16" class="bottom-row">
      <a-col :span="12">
        <a-card title="情景压力测试 Stress Test" size="small">
          <p class="sub-desc">人民币升值对敞口影响 Impact of CNY appreciation on exposure</p>
          <a-table :columns="stressTestColumns" :data-source="stressTestData" :pagination="false" row-key="scenario" size="small">
            <template #bodyCell="{ column, record }">
              <template v-if="column.key === 'impact'">
                <span :style="{ color: record.impact < 0 ? '#f5222d' : '#52c41a', fontWeight: 'bold' }">
                  {{ record.impact.toLocaleString() }}
                </span>
              </template>
              <template v-if="column.key === 'impactPct'">
                <span :style="{ color: record.impactPct < 0 ? '#f5222d' : '#52c41a' }">
                  {{ record.impactPct > 0 ? '+' : '' }}{{ record.impactPct }}%
                </span>
              </template>
            </template>
          </a-table>
        </a-card>
      </a-col>
      <a-col :span="12">
        <a-card title="套保建议 Hedge Recommendation" size="small">
          <a-descriptions bordered size="small" :column="1">
            <a-descriptions-item label="推荐套保比率 Recommended Ratio">
              <a-tag color="blue">65%</a-tag>
            </a-descriptions-item>
            <a-descriptions-item label="敞口分析 Exposure Analysis">
              当前企业存在较大USD多头敞口，建议通过远期+期权组合进行套保。
              The company currently has a significant long USD exposure.
              We recommend hedging through a combination of forwards and options.
            </a-descriptions-item>
            <a-descriptions-item label="推荐产品 Product Combo">
              <a-tag color="green">远期合约 Forward</a-tag>
              <a-tag color="purple" style="margin-left:8px">区间宝 Range Forward</a-tag>
              <a-tag color="orange" style="margin-left:8px">领口期权 Collar</a-tag>
            </a-descriptions-item>
            <a-descriptions-item label="套保期限 Tenor">6个月 - 12个月</a-descriptions-item>
            <a-descriptions-item label="预计套保成本 Est. Cost">
              <span style="color:#52c41a">零成本组合 Zero-cost combo available</span>
            </a-descriptions-item>
          </a-descriptions>
        </a-card>
      </a-col>
    </a-row>
  </div>
</template>

<script setup lang="ts">
import { ref, onMounted, nextTick } from 'vue'
import * as echarts from 'echarts'

const selectedCustomer = ref('C001')

// Stress test table 情景压力测试表
const stressTestColumns = [
  { title: '情景 Scenario', dataIndex: 'scenario', key: 'scenario' },
  { title: '汇率变动 Rate Change', dataIndex: 'rateChange', key: 'rateChange' },
  { title: '对敞口影响 Impact(¥)', dataIndex: 'impact', key: 'impact' },
  { title: '影响比例 Impact%', dataIndex: 'impactPct', key: 'impactPct' }
]

const stressTestData = ref([
  { scenario: '人民币升值2% CNY +2%', rateChange: '7.2536 → 7.1085', impact: -3500000, impactPct: -2.8 },
  { scenario: '人民币升值5% CNY +5%', rateChange: '7.2536 → 6.8909', impact: -8750000, impactPct: -7.0 },
  { scenario: '人民币升值10% CNY +10%', rateChange: '7.2536 → 6.5282', impact: -17500000, impactPct: -14.0 }
])

// Bar chart: FX exposure by currency 柱状图：按币种的外汇敞口
const barChartRef = ref<HTMLElement | null>(null)
let barChart: echarts.ECharts | null = null

// Gauge chart: VaR 仪表盘：风险价值
const gaugeChartRef = ref<HTMLElement | null>(null)
let gaugeChart: echarts.ECharts | null = null

const exposureDataMap: Record<string, { currencies: string[]; long: number[]; short: number[]; varValue: number }> = {
  C001: {
    currencies: ['USD', 'EUR', 'JPY', 'GBP', 'HKD', 'AUD'],
    long: [5200, 1800, 0, 600, 1200, 300],
    short: [800, 400, 2000, 0, 0, 0],
    varValue: 4.25
  },
  C002: {
    currencies: ['USD', 'EUR', 'JPY', 'GBP'],
    long: [1200, 0, 0, 500],
    short: [4800, 2200, 1500, 0],
    varValue: 3.15
  },
  C003: {
    currencies: ['USD', 'EUR', 'JPY', 'GBP'],
    long: [3000, 2000, 1000, 800],
    short: [2500, 1500, 800, 400],
    varValue: 2.80
  }
}

function initBarChart(data?: { currencies: string[]; long: number[]; short: number[] }) {
  if (!barChartRef.value) return
  if (!barChart) barChart = echarts.init(barChartRef.value)
  const d = data || exposureDataMap['C001']
  barChart.setOption({
    tooltip: { trigger: 'axis', axisPointer: { type: 'shadow' } },
    legend: { data: ['多头 Long', '空头 Short'], bottom: 0 },
    grid: { left: 60, right: 30, top: 20, bottom: 40 },
    xAxis: { type: 'category', data: d.currencies },
    yAxis: { type: 'value', name: '万元(¥10K)' },
    series: [
      { name: '多头 Long', type: 'bar', data: d.long, itemStyle: { color: '#1677ff' }, barGap: '10%' },
      { name: '空头 Short', type: 'bar', data: d.short, itemStyle: { color: '#f5222d' } }
    ]
  })
}

function initGaugeChart(varValue?: number) {
  if (!gaugeChartRef.value) return
  if (!gaugeChart) gaugeChart = echarts.init(gaugeChartRef.value)
  const val = varValue ?? exposureDataMap['C001'].varValue
  gaugeChart.setOption({
    series: [{
      type: 'gauge',
      startAngle: 210,
      endAngle: -30,
      center: ['50%', '60%'],
      radius: '85%',
      min: 0,
      max: 10,
      splitNumber: 10,
      axisLine: {
        show: true,
        lineStyle: {
          width: 20,
          color: [[0.3, '#52c41a'], [0.7, '#fa8c16'], [1, '#f5222d']]
        }
      },
      pointer: { length: '70%', width: 6, itemStyle: { color: 'auto' } },
      axisTick: { distance: -20, length: 8, lineStyle: { width: 2 } },
      splitLine: { distance: -24, length: 18, lineStyle: { width: 3 } },
      axisLabel: { distance: 30, fontSize: 11 },
      detail: {
        valueAnimation: true,
        formatter: '{value} 百万¥',
        fontSize: 16,
        offsetCenter: [0, '65%'],
        color: val > 5 ? '#f5222d' : '#333'
      },
      data: [{ value: val, name: 'VaR (95% 置信度)' }]
    }]
  })
}

function onCustomerChange(val: string) {
  const data = exposureDataMap[val]
  if (!data) return
  barChart?.setOption({
    xAxis: { data: data.currencies },
    series: [
      { name: '多头 Long', data: data.long },
      { name: '空头 Short', data: data.short }
    ]
  })
  gaugeChart?.setOption({
    series: [{ data: [{ value: data.varValue }] }],
    detail: { color: data.varValue > 5 ? '#f5222d' : '#333' }
  })
}

onMounted(() => {
  nextTick(() => {
    initBarChart()
    initGaugeChart()
  })
})
</script>

<style scoped>
.enterprise-risk-dashboard { background: #fff; border-radius: 8px; padding: 24px; }
.page-title { margin: 0 0 20px; font-size: 18px; color: #333; font-weight: 600; }
.selector-row { margin-bottom: 16px; }
.chart-row { margin-bottom: 16px; }
.bottom-row { margin-top: 16px; }
.sub-desc { color: #888; font-size: 13px; margin-bottom: 12px; }
</style>
