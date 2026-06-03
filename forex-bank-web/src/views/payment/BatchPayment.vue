<template>
  <div class="batch-payment">
    <h2>批量汇款</h2>

    <a-card title="上传文件" style="margin-bottom: 16px">
      <a-upload-dragger
        v-model:fileList="fileList"
        name="file"
        :multiple="false"
        accept=".xlsx,.csv"
        :before-upload="handleBeforeUpload"
        @remove="handleRemove"
      >
        <p class="ant-upload-drag-icon">
          <inbox-outlined />
        </p>
        <p class="ant-upload-text">点击或拖拽文件到此区域上传</p>
        <p class="ant-upload-hint">支持 Excel (.xlsx) 或 CSV 格式，单次最多 500 笔</p>
      </a-upload-dragger>
    </a-card>

    <a-card v-if="parsedData.length > 0" title="数据预览" style="margin-bottom: 16px">
      <template #extra>
        <a-space>
          <span>共 {{ parsedData.length }} 笔</span>
          <a-tag color="green">{{ validCount }} 笔通过</a-tag>
          <a-tag color="red">{{ errorCount }} 笔异常</a-tag>
        </a-space>
      </template>
      <a-table :columns="previewColumns" :data-source="parsedData" :pagination="{ pageSize: 10 }" row-key="index" size="small">
        <template #bodyCell="{ column, record }">
          <template v-if="column.key === 'valid'">
            <check-circle-outlined v-if="record.valid" style="color: #52c41a; font-size: 18px" />
            <close-circle-outlined v-else style="color: #f5222d; font-size: 18px" />
          </template>
        </template>
      </a-table>
    </a-card>

    <a-card v-if="parsedData.length > 0" title="通道分配" style="margin-bottom: 16px">
      <a-form layout="inline">
        <a-form-item label="分配通道">
          <a-radio-group v-model:value="assignedChannel">
            <a-radio-button value="SWIFT">SWIFT</a-radio-button>
            <a-radio-button value="CIPS">CIPS</a-radio-button>
            <a-radio-button value="CFXPS">CFXPS</a-radio-button>
            <a-radio-button value="GFIX">GFIX</a-radio-button>
          </a-radio-group>
        </a-form-item>
        <a-form-item>
          <a-button type="primary" @click="handleAssignChannel">分配通道</a-button>
        </a-form-item>
      </a-form>
    </a-card>

    <a-card v-if="parsedData.length > 0">
      <div style="text-align: center">
        <a-button type="primary" size="large" :loading="submitting" @click="handleBatchSubmit">
          提交批量汇款
        </a-button>
        <a-progress v-if="submitting" :percent="submitProgress" style="margin-top: 16px" />
        <div v-if="submitResult" style="margin-top: 16px">
          <a-alert
            :type="submitResult.failures === 0 ? 'success' : 'warning'"
            :message="`提交完成：成功 ${submitResult.successes} 笔，失败 ${submitResult.failures} 笔`"
            show-icon
          />
        </div>
      </div>
    </a-card>
  </div>
</template>

<script setup lang="ts">
import { ref, reactive, computed } from 'vue'
import { message } from 'ant-design-vue'

interface ParsedPayment {
  index: number
  customerId: string
  paymentType: string
  payAmount: number
  payCurrency: string
  beneficiaryName: string
  beneficiaryAccount: string
  beneficiaryBank: string
  beneficiarySwift: string
  beneficiaryCountry: string
  receivingBankCode: string
  paymentPurpose: string
  chargeBearer: string
  valid: boolean
  errorMsg?: string
}

const fileList = ref<any[]>([])
const parsedData = ref<ParsedPayment[]>([])
const assignedChannel = ref('SWIFT')
const submitting = ref(false)
const submitProgress = ref(0)
const submitResult = ref<{ successes: number; failures: number } | null>(null)
const channelAssigned = ref(false)

const validCount = computed(() => parsedData.value.filter(d => d.valid).length)
const errorCount = computed(() => parsedData.value.filter(d => !d.valid).length)

const previewColumns = [
  { title: '序号', dataIndex: 'index', key: 'index', width: 60 },
  { title: '验证', dataIndex: 'valid', key: 'valid', width: 60 },
  { title: '客户ID', dataIndex: 'customerId', key: 'customerId' },
  { title: '支付类型', dataIndex: 'paymentType', key: 'paymentType' },
  { title: '金额', dataIndex: 'payAmount', key: 'payAmount' },
  { title: '币种', dataIndex: 'payCurrency', key: 'payCurrency' },
  { title: '收款人', dataIndex: 'beneficiaryName', key: 'beneficiaryName' },
  { title: '收款银行', dataIndex: 'beneficiaryBank', key: 'beneficiaryBank' },
  { title: 'SWIFT', dataIndex: 'beneficiarySwift', key: 'beneficiarySwift' },
  { title: '用途', dataIndex: 'paymentPurpose', key: 'paymentPurpose' },
  { title: '异常信息', dataIndex: 'errorMsg', key: 'errorMsg' }
]

function handleBeforeUpload(file: File) {
  fileList.value = [file]
  parseFile(file)
  return false
}

function handleRemove() {
  fileList.value = []
  parsedData.value = []
  submitResult.value = null
  channelAssigned.value = false
}

function parseFile(file: File) {
  const mockParsedData: ParsedPayment[] = [
    { index: 1, customerId: '1001', paymentType: 'SWIFT', payAmount: 50000, payCurrency: 'USD', beneficiaryName: 'ABC Corp', beneficiaryAccount: 'US1234567890', beneficiaryBank: 'Bank of America', beneficiarySwift: 'BOFAUS3NXXX', beneficiaryCountry: 'US', receivingBankCode: 'BKCHCNBJ', paymentPurpose: '货款支付', chargeBearer: 'OUR', valid: true },
    { index: 2, customerId: '1002', paymentType: 'SWIFT', payAmount: 120000, payCurrency: 'EUR', beneficiaryName: 'XYZ Ltd', beneficiaryAccount: 'DE89370400440532013000', beneficiaryBank: 'Deutsche Bank', beneficiarySwift: 'DEUTDEFFXXX', beneficiaryCountry: 'DE', receivingBankCode: 'BKCHCNBJ', paymentPurpose: '服务费', chargeBearer: 'SHA', valid: true },
    { index: 3, customerId: '1003', paymentType: 'CIPS', payAmount: 750000, payCurrency: 'CNY', beneficiaryName: 'Shanghai Trading', beneficiaryAccount: 'CN65000123456789', beneficiaryBank: 'ICBC', beneficiarySwift: 'ICBKCNBJ', beneficiaryCountry: 'CN', receivingBankCode: 'ICBKCNBJ', paymentPurpose: '投资款', chargeBearer: 'OUR', valid: true },
    { index: 4, customerId: '1004', paymentType: 'SWIFT', payAmount: 200000, payCurrency: 'USD', beneficiaryName: 'Invalid Co', beneficiaryAccount: '', beneficiaryBank: '', beneficiarySwift: 'INVALID', beneficiaryCountry: '', receivingBankCode: '', paymentPurpose: '', chargeBearer: '', valid: false, errorMsg: '收款人信息不完整' }
  ]
  parsedData.value = mockParsedData
  message.success(`成功解析 ${mockParsedData.length} 条记录`)
}

function handleAssignChannel() {
  channelAssigned.value = true
  message.success(`已将所有支付分配到 ${assignedChannel.value} 通道`)
}

async function handleBatchSubmit() {
  if (!channelAssigned.value) {
    message.warning('请先分配通道')
    return
  }
  submitting.value = true
  submitProgress.value = 0
  const timer = setInterval(() => {
    submitProgress.value = Math.min(submitProgress.value + 10, 90)
  }, 300)

  setTimeout(() => {
    clearInterval(timer)
    submitProgress.value = 100
    submitting.value = false
    submitResult.value = { successes: validCount.value, failures: errorCount.value }
    message.success('批量提交完成')
  }, 2000)
}
</script>

<style scoped>
.batch-payment h2 { margin-bottom: 16px; }
</style>
