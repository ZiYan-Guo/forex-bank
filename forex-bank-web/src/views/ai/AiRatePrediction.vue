<template>
  <div class="ai-rate-prediction">
    <h2>AI汇率预测与风险预警</h2>

    <a-card style="margin-bottom:16px">
      <a-form layout="inline" :model="filterForm">
        <a-form-item label="币种对">
          <a-select v-model:value="filterForm.currencyPair" style="width:160px">
            <a-select-option value="USD/CNY">USD/CNY</a-select-option>
            <a-select-option value="EUR/CNY">EUR/CNY</a-select-option>
            <a-select-option value="JPY/CNY">JPY/CNY</a-select-option>
            <a-select-option value="GBP/CNY">GBP/CNY</a-select-option>
          </a-select>
        </a-form-item>
        <a-form-item label="预测类型">
          <a-select v-model:value="filterForm.predictType" style="width:160px">
            <a-select-option value="HOURLY">小时级</a-select-option>
            <a-select-option value="DAILY">日级</a-select-option>
            <a-select-option value="WEEKLY">周级</a-select-option>
          </a-select>
        </a-form-item>
        <a-form-item>
          <a-button type="primary" :loading="predicting" @click="startPrediction">开始预测</a-button>
        </a-form-item>
      </a-form>
    </a-card>

    <a-card title="预测结果" style="margin-bottom:16px" v-if="predictionResult">
      <a-row :gutter="24">
        <a-col :span="12">
          <v-chart :option="chartOption" style="height:360px" />
        </a-col>
        <a-col :span="12">
          <a-descriptions :column="1" bordered size="small">
            <a-descriptions-item label="当前汇率">{{ predictionResult.currentRate }}</a-descriptions-item>
            <a-descriptions-item label="预测汇率">{{ predictionResult.predictedRate }}</a-descriptions-item>
            <a-descriptions-item label="置信上界">{{ predictionResult.upperBound }}</a-descriptions-item>
            <a-descriptions-item label="置信下界">{{ predictionResult.lowerBound }}</a-descriptions-item>
            <a-descriptions-item label="置信度">{{ predictionResult.confidence }}%</a-descriptions-item>
            <a-descriptions-item label="趋势">
              <span :style="{ color: predictionResult.trend === 'up' ? '#f5222d' : '#52c41a' }">
                {{ predictionResult.trend === 'up' ? '↑ 看涨' : '↓ 看跌' }}
              </span>
            </a-descriptions-item>
            <a-descriptions-item label="预测时间">{{ predictionResult.forecastTime }}</a-descriptions-item>
          </a-descriptions>
        </a-col>
      </a-row>
    </a-card>

    <a-card title="风险敞口预测" style="margin-bottom:16px">
      <a-table
        :columns="exposureColumns"
        :data-source="exposureData"
        :pagination="false"
        row-key="currency"
      >
        <template #bodyCell="{ column, record }">
          <template v-if="column.key === 'riskLevel'">
            <a-tag :color="record.riskLevel === '高' ? 'red' : record.riskLevel === '中' ? 'orange' : 'green'">
              {{ record.riskLevel }}
            </a-tag>
          </template>
        </template>
      </a-table>
    </a-card>

    <a-card title="预警阈值配置">
      <a-form layout="inline">
        <a-form-item v-for="ccy in ['USD', 'EUR', 'JPY', 'GBP']" :key="ccy" :label="ccy + '/CNY'">
          <a-input-number
            v-model:value="thresholds[ccy]"
            :min="0"
            :step="10000"
            style="width:140px"
            addon-after="万元"
          />
        </a-form-item>
        <a-form-item>
          <a-button type="primary" @click="saveThresholds">保存阈值</a-button>
        </a-form-item>
      </a-form>
    </a-card>
  </div>
</template>

<script setup lang="ts">
import { ref, reactive, computed } from 'vue'
import { message } from 'ant-design-vue'
import VChart from 'vue-echarts'
import { use } from 'echarts/core'
import { LineChart } from 'echarts/charts'
import { GridComponent, TooltipComponent, LegendComponent } from 'echarts/components'
import { CanvasRenderer } from 'echarts/renderers'

use([LineChart, GridComponent, TooltipComponent, LegendComponent, CanvasRenderer])

const filterForm = reactive({
  currencyPair: 'USD/CNY',
  predictType: 'DAILY'
})

const predicting = ref(false)
const predictionResult = ref<any>(null)

const thresholds = reactive<Record<string, number>>({
  USD: 500,
  EUR: 300,
  JPY: 1000,
  GBP: 200
})

const exposureColumns = [
  { title: '币种', dataIndex: 'currency', key: 'currency' },
  { title: '预计流入', dataIndex: 'expectedInflow', key: 'expectedInflow' },
  { title: '预计流出', dataIndex: 'expectedOutflow', key: 'expectedOutflow' },
  { title: '净敞口', dataIndex: 'netExposure', key: 'netExposure' },
  { title: '风险等级', dataIndex: 'riskLevel', key: 'riskLevel' }
]

const exposureData = ref([
  { currency: 'USD', expectedInflow: '1,200万', expectedOutflow: '800万', netExposure: '+400万', riskLevel: '低' },
  { currency: 'EUR', expectedInflow: '600万', expectedOutflow: '900万', netExposure: '-300万', riskLevel: '中' },
  { currency: 'JPY', expectedInflow: '5,000万', expectedOutflow: '8,000万', netExposure: '-3,000万', riskLevel: '高' },
  { currency: 'GBP', expectedInflow: '400万', expectedOutflow: '350万', netExposure: '+50万', riskLevel: '低' }
])

const chartOption = computed(() => ({
  tooltip: { trigger: 'axis' },
  legend: { data: ['预测汇率', '置信上界', '置信下界'] },
  grid: { left: '3%', right: '4%', bottom: '3%', containLabel: true },
  xAxis: {
    type: 'category',
    data: ['T-4', 'T-3', 'T-2', 'T-1', 'T', 'T+1', 'T+2', 'T+3', 'T+4']
  },
  yAxis: { type: 'value' },
  series: [
    {
      name: '预测汇率',
      type: 'line',
      data: [6.85, 6.86, 6.84, 6.87, 6.88, 6.89, 6.90, 6.91, 6.92],
      smooth: true,
      lineStyle: { width: 2 }
    },
    {
      name: '置信上界',
      type: 'line',
      data: [6.88, 6.89, 6.87, 6.90, 6.91, 6.93, 6.94, 6.95, 6.96],
      lineStyle: { type: 'dashed', color: '#faad14' },
      itemStyle: { color: '#faad14' }
    },
    {
      name: '置信下界',
      type: 'line',
      data: [6.82, 6.83, 6.81, 6.84, 6.85, 6.85, 6.86, 6.87, 6.88],
      lineStyle: { type: 'dashed', color: '#faad14' },
      itemStyle: { color: '#faad14' }
    }
  ]
}))

function startPrediction() {
  predicting.value = true
  setTimeout(() => {
    predicting.value = false
    predictionResult.value = {
      currentRate: '6.8800',
      predictedRate: '6.9200',
      upperBound: '6.9600',
      lowerBound: '6.8800',
      confidence: 95,
      trend: 'up',
      forecastTime: new Date().toLocaleString()
    }
    message.success('预测完成')
  }, 1500)
}

function saveThresholds() {
  message.success('阈值配置已保存')
}
</script>

<style scoped>
.ai-rate-prediction h2 { margin-bottom: 16px; }
</style>
