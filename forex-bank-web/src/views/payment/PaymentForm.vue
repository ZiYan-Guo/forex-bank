<template>
  <div class="payment-page">
    <h2>跨境支付</h2>

    <a-tabs v-model:activeKey="activeTab">
      <a-tab-pane key="outward" tab="汇出汇款">
        <a-steps :current="step" style="margin-bottom:24px">
          <a-step title="汇款人信息" />
          <a-step title="收款人信息" />
          <a-step title="款项信息" />
          <a-step title="确认" />
        </a-steps>

        <!-- Step 1: 汇款人信息 -->
        <a-form v-if="step === 0" :model="outwardForm" layout="vertical" style="max-width:600px">
          <a-form-item label="汇款人名称" required>
            <a-input v-model:value="outwardForm.senderName" placeholder="请输入汇款人名称" />
          </a-form-item>
          <a-form-item label="汇款人账号" required>
            <a-input v-model:value="outwardForm.senderAccount" placeholder="请输入汇款人账号" />
          </a-form-item>
          <a-form-item label="汇款人地址">
            <a-input v-model:value="outwardForm.senderAddress" placeholder="请输入汇款人地址" />
          </a-form-item>
        </a-form>

        <!-- Step 2: 收款人信息 -->
        <a-form v-if="step === 1" :model="outwardForm" layout="vertical" style="max-width:600px">
          <a-form-item label="收款人名称" required>
            <a-input v-model:value="outwardForm.receiverName" placeholder="请输入收款人名称" />
          </a-form-item>
          <a-form-item label="收款人账号" required>
            <a-input v-model:value="outwardForm.receiverAccount" placeholder="请输入收款人账号" />
          </a-form-item>
          <a-form-item label="收款银行">
            <a-input v-model:value="outwardForm.receiverBank" placeholder="请输入收款银行" />
          </a-form-item>
          <a-form-item label="收款行SWIFT" required>
            <a-input v-model:value="outwardForm.swiftCode" placeholder="8或11位" />
          </a-form-item>
          <a-form-item label="收款行国家" required>
            <a-select v-model:value="outwardForm.receiverCountry" placeholder="请选择国家">
              <a-select-option value="US">美国</a-select-option>
              <a-select-option value="GB">英国</a-select-option>
              <a-select-option value="JP">日本</a-select-option>
              <a-select-option value="HK">香港</a-select-option>
              <a-select-option value="SG">新加坡</a-select-option>
              <a-select-option value="DE">德国</a-select-option>
              <a-select-option value="AU">澳大利亚</a-select-option>
            </a-select>
          </a-form-item>
        </a-form>

        <!-- Step 3: 款项信息 -->
        <a-form v-if="step === 2" :model="outwardForm" layout="vertical" style="max-width:600px">
          <a-form-item label="汇款币种" required>
            <a-select v-model:value="outwardForm.currency" placeholder="请选择币种">
              <a-select-option value="USD">USD 美元</a-select-option>
              <a-select-option value="EUR">EUR 欧元</a-select-option>
              <a-select-option value="GBP">GBP 英镑</a-select-option>
              <a-select-option value="JPY">JPY 日元</a-select-option>
              <a-select-option value="CNY">CNY 人民币</a-select-option>
              <a-select-option value="HKD">HKD 港币</a-select-option>
              <a-select-option value="AUD">AUD 澳元</a-select-option>
            </a-select>
          </a-form-item>
          <a-form-item label="汇款金额" required>
            <a-input-number v-model:value="outwardForm.amount" :min="0" :precision="2" style="width:100%" placeholder="请输入金额" />
          </a-form-item>
          <a-form-item label="汇款用途" required>
            <a-textarea v-model:value="outwardForm.purpose" :rows="3" placeholder="请输入汇款用途" />
          </a-form-item>
          <a-form-item label="费用承担" required>
            <a-select v-model:value="outwardForm.chargeBearer" placeholder="请选择费用承担方式">
              <a-select-option value="OUR">OUR 汇款人承担</a-select-option>
              <a-select-option value="BEN">BEN 收款人承担</a-select-option>
              <a-select-option value="SHA">SHA 共同承担</a-select-option>
            </a-select>
          </a-form-item>
          <a-form-item label="起息日">
            <a-date-picker v-model:value="outwardForm.valueDate" style="width:100%" placeholder="请选择起息日" />
          </a-form-item>
        </a-form>

        <!-- Step 4: 确认 -->
        <div v-if="step === 3" class="summary-card">
          <a-descriptions title="汇款信息确认" :column="2" bordered size="small">
            <a-descriptions-item label="汇款人名称">{{ outwardForm.senderName }}</a-descriptions-item>
            <a-descriptions-item label="汇款人账号">{{ outwardForm.senderAccount }}</a-descriptions-item>
            <a-descriptions-item label="汇款人地址">{{ outwardForm.senderAddress || '-' }}</a-descriptions-item>
            <a-descriptions-item label="收款人名称">{{ outwardForm.receiverName }}</a-descriptions-item>
            <a-descriptions-item label="收款人账号">{{ outwardForm.receiverAccount }}</a-descriptions-item>
            <a-descriptions-item label="收款银行">{{ outwardForm.receiverBank || '-' }}</a-descriptions-item>
            <a-descriptions-item label="SWIFT代码">{{ outwardForm.swiftCode }}</a-descriptions-item>
            <a-descriptions-item label="收款行国家">{{ outwardForm.receiverCountry }}</a-descriptions-item>
            <a-descriptions-item label="汇款币种">{{ outwardForm.currency }}</a-descriptions-item>
            <a-descriptions-item label="汇款金额">{{ outwardForm.amount }}</a-descriptions-item>
            <a-descriptions-item label="汇款用途">{{ outwardForm.purpose }}</a-descriptions-item>
            <a-descriptions-item label="费用承担">{{ outwardForm.chargeBearer }}</a-descriptions-item>
            <a-descriptions-item label="起息日">{{ outwardForm.valueDate || '-' }}</a-descriptions-item>
          </a-descriptions>
        </div>

        <div style="margin-top:24px">
          <a-button v-if="step > 0" style="margin-right:8px" @click="step--">上一步</a-button>
          <a-button v-if="step < 3" type="primary" @click="step++">下一步</a-button>
          <a-button v-if="step === 3" type="primary" :loading="submitting" @click="handleOutwardSubmit">提交汇款</a-button>
        </div>
      </a-tab-pane>

      <a-tab-pane key="inward" tab="汇入汇款">
        <a-form :model="inwardForm" layout="vertical" style="max-width:600px">
          <a-form-item label="业务参考号" required>
            <a-input v-model:value="inwardForm.refNo" placeholder="请输入业务参考号" />
          </a-form-item>
          <a-form-item label="汇款人" required>
            <a-input v-model:value="inwardForm.sender" placeholder="请输入汇款人" />
          </a-form-item>
          <a-form-item label="金额" required>
            <a-input-number v-model:value="inwardForm.amount" :min="0" :precision="2" style="width:100%" placeholder="请输入金额" />
          </a-form-item>
          <a-form-item label="币种" required>
            <a-select v-model:value="inwardForm.currency" placeholder="请选择币种">
              <a-select-option value="USD">USD 美元</a-select-option>
              <a-select-option value="EUR">EUR 欧元</a-select-option>
              <a-select-option value="GBP">GBP 英镑</a-select-option>
              <a-select-option value="JPY">JPY 日元</a-select-option>
              <a-select-option value="CNY">CNY 人民币</a-select-option>
              <a-select-option value="HKD">HKD 港币</a-select-option>
            </a-select>
          </a-form-item>
          <a-form-item label="收款账号" required>
            <a-input v-model:value="inwardForm.receiverAccount" placeholder="请输入收款账号" />
          </a-form-item>
          <a-form-item>
            <a-button type="primary" :loading="submitting" @click="handleInwardSubmit">提交汇入</a-button>
          </a-form-item>
        </a-form>
      </a-tab-pane>
    </a-tabs>

    <!-- 汇款记录 -->
    <a-card title="汇款记录" style="margin-top:24px">
      <a-table
        :columns="paymentColumns"
        :data-source="paymentList"
        :loading="loading"
        :pagination="{ current: pagination.pageNum, pageSize: pagination.pageSize, total: pagination.total, showSizeChanger: true, showQuickJumper: true, onChange: onPageChange }"
        row-key="paymentNo"
      >
        <template #bodyCell="{ column, record }">
          <template v-if="column.key === 'direction'">
            <a-tag :color="record.direction === 'OUTWARD' ? '#1677ff' : '#52c41a'">
              {{ record.direction === 'OUTWARD' ? '汇出' : '汇入' }}
            </a-tag>
          </template>
          <template v-if="column.key === 'paymentStatus'">
            <a-tag :color="OrderStatusMap[record.paymentStatus]?.color || '#8c8c8c'">
              {{ OrderStatusMap[record.paymentStatus]?.label || record.paymentStatus }}
            </a-tag>
          </template>
          <template v-if="column.key === 'action'">
            <a-space>
              <a @click="showDetail(record)">查看</a>
              <a v-if="record.paymentStatus === 'PENDING'" @click="submitPayment(record)">提交</a>
              <a v-if="record.paymentStatus === 'PROCESSING'" @click="sendPayment(record)">发送</a>
              <a v-if="record.paymentStatus === 'PENDING' || record.paymentStatus === 'PROCESSING'" style="color:#f5222d" @click="cancelPayment(record)">取消</a>
            </a-space>
          </template>
        </template>
      </a-table>
    </a-card>

    <!-- 详情弹窗 -->
    <a-modal v-model:open="detailVisible" title="汇款详情" :footer="null" width="700px">
      <template v-if="detailRecord">
        <a-descriptions :column="2" bordered size="small">
          <a-descriptions-item label="汇款编号">{{ detailRecord.paymentNo }}</a-descriptions-item>
          <a-descriptions-item label="方向">
            <a-tag :color="detailRecord.direction === 'OUTWARD' ? '#1677ff' : '#52c41a'">
              {{ detailRecord.direction === 'OUTWARD' ? '汇出' : '汇入' }}
            </a-tag>
          </a-descriptions-item>
          <a-descriptions-item label="金额">{{ detailRecord.amount }}</a-descriptions-item>
          <a-descriptions-item label="币种">{{ detailRecord.currency }}</a-descriptions-item>
          <a-descriptions-item label="状态">
            <a-tag :color="OrderStatusMap[detailRecord.paymentStatus]?.color || '#8c8c8c'">
              {{ OrderStatusMap[detailRecord.paymentStatus]?.label || detailRecord.paymentStatus }}
            </a-tag>
          </a-descriptions-item>
          <a-descriptions-item label="时间">{{ detailRecord.createTime }}</a-descriptions-item>
        </a-descriptions>
        <a-divider>GPI 追踪</a-divider>
        <a-timeline>
          <a-timeline-item color="green">付款行处理 — 汇款申请已受理</a-timeline-item>
          <a-timeline-item :color="gpiStep >= 2 ? 'green' : 'grey'">中间行中转 — 款项已转发至代理行</a-timeline-item>
          <a-timeline-item :color="gpiStep >= 3 ? 'green' : 'grey'">收款行已到账 — 资金已到达收款账户</a-timeline-item>
        </a-timeline>
      </template>
    </a-modal>
  </div>
</template>

<script setup lang="ts">
import { ref, reactive, onMounted } from 'vue'
import { message } from 'ant-design-vue'
import { paymentApi } from '@/api/business'
import { OrderStatusMap } from '@/types/api'

const activeTab = ref('outward')
const step = ref(0)
const submitting = ref(false)
const loading = ref(false)

const outwardForm = reactive({
  senderName: '',
  senderAccount: '',
  senderAddress: '',
  receiverName: '',
  receiverAccount: '',
  receiverBank: '',
  swiftCode: '',
  receiverCountry: undefined as string | undefined,
  currency: undefined as string | undefined,
  amount: null as number | null,
  purpose: '',
  chargeBearer: undefined as string | undefined,
  valueDate: undefined as string | undefined
})

const inwardForm = reactive({
  refNo: '',
  sender: '',
  amount: null as number | null,
  currency: undefined as string | undefined,
  receiverAccount: ''
})

const detailVisible = ref(false)
const detailRecord = ref<any>(null)
const gpiStep = ref(1)

const paymentList = ref<any[]>([])
const pagination = reactive({ pageNum: 1, pageSize: 10, total: 0 })

const paymentColumns = [
  { title: '汇款编号', dataIndex: 'paymentNo', key: 'paymentNo' },
  { title: '方向', dataIndex: 'direction', key: 'direction' },
  { title: '金额', dataIndex: 'amount', key: 'amount' },
  { title: '币种', dataIndex: 'currency', key: 'currency' },
  { title: '状态', dataIndex: 'paymentStatus', key: 'paymentStatus' },
  { title: '时间', dataIndex: 'createTime', key: 'createTime' },
  { title: '操作', key: 'action', width: 200 }
]

async function fetchPayments() {
  loading.value = true
  try {
    const res = await paymentApi.pageQuery({ pageNum: pagination.pageNum, pageSize: pagination.pageSize })
    if (res.data?.code === 200) {
      paymentList.value = res.data.data?.records || []
      pagination.total = res.data.data?.total || 0
    }
  } finally {
    loading.value = false
  }
}

function onPageChange(page: number, size: number) {
  pagination.pageNum = page
  pagination.pageSize = size
  fetchPayments()
}

async function handleOutwardSubmit() {
  submitting.value = true
  try {
    const res = await paymentApi.createOutward(outwardForm)
    if (res.data?.code === 200) {
      message.success('汇款申请提交成功')
      step.value = 0
      fetchPayments()
    } else {
      message.error(res.data?.message || '提交失败')
    }
  } finally {
    submitting.value = false
  }
}

async function handleInwardSubmit() {
  submitting.value = true
  try {
    const res = await paymentApi.createInward(inwardForm)
    if (res.data?.code === 200) {
      message.success('汇入汇款创建成功')
      fetchPayments()
    } else {
      message.error(res.data?.message || '创建失败')
    }
  } finally {
    submitting.value = false
  }
}

function showDetail(record: any) {
  detailRecord.value = record
  detailVisible.value = true
  gpiStep.value = record.paymentStatus === 'SUCCESS' ? 3 : record.paymentStatus === 'PROCESSING' ? 2 : 1
}

async function submitPayment(record: any) {
  const res = await paymentApi.submit(record.paymentNo)
  if (res.data?.code === 200) {
    message.success('已提交')
    fetchPayments()
  }
}

async function sendPayment(record: any) {
  const res = await paymentApi.send({ paymentNo: record.paymentNo })
  if (res.data?.code === 200) {
    message.success('已发送')
    fetchPayments()
  }
}

async function cancelPayment(record: any) {
  const res = await paymentApi.cancel(record.paymentNo, '用户取消')
  if (res.data?.code === 200) {
    message.success('已取消')
    fetchPayments()
  }
}

onMounted(() => {
  fetchPayments()
})
</script>

<style scoped>
.payment-page { background: #fff; padding: 24px; border-radius: 8px; }
.summary-card { max-width: 800px; }
</style>
