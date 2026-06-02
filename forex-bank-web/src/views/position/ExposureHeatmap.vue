<template>
  <div class="exposure-heatmap">
    <h2>敞口多维度分析</h2>

    <a-tabs v-model:activeKey="activeTab" type="card">
      <a-tab-pane key="heatmap" tab="敞口热力图">
        <a-card>
          <v-chart :option="heatmapOption" style="height: 500px" autoresize />
        </a-card>
      </a-tab-pane>

      <a-tab-pane key="maturity" tab="到期日分析">
        <a-card>
          <a-table
            :columns="maturityColumns"
            :data-source="maturityData"
            :pagination="false"
            row-key="currency"
          >
            <template #bodyCell="{ column, record }">
              <template v-if="column.key === 'currency'">
                <a-tag color="#1677ff">{{ record.currency }}</a-tag>
              </template>
              <template v-else-if="column.key !== 'currency'">
                <div class="amount-cell">
                  <div class="long-amount" v-if="record[column.key]?.long">多: {{ record[column.key].long }}</div>
                  <div class="short-amount" v-if="record[column.key]?.short">空: {{ record[column.key].short }}</div>
                  <div :class="['net-amount', record[column.key]?.net >= 0 ? 'positive' : 'negative']">
                    净: {{ record[column.key]?.net }}
                  </div>
                </div>
              </template>
            </template>
          </a-table>
        </a-card>
      </a-tab-pane>

      <a-tab-pane key="custom" tab="自定义维度">
        <a-card>
          <div style="margin-bottom: 16px">
            <span style="margin-right: 12px; font-weight: 500">选择维度：</span>
            <a-checkbox-group v-model:value="selectedDims" :options="dimOptions" />
            <a-button type="primary" style="margin-left: 16px" @click="handleCustomAnalyze">分析</a-button>
          </div>
          <a-table
            :columns="customColumns"
            :data-source="customData"
            :pagination="false"
            row-key="id"
          />
        </a-card>
      </a-tab-pane>
    </a-tabs>
  </div>
</template>

<script setup lang="ts">
import { ref, reactive, computed } from 'vue'
import VChart from 'vue-echarts'
import * as echarts from 'echarts/core'
import { HeatmapChart, BarChart } from 'echarts/charts'
import { TitleComponent, TooltipComponent, GridComponent, VisualMapComponent } from 'echarts/components'
import { CanvasRenderer } from 'echarts/renderers'

echarts.use([HeatmapChart, BarChart, TitleComponent, TooltipComponent, GridComponent, VisualMapComponent, CanvasRenderer])

const activeTab = ref('heatmap')

interface Amounts { long: number; short: number; net: number }

interface MaturityRecord {
  currency: string
  m1: Amounts
  m1m3: Amounts
  m3m12: Amounts
  y1plus: Amounts
  total: Amounts
}

interface CustomRecord {
  id: string
  dimName: string
  longAmount: number
  shortAmount: number
  netAmount: number
}

const currencies = ['USD', 'EUR', 'JPY', 'GBP', 'HKD']
const products = ['SPOT', 'FORWARD', 'SWAP', 'OPTION', 'FUTURES']

const heatmapOption = computed(() => ({
  title: { text: '币种 × 产品类型 敞口热力图', left: 'center', textStyle: { fontSize: 14 } },
  tooltip: { position: 'top' },
  grid: { left: 80, right: 60, top: 50, bottom: 60 },
  xAxis: { type: 'category', data: products, splitArea: { show: true } },
  yAxis: { type: 'category', data: currencies, splitArea: { show: true } },
  visualMap: { min: -5000, max: 5000, calculable: true, orient: 'horizontal', left: 'center', bottom: 0, inRange: { color: ['#f5222d', '#fff7e6', '#52c41a'] } },
  series: [{
    type: 'heatmap',
    data: [
      [0, 0, 4500], [0, 1, -1200], [0, 2, 2800], [0, 3, -600], [0, 4, 1500],
      [1, 0, -2100], [1, 1, 3800], [1, 2, -900], [1, 3, 3200], [1, 4, -300],
      [2, 0, 1500], [2, 1, -800], [2, 2, 4300], [2, 3, -1500], [2, 4, 900],
      [3, 0, -3500], [3, 1, 2200], [3, 2, -1800], [3, 3, 4100], [3, 4, -1100],
      [4, 0, 800], [4, 1, -2800], [4, 2, 1600], [4, 3, -2200], [4, 4, 3500]
    ],
    label: { show: true, fontSize: 11 }
  }]
}))

const maturityColumns = [
  { title: '币种', dataIndex: 'currency', key: 'currency', width: 100 },
  { title: '1M 内', dataIndex: 'm1', key: 'm1' },
  { title: '1-3M', dataIndex: 'm1m3', key: 'm1m3' },
  { title: '3-12M', dataIndex: 'm3m12', key: 'm3m12' },
  { title: '1Y+', dataIndex: 'y1plus', key: 'y1plus' },
  { title: '合计', dataIndex: 'total', key: 'total' }
]

const maturityData = ref<MaturityRecord[]>([
  { currency: 'USD', m1: { long: 50000, short: 30000, net: 20000 }, m1m3: { long: 80000, short: 60000, net: 20000 }, m3m12: { long: 40000, short: 55000, net: -15000 }, y1plus: { long: 20000, short: 15000, net: 5000 }, total: { long: 190000, short: 160000, net: 30000 } },
  { currency: 'EUR', m1: { long: 35000, short: 45000, net: -10000 }, m1m3: { long: 60000, short: 40000, net: 20000 }, m3m12: { long: 30000, short: 35000, net: -5000 }, y1plus: { long: 25000, short: 20000, net: 5000 }, total: { long: 150000, short: 140000, net: 10000 } },
  { currency: 'JPY', m1: { long: 20000, short: 15000, net: 5000 }, m1m3: { long: 45000, short: 50000, net: -5000 }, m3m12: { long: 35000, short: 25000, net: 10000 }, y1plus: { long: 15000, short: 20000, net: -5000 }, total: { long: 115000, short: 110000, net: 5000 } },
  { currency: 'GBP', m1: { long: 28000, short: 38000, net: -10000 }, m1m3: { long: 55000, short: 42000, net: 13000 }, m3m12: { long: 25000, short: 32000, net: -7000 }, y1plus: { long: 18000, short: 12000, net: 6000 }, total: { long: 126000, short: 124000, net: 2000 } },
  { currency: 'HKD', m1: { long: 15000, short: 10000, net: 5000 }, m1m3: { long: 22000, short: 28000, net: -6000 }, m3m12: { long: 18000, short: 15000, net: 3000 }, y1plus: { long: 8000, short: 12000, net: -4000 }, total: { long: 63000, short: 65000, net: -2000 } }
])

const dimOptions = ['币种', '产品类型', '交易员', '机构']
const selectedDims = ref<string[]>(['币种', '产品类型'])

const customColumns = [
  { title: '维度名称', dataIndex: 'dimName', key: 'dimName' },
  { title: '多头金额', dataIndex: 'longAmount', key: 'longAmount' },
  { title: '空头金额', dataIndex: 'shortAmount', key: 'shortAmount' },
  { title: '净敞口', dataIndex: 'netAmount', key: 'netAmount' }
]

const customData = ref<CustomRecord[]>([])

function handleCustomAnalyze() {
  const dim = selectedDims.value.join(' × ')
  customData.value = [
    { id: '1', dimName: `${dim}: USD`, longAmount: 120000, shortAmount: 90000, netAmount: 30000 },
    { id: '2', dimName: `${dim}: EUR`, longAmount: 95000, shortAmount: 105000, netAmount: -10000 },
    { id: '3', dimName: `${dim}: JPY`, longAmount: 75000, shortAmount: 68000, netAmount: 7000 },
    { id: '4', dimName: `${dim}: GBP`, longAmount: 88000, shortAmount: 91000, netAmount: -3000 },
    { id: '5', dimName: `${dim}: HKD`, longAmount: 45000, shortAmount: 52000, netAmount: -7000 }
  ]
}

handleCustomAnalyze()
</script>

<style scoped>
.exposure-heatmap h2 { margin-bottom: 16px; }
.amount-cell { font-size: 12px; line-height: 1.6; }
.long-amount { color: #52c41a; }
.short-amount { color: #f5222d; }
.net-amount { font-weight: 600; }
.net-amount.positive { color: #52c41a; }
.net-amount.negative { color: #f5222d; }
</style>
