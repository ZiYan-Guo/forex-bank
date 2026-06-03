<template>
  <div class="route-optimizer">
    <h2>路由优选</h2>

    <a-card title="路由分析请求" style="margin-bottom: 16px">
      <a-form :model="routeForm" layout="inline">
        <a-form-item label="付款币种" required>
          <a-select v-model:value="routeForm.payCurrency" style="width: 120px" placeholder="选择币种">
            <a-select-option value="USD">USD</a-select-option>
            <a-select-option value="EUR">EUR</a-select-option>
            <a-select-option value="GBP">GBP</a-select-option>
            <a-select-option value="CNY">CNY</a-select-option>
            <a-select-option value="JPY">JPY</a-select-option>
          </a-select>
        </a-form-item>
        <a-form-item label="收款币种" required>
          <a-select v-model:value="routeForm.receiveCurrency" style="width: 120px" placeholder="选择币种">
            <a-select-option value="USD">USD</a-select-option>
            <a-select-option value="EUR">EUR</a-select-option>
            <a-select-option value="GBP">GBP</a-select-option>
            <a-select-option value="CNY">CNY</a-select-option>
            <a-select-option value="JPY">JPY</a-select-option>
          </a-select>
        </a-form-item>
        <a-form-item label="金额" required>
          <a-input-number v-model:value="routeForm.amount" :min="0" :precision="2" style="width: 180px" placeholder="输入金额" />
        </a-form-item>
        <a-form-item label="对方国家" required>
          <a-input v-model:value="routeForm.country" style="width: 120px" placeholder="如 US" />
        </a-form-item>
        <a-form-item>
          <a-button type="primary" :loading="analyzing" @click="handleAnalyze">分析</a-button>
        </a-form-item>
      </a-form>
    </a-card>

    <a-card v-if="channels.length > 0" title="可选通道评分">
      <a-radio-group v-model:value="selectedChannel" style="width: 100%">
        <a-row :gutter="16">
          <a-col :span="6" v-for="ch in channels" :key="ch.channelCode">
            <a-card
              :class="['channel-card', { recommended: ch.recommendation === 'RECOMMENDED' && selectedChannel === ch.channelCode }]"
              :hoverable="true"
              @click="selectedChannel = ch.channelCode"
            >
              <template #title>
                <a-radio :value="ch.channelCode">
                  <span :style="{ color: ch.recommendation === 'RECOMMENDED' ? '#52c41a' : '' }">
                    {{ ch.channelName || ch.channelCode }}
                  </span>
                </a-radio>
              </template>
              <template #extra>
                <a-tag v-if="ch.recommendation === 'RECOMMENDED'" color="green">推荐</a-tag>
              </template>

              <div class="score-item">
                <span class="score-label">费用</span>
                <a-progress
                  :percent="ch.costScore"
                  :stroke-color="{ from: '#52c41a', to: '#1677ff' }"
                  size="small"
                />
                <span class="score-value">{{ ch.totalCost }} USD</span>
              </div>
              <div class="score-item">
                <span class="score-label">速度</span>
                <a-progress
                  :percent="ch.speedScore"
                  :stroke-color="{ from: '#fa8c16', to: '#52c41a' }"
                  size="small"
                />
                <span class="score-value">{{ ch.estimatedHours }}h</span>
              </div>
              <div class="score-item">
                <span class="score-label">截止时间</span>
                <a-progress
                  :percent="ch.cutoffScore"
                  :stroke-color="{ from: '#1677ff', to: '#722ed1' }"
                  size="small"
                />
                <span class="score-value">{{ ch.cutOffTime }}</span>
              </div>
              <div style="text-align: center; margin-top: 12px">
                <span class="total-score">{{ ch.routeScore }} 分</span>
              </div>
            </a-card>
          </a-col>
        </a-row>
      </a-radio-group>

      <div style="text-align: center; margin-top: 24px">
        <a-button type="primary" size="large" :disabled="!selectedChannel" @click="handleApplyRoute">
          应用此路由
        </a-button>
      </div>
    </a-card>
  </div>
</template>

<script setup lang="ts">
import { ref, reactive } from 'vue'
import { message } from 'ant-design-vue'

interface ChannelOption {
  channelCode: string
  channelName: string
  totalCost: number
  estimatedHours: number
  routeScore: number
  recommendation: string
  costScore: number
  speedScore: number
  cutoffScore: number
  cutOffTime: string
}

const routeForm = reactive({
  payCurrency: 'USD',
  receiveCurrency: 'EUR',
  amount: null as number | null,
  country: 'US'
})

const analyzing = ref(false)
const channels = ref<ChannelOption[]>([])
const selectedChannel = ref<string>('')

async function handleAnalyze() {
  if (!routeForm.payCurrency || !routeForm.receiveCurrency || !routeForm.amount || !routeForm.country) {
    message.warning('请填写完整的路由分析信息')
    return
  }

  analyzing.value = true
  setTimeout(() => {
    channels.value = [
      { channelCode: 'SWIFT', channelName: 'SWIFT', totalCost: 35, estimatedHours: 24, routeScore: 65, recommendation: 'ACCEPTABLE', costScore: 55, speedScore: 40, cutoffScore: 60, cutOffTime: '17:00' },
      { channelCode: 'CIPS', channelName: 'CIPS', totalCost: 25, estimatedHours: 4, routeScore: 82, recommendation: 'RECOMMENDED', costScore: 75, speedScore: 85, cutoffScore: 80, cutOffTime: '20:00' },
      { channelCode: 'CFXPS', channelName: '境内外币支付', totalCost: 15, estimatedHours: 4, routeScore: 78, recommendation: 'ACCEPTABLE', costScore: 90, speedScore: 85, cutoffScore: 55, cutOffTime: '16:30' },
      { channelCode: 'GFIX', channelName: 'GFIX通用传输', totalCost: 20, estimatedHours: 4, routeScore: 72, recommendation: 'ACCEPTABLE', costScore: 80, speedScore: 80, cutoffScore: 65, cutOffTime: '18:00' }
    ]

    const recommended = channels.value.find(c => c.recommendation === 'RECOMMENDED')
    if (recommended) {
      selectedChannel.value = recommended.channelCode
    }

    analyzing.value = false
    message.success('路由分析完成')
  }, 1000)
}

function handleApplyRoute() {
  const route = channels.value.find(c => c.channelCode === selectedChannel.value)
  if (route) {
    message.success(`已应用 ${route.channelName || route.channelCode} 路由，预计费用 ${route.totalCost} USD，${route.estimatedHours}h 到账`)
  }
}
</script>

<style scoped>
.route-optimizer h2 { margin-bottom: 16px; }
.channel-card {
  cursor: pointer;
  transition: border-color 0.3s;
}
.channel-card.recommended {
  border-color: #52c41a;
  box-shadow: 0 0 6px rgba(82, 196, 26, 0.3);
}
.channel-card:hover {
  border-color: #1677ff;
}
.score-item {
  margin-bottom: 8px;
}
.score-label {
  font-size: 12px;
  color: #8c8c8c;
  margin-bottom: 2px;
  display: block;
}
.score-value {
  font-size: 12px;
  color: #555;
  text-align: right;
  display: block;
}
.total-score {
  font-size: 24px;
  font-weight: bold;
  color: #1677ff;
}
</style>
