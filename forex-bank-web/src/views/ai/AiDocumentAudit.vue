<template>
  <div class="ai-document-audit">
    <h2>AI智能审单</h2>

    <a-row :gutter="16" style="margin-bottom:16px">
      <a-col :span="8">
        <a-card title="发票" size="small">
          <a-upload-dragger
            v-model:fileList="invoiceFileList"
            :before-upload="() => false"
            :max-count="1"
            @change="handleInvoiceUpload"
          >
            <p class="ant-upload-drag-icon"><inbox-outlined /></p>
            <p class="ant-upload-text">点击或拖拽发票到此区域上传</p>
          </a-upload-dragger>
          <a-textarea
            v-if="invoiceOcrText"
            v-model:value="invoiceOcrText"
            :rows="6"
            placeholder="OCR识别结果..."
            style="margin-top:8px"
          />
        </a-card>
      </a-col>
      <a-col :span="8">
        <a-card title="提单" size="small">
          <a-upload-dragger
            v-model:fileList="blFileList"
            :before-upload="() => false"
            :max-count="1"
            @change="handleBlUpload"
          >
            <p class="ant-upload-drag-icon"><inbox-outlined /></p>
            <p class="ant-upload-text">点击或拖拽提单到此区域上传</p>
          </a-upload-dragger>
          <a-textarea
            v-if="blOcrText"
            v-model:value="blOcrText"
            :rows="6"
            placeholder="OCR识别结果..."
            style="margin-top:8px"
          />
        </a-card>
      </a-col>
      <a-col :span="8">
        <a-card title="报关单" size="small">
          <a-upload-dragger
            v-model:fileList="customsFileList"
            :before-upload="() => false"
            :max-count="1"
            @change="handleCustomsUpload"
          >
            <p class="ant-upload-drag-icon"><inbox-outlined /></p>
            <p class="ant-upload-text">点击或拖拽报关单到此区域上传</p>
          </a-upload-dragger>
          <a-textarea
            v-if="customsOcrText"
            v-model:value="customsOcrText"
            :rows="6"
            placeholder="OCR识别结果..."
            style="margin-top:8px"
          />
        </a-card>
      </a-col>
    </a-row>

    <div style="text-align:center;margin-bottom:16px">
      <a-button type="primary" size="large" :loading="comparing" @click="doCompare">
        三单比对
      </a-button>
    </div>

    <a-card v-if="compareResult" title="比对结果">
      <a-descriptions :column="1" bordered size="small" style="margin-bottom:16px">
        <a-descriptions-item label="金额一致性">
          <span v-if="compareResult.amountMatch" style="color:#52c41a">
            <check-circle-outlined /> 一致
          </span>
          <span v-else style="color:#f5222d">
            <close-circle-outlined /> 不一致
          </span>
        </a-descriptions-item>
        <a-descriptions-item label="日期一致性">
          <span v-if="compareResult.dateMatch" style="color:#52c41a">
            <check-circle-outlined /> 一致
          </span>
          <span v-else style="color:#f5222d">
            <close-circle-outlined /> 不一致
          </span>
        </a-descriptions-item>
        <a-descriptions-item label="商品描述匹配度">
          <a-progress
            :percent="compareResult.descriptionMatch"
            :stroke-color="compareResult.descriptionMatch >= 80 ? '#52c41a' : compareResult.descriptionMatch >= 60 ? '#fa8c16' : '#f5222d'"
          />
        </a-descriptions-item>
      </a-descriptions>

      <a-form-item label="审核意见">
        <a-textarea
          v-model:value="compareResult.auditOpinion"
          :rows="3"
          placeholder="审核意见..."
        />
      </a-form-item>

      <div style="margin-top:8px">
        <a-tag :color="overallTagColor">
          {{ compareResult.overallResult }}
        </a-tag>
      </div>
    </a-card>
  </div>
</template>

<script setup lang="ts">
import { ref, computed, h } from 'vue'
import { message } from 'ant-design-vue'
import { InboxOutlined, CheckCircleOutlined, CloseCircleOutlined } from '@ant-design/icons-vue'

const invoiceFileList = ref<any[]>([])
const blFileList = ref<any[]>([])
const customsFileList = ref<any[]>([])

const invoiceOcrText = ref('')
const blOcrText = ref('')
const customsOcrText = ref('')

const comparing = ref(false)
const compareResult = ref<any>(null)

const overallTagColor = computed(() => {
  if (!compareResult.value) return ''
  const r = compareResult.value.overallResult
  if (r === '通过') return 'green'
  if (r === '存疑') return 'orange'
  return 'red'
})

function handleInvoiceUpload(info: any) {
  if (info.fileList?.length) {
    simulateOcr('invoice')
  }
}

function handleBlUpload(info: any) {
  if (info.fileList?.length) {
    simulateOcr('bl')
  }
}

function handleCustomsUpload(info: any) {
  if (info.fileList?.length) {
    simulateOcr('customs')
  }
}

function simulateOcr(type: string) {
  message.loading({ content: 'OCR识别中...', key: 'ocr', duration: 0 })
  setTimeout(() => {
    message.success({ content: `${type} OCR识别完成`, key: 'ocr' })
    if (type === 'invoice') {
      invoiceOcrText.value = '发票号: INV-2024-001234\n金额: USD 150,000.00\n日期: 2024-06-01\n商品: 电子元器件/集成电路'
    } else if (type === 'bl') {
      blOcrText.value = '提单号: BL-SHA2406001\n发货日期: 2024-06-01\n商品: 电子元器件\n重量: 500 KG'
    } else if (type === 'customs') {
      customsOcrText.value = '报关单号: CUS-20240601-0088\n申报金额: USD 150,000.00\n申报日期: 2024-06-01\n商品: 集成电路'
    }
  }, 1500)
}

function doCompare() {
  if (!invoiceOcrText.value || !blOcrText.value || !customsOcrText.value) {
    message.warning('请先上传三份单据并等待OCR识别完成')
    return
  }
  comparing.value = true
  setTimeout(() => {
    comparing.value = false
    compareResult.value = {
      amountMatch: true,
      dateMatch: true,
      descriptionMatch: 88,
      auditOpinion: '三单信息基本一致，金额、日期吻合，商品描述匹配度较高。建议通过审核。',
      overallResult: '通过'
    }
    message.success('三单比对完成')
  }, 1500)
}
</script>

<style scoped>
.ai-document-audit h2 { margin-bottom: 16px; }
</style>
