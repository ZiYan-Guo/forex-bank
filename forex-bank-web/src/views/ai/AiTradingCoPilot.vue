<template>
  <div class="ai-trading-copilot">
    <h2>AI交易助手</h2>

    <a-card title="自然语言交易" style="margin-bottom:16px">
      <a-textarea
        v-model:value="nlCommand"
        :rows="3"
        placeholder="例如: 帮我锁1个月后100万美元购汇"
      />
      <a-button type="primary" style="margin-top:12px" :loading="nlAnalyzing" @click="analyzeNL">
        分析
      </a-button>
      <div v-if="nlResult" class="nl-result" style="margin-top:16px">
        <a-descriptions :column="2" bordered size="small" title="解析结果">
          <a-descriptions-item label="意图">{{ nlResult.intent }}</a-descriptions-item>
          <a-descriptions-item label="交易方向">{{ nlResult.direction }}</a-descriptions-item>
          <a-descriptions-item label="币种对">{{ nlResult.currencyPair }}</a-descriptions-item>
          <a-descriptions-item label="金额">{{ nlResult.amount }}</a-descriptions-item>
          <a-descriptions-item label="期限">{{ nlResult.tenor }}</a-descriptions-item>
          <a-descriptions-item label="交易类型">{{ nlResult.productType }}</a-descriptions-item>
        </a-descriptions>
      </div>
    </a-card>

    <a-card title="智能套保推荐" style="margin-bottom:16px">
      <a-form layout="inline" :model="hedgeForm">
        <a-form-item label="业务类型">
          <a-select v-model:value="hedgeForm.bizType" style="width:140px">
            <a-select-option value="import">进口</a-select-option>
            <a-select-option value="export">出口</a-select-option>
          </a-select>
        </a-form-item>
        <a-form-item label="付款周期">
          <a-select v-model:value="hedgeForm.paymentCycle" style="width:140px">
            <a-select-option value="30">30天</a-select-option>
            <a-select-option value="60">60天</a-select-option>
            <a-select-option value="90">90天</a-select-option>
          </a-select>
        </a-form-item>
        <a-form-item label="风险偏好">
          <a-radio-group v-model:value="hedgeForm.riskAppetite">
            <a-radio value="conservative">保守</a-radio>
            <a-radio value="balanced">平衡</a-radio>
            <a-radio value="aggressive">积极</a-radio>
          </a-radio-group>
        </a-form-item>
        <a-form-item>
          <a-button type="primary" :loading="hedgeLoading" @click="getHedgeRecommendation">
            获取推荐
          </a-button>
        </a-form-item>
      </a-form>

      <a-list
        v-if="hedgeStrategies.length"
        :data-source="hedgeStrategies"
        style="margin-top:16px"
        bordered
      >
        <template #renderItem="{ item }">
          <a-list-item>
            <a-list-item-meta>
              <template #title>
                <a-tag color="blue">{{ item.strategy }}</a-tag>
                <span style="margin-left:8px">推荐比例: {{ item.ratio }}</span>
              </template>
              <template #description>{{ item.reason }}</template>
            </a-list-item-meta>
          </a-list-item>
        </template>
      </a-list>
    </a-card>

    <a-card title="智能报告">
      <a-form layout="inline" :model="reportForm">
        <a-form-item label="客户选择">
          <a-select
            v-model:value="reportForm.customerId"
            show-search
            placeholder="搜索客户..."
            style="width:200px"
            :filter-option="false"
          >
            <a-select-option value="1">张三贸易公司</a-select-option>
            <a-select-option value="2">李四进出口公司</a-select-option>
            <a-select-option value="3">王五制造集团</a-select-option>
          </a-select>
        </a-form-item>
        <a-form-item label="期间">
          <a-month-picker v-model:value="reportForm.period" style="width:160px" />
        </a-form-item>
        <a-form-item label="报告类型">
          <a-checkbox-group v-model:value="reportForm.reportTypes">
            <a-checkbox value="risk">风险管理报告</a-checkbox>
            <a-checkbox value="exposure">敞口分析报告</a-checkbox>
            <a-checkbox value="transaction">交易汇总报告</a-checkbox>
            <a-checkbox value="compliance">合规审查报告</a-checkbox>
          </a-checkbox-group>
        </a-form-item>
        <a-form-item>
          <a-button type="primary" :loading="reportGenerating" @click="generateReport">
            一键生成
          </a-button>
        </a-form-item>
      </a-form>

      <div v-if="reportUrl" style="margin-top:16px">
        <a-alert type="success" message="报告生成成功" show-icon>
          <template #action>
            <a-button type="link" @click="downloadReport">下载报告</a-button>
          </template>
        </a-alert>
      </div>
    </a-card>
  </div>
</template>

<script setup lang="ts">
import { ref, reactive } from 'vue'
import { message } from 'ant-design-vue'

const nlCommand = ref('')
const nlAnalyzing = ref(false)
const nlResult = ref<any>(null)

const hedgeForm = reactive({
  bizType: 'import',
  paymentCycle: '60',
  riskAppetite: 'balanced'
})
const hedgeLoading = ref(false)
const hedgeStrategies = ref<any[]>([])

const reportForm = reactive<{
  customerId: string | undefined
  period: any
  reportTypes: string[]
}>({
  customerId: undefined,
  period: null,
  reportTypes: []
})
const reportGenerating = ref(false)
const reportUrl = ref('')

function analyzeNL() {
  if (!nlCommand.value.trim()) {
    message.warning('请输入交易指令')
    return
  }
  nlAnalyzing.value = true
  setTimeout(() => {
    nlAnalyzing.value = false
    nlResult.value = {
      intent: '远期购汇',
      direction: '买入',
      currencyPair: 'USD/CNY',
      amount: '1,000,000 USD',
      tenor: '1个月',
      productType: '远期结售汇'
    }
    message.success('自然语言分析完成')
  }, 1000)
}

function getHedgeRecommendation() {
  hedgeLoading.value = true
  setTimeout(() => {
    hedgeLoading.value = false
    hedgeStrategies.value = [
      { strategy: '远期结汇', ratio: '60%', reason: '锁定核心敞口汇率，规避大部分汇率波动风险' },
      { strategy: '期权保护', ratio: '25%', reason: '保留汇率有利变动收益，同时设定保底汇率' },
      { strategy: '即期交易', ratio: '15%', reason: '保持流动性，应对短期资金需求' }
    ]
    message.success('策略推荐已生成')
  }, 1200)
}

function generateReport() {
  if (!reportForm.customerId || !reportForm.period) {
    message.warning('请选择客户和期间')
    return
  }
  reportGenerating.value = true
  setTimeout(() => {
    reportGenerating.value = false
    reportUrl.value = '/api/reports/ai-report-' + Date.now() + '.pdf'
    message.success('报告生成成功')
  }, 2000)
}

function downloadReport() {
  message.info('开始下载报告')
  window.open(reportUrl.value, '_blank')
}
</script>

<style scoped>
.ai-trading-copilot h2 { margin-bottom: 16px; }
.nl-result { background: #fafafa; padding: 12px; border-radius: 4px; }
</style>
