<template>
  <div class="payment-template-manager">
    <h2>场景模板管理</h2>

    <a-row :gutter="24" style="margin-top: 16px">
      <a-col :span="6">
        <a-card title="场景类型 Scenario Types" size="small">
          <a-menu v-model:selectedKeys="selectedScenario" mode="inline" @click="filterByScenario">
            <a-menu-item key="ALL">
              <template #icon><AppstoreOutlined /></template>
              全部模板 All Templates
            </a-menu-item>
            <a-menu-item key="STUDY_ABROAD">
              <template #icon><ReadOutlined /></template>
              留学汇款 Study Abroad
            </a-menu-item>
            <a-menu-item key="TRAVEL_DEPOSIT">
              <template #icon><CompassOutlined /></template>
              旅游保证金 Travel Deposit
            </a-menu-item>
            <a-menu-item key="MEDICAL_EXPENSE">
              <template #icon><MedicineBoxOutlined /></template>
              境外医疗 Medical Expense
            </a-menu-item>
            <a-menu-item key="CUSTOM">
              <template #icon><FormOutlined /></template>
              自定义 Custom
            </a-menu-item>
          </a-menu>
        </a-card>
      </a-col>

      <a-col :span="18">
        <a-card :title="'模板列表 Template List (' + filteredTemplates.length + ')'">
          <template #extra>
            <a-button type="primary" @click="showCreateModal = true">新建模板 New Template</a-button>
          </template>
          <a-row :gutter="[16, 16]">
            <a-col v-for="item in filteredTemplates" :key="item.id" :span="8">
              <a-card hoverable size="small" class="template-card">
                <div class="card-header">
                  <a-tag :color="scenarioColorMap[item.scenarioType]">
                    {{ scenarioNameMap[item.scenarioType] || item.scenarioType }}
                  </a-tag>
                  <span class="card-currency">{{ item.defaultPayCurrency }}</span>
                </div>
                <h4 class="card-title">{{ item.templateName }}</h4>
                <p class="card-beneficiary">受益人: {{ parseBeneficiary(item) }}</p>
                <p class="card-instructions" style="color: #888; font-size: 12px">
                  {{ truncateText(item.usageInstructions, 60) }}
                </p>
                <div class="card-actions">
                  <a-button type="primary" size="small" block @click="useTemplate(item)">
                    使用此模板 Use Template
                  </a-button>
                  <a-space style="margin-top: 8px">
                    <a size="small" @click="editTemplate(item)">编辑 Edit</a>
                    <a-popconfirm
                      v-if="!item.isPublic"
                      title="确认删除该模板？"
                      @confirm="removeTemplate(item)"
                    >
                      <a size="small" style="color: #ff4d4f">删除 Delete</a>
                    </a-popconfirm>
                  </a-space>
                </div>
              </a-card>
            </a-col>
          </a-row>
          <a-empty v-if="filteredTemplates.length === 0" description="暂无模板 No templates" style="margin: 40px 0" />
        </a-card>
      </a-col>
    </a-row>

    <a-modal v-model:open="showCreateModal" title="新建场景模板 New Scenario Template" @ok="createTemplate" width="640px">
      <a-form layout="vertical">
        <a-row :gutter="16">
          <a-col :span="12">
            <a-form-item label="模板名称 Template Name">
              <a-input v-model:value="newTemplate.templateName" placeholder="模板名称" />
            </a-form-item>
          </a-col>
          <a-col :span="12">
            <a-form-item label="场景类型 Scenario Type">
              <a-select v-model:value="newTemplate.scenarioType">
                <a-select-option value="STUDY_ABROAD">留学汇款 Study Abroad</a-select-option>
                <a-select-option value="TRAVEL_DEPOSIT">旅游保证金 Travel Deposit</a-select-option>
                <a-select-option value="MEDICAL_EXPENSE">境外医疗 Medical Expense</a-select-option>
                <a-select-option value="CUSTOM">自定义 Custom</a-select-option>
              </a-select>
            </a-form-item>
          </a-col>
        </a-row>
        <a-row :gutter="16">
          <a-col :span="12">
            <a-form-item label="支付方向 Direction">
              <a-radio-group v-model:value="newTemplate.paymentDirection">
                <a-radio value="OUTWARD">汇出 Outward</a-radio>
                <a-radio value="INWARD">汇入 Inward</a-radio>
              </a-radio-group>
            </a-form-item>
          </a-col>
          <a-col :span="12">
            <a-form-item label="默认币种 Currency">
              <a-input v-model:value="newTemplate.defaultPayCurrency" placeholder="USD" />
            </a-form-item>
          </a-col>
        </a-row>
        <a-row :gutter="16">
          <a-col :span="12">
            <a-form-item label="收款国别 Country">
              <a-input v-model:value="newTemplate.defaultBeneficiaryCountry" placeholder="US" />
            </a-form-item>
          </a-col>
          <a-col :span="12">
            <a-form-item label="汇款用途 Purpose">
              <a-input v-model:value="newTemplate.defaultPurpose" placeholder="汇款用途" />
            </a-form-item>
          </a-col>
        </a-row>
        <a-form-item label="用途代码 Purpose Code">
          <a-input v-model:value="newTemplate.defaultPurposeCode" placeholder="S0001" />
        </a-form-item>
        <a-form-item label="受益人信息 Beneficiary (JSON)">
          <a-textarea v-model:value="newTemplate.beneficiaryDetails" :rows="3" placeholder='{"name":"","bank":"","swift":""}' />
        </a-form-item>
        <a-form-item label="使用说明 Instructions">
          <a-textarea v-model:value="newTemplate.usageInstructions" :rows="2" placeholder="输入使用说明" />
        </a-form-item>
        <a-form-item label="公开模板 Public">
          <a-switch v-model:checked="newTemplate.isPublic" checked-children="是" un-checked-children="否" />
        </a-form-item>
      </a-form>
    </a-modal>
  </div>
</template>

<script setup lang="ts">
import { ref, reactive, computed } from 'vue'
import { message } from 'ant-design-vue'
import {
  AppstoreOutlined,
  ReadOutlined,
  CompassOutlined,
  MedicineBoxOutlined,
  FormOutlined
} from '@ant-design/icons-vue'
import { useRouter } from 'vue-router'

const router = useRouter()

const selectedScenario = ref<string[]>(['ALL'])

const scenarioColorMap: Record<string, string> = {
  STUDY_ABROAD: 'blue',
  TRAVEL_DEPOSIT: 'orange',
  MEDICAL_EXPENSE: 'red',
  CUSTOM: 'default'
}

const scenarioNameMap: Record<string, string> = {
  STUDY_ABROAD: '留学汇款 Study Abroad',
  TRAVEL_DEPOSIT: '旅游保证金 Travel Deposit',
  MEDICAL_EXPENSE: '境外医疗 Medical',
  CUSTOM: '自定义 Custom'
}

const templates = ref([
  {
    id: 1,
    templateCode: 'TPL_STUDY_001',
    templateName: '美国哈佛大学学费 Harvard Tuition',
    scenarioType: 'STUDY_ABROAD',
    paymentDirection: 'OUTWARD',
    defaultPayCurrency: 'USD',
    defaultBeneficiaryCountry: 'US',
    beneficiaryDetails: '{"name":"Harvard University","bank":"Bank of America","swift":"BOFAUS3N"}',
    defaultPurpose: 'OVERSEAS STUDY TUITION PAYMENT',
    defaultPurposeCode: 'S0001',
    usageInstructions: '适用于美国大学留学学费汇款。请确保汇款账户与录取通知书一致。学费Payment需提供I-20表格。',
    sortOrder: 1,
    isPublic: true
  },
  {
    id: 2,
    templateCode: 'TPL_TRAVEL_001',
    templateName: '泰国旅游保证金 Thailand Travel Deposit',
    scenarioType: 'TRAVEL_DEPOSIT',
    paymentDirection: 'OUTWARD',
    defaultPayCurrency: 'CNY',
    defaultBeneficiaryCountry: 'TH',
    beneficiaryDetails: '{"name":"Tourism Authority of Thailand","bank":"Bangkok Bank","swift":"BKKBTHBK"}',
    defaultPurpose: 'TRAVEL SECURITY DEPOSIT',
    defaultPurposeCode: 'T0001',
    usageInstructions: '适用于出境旅游保证金缴纳。请提供旅游合同编号及出行日期。回国后凭护照及登机牌退还保证金。',
    sortOrder: 2,
    isPublic: true
  },
  {
    id: 3,
    templateCode: 'TPL_MEDICAL_001',
    templateName: '日本东京医疗费用 Tokyo Medical',
    scenarioType: 'MEDICAL_EXPENSE',
    paymentDirection: 'OUTWARD',
    defaultPayCurrency: 'JPY',
    defaultBeneficiaryCountry: 'JP',
    beneficiaryDetails: '{"name":"Tokyo Medical University Hospital","bank":"MUFG Bank","swift":"BOTKJPJT"}',
    defaultPurpose: 'OVERSEAS MEDICAL TREATMENT EXPENSE',
    defaultPurposeCode: 'M0001',
    usageInstructions: '适用于境外就医费用支付。请提供医院出具的诊断证明及费用清单。单笔限额等值5万美元。',
    sortOrder: 3,
    isPublic: true
  },
  {
    id: 4,
    templateCode: 'TPL_STUDY_002',
    templateName: '英国剑桥大学学费 Cambridge Tuition',
    scenarioType: 'STUDY_ABROAD',
    paymentDirection: 'OUTWARD',
    defaultPayCurrency: 'GBP',
    defaultBeneficiaryCountry: 'GB',
    beneficiaryDetails: '{"name":"University of Cambridge","bank":"Barclays Bank","swift":"BARCGB22"}',
    defaultPurpose: 'OVERSEAS STUDY TUITION PAYMENT',
    defaultPurposeCode: 'S0002',
    usageInstructions: '适用于英国大学学费汇款。请提供CAS编号及录取通知书。',
    sortOrder: 4,
    isPublic: true
  },
  {
    id: 5,
    templateCode: 'TPL_CUSTOM_001',
    templateName: '个人赡家款 Family Support',
    scenarioType: 'CUSTOM',
    paymentDirection: 'OUTWARD',
    defaultPayCurrency: 'AUD',
    defaultBeneficiaryCountry: 'AU',
    beneficiaryDetails: '{"name":"","bank":"Commonwealth Bank","swift":""}',
    defaultPurpose: 'FAMILY SUPPORT REMITTANCE',
    defaultPurposeCode: 'C0001',
    usageInstructions: '用于向境外亲属汇出赡家款。需提供亲属关系证明。年度限额等值5万美元。',
    sortOrder: 5,
    isPublic: false,
    ownerCustomerId: 1001
  }
])

const filteredTemplates = computed(() => {
  if (selectedScenario.value[0] === 'ALL') return templates.value
  return templates.value.filter(t => t.scenarioType === selectedScenario.value[0])
})

function filterByScenario(info: any) {
  selectedScenario.value = [info.key]
}

function parseBeneficiary(item: any): string {
  try {
    const details = JSON.parse(item.beneficiaryDetails)
    return details.name || 'N/A'
  } catch {
    return item.beneficiaryDetails || 'N/A'
  }
}

function truncateText(text: string, maxLen: number): string {
  if (!text) return ''
  return text.length > maxLen ? text.substring(0, maxLen) + '...' : text
}

const showCreateModal = ref(false)
const newTemplate = reactive({
  templateName: '',
  scenarioType: 'STUDY_ABROAD',
  paymentDirection: 'OUTWARD',
  defaultPayCurrency: 'USD',
  defaultBeneficiaryCountry: '',
  beneficiaryDetails: '{}',
  defaultPurpose: '',
  defaultPurposeCode: '',
  usageInstructions: '',
  isPublic: false
})

function createTemplate() {
  if (!newTemplate.templateName) {
    message.warning('请填写模板名称 Fill template name')
    return
  }
  templates.value.push({
    id: templates.value.length + 1,
    templateCode: 'TPL_' + Date.now(),
    templateName: newTemplate.templateName,
    scenarioType: newTemplate.scenarioType,
    paymentDirection: newTemplate.paymentDirection,
    defaultPayCurrency: newTemplate.defaultPayCurrency,
    defaultBeneficiaryCountry: newTemplate.defaultBeneficiaryCountry,
    beneficiaryDetails: newTemplate.beneficiaryDetails,
    defaultPurpose: newTemplate.defaultPurpose,
    defaultPurposeCode: newTemplate.defaultPurposeCode,
    usageInstructions: newTemplate.usageInstructions,
    sortOrder: templates.value.length + 1,
    isPublic: newTemplate.isPublic
  })
  showCreateModal.value = false
  message.success('模板已创建 Template created')
  // Reset 重置
  newTemplate.templateName = ''
  newTemplate.scenarioType = 'STUDY_ABROAD'
  newTemplate.usageInstructions = ''
  newTemplate.beneficiaryDetails = '{}'
  newTemplate.defaultPurpose = ''
  newTemplate.defaultPurposeCode = ''
  newTemplate.isPublic = false
}

function useTemplate(item: any) {
  message.success(`已选择模板: ${item.templateName}`)
  router.push({
    path: '/payment',
    query: {
      templateCode: item.templateCode,
      currency: item.defaultPayCurrency,
      purpose: item.defaultPurpose,
      country: item.defaultBeneficiaryCountry,
      beneficiary: item.beneficiaryDetails
    }
  })
}

function editTemplate(item: any) {
  message.info('编辑模板: ' + item.templateName)
}

function removeTemplate(item: any) {
  templates.value = templates.value.filter(t => t.id !== item.id)
  message.success('模板已删除 Template deleted')
}
</script>

<style scoped>
.payment-template-manager h2 { margin-bottom: 16px; }
.template-card { transition: box-shadow 0.3s; }
.template-card:hover { box-shadow: 0 2px 8px rgba(0,0,0,0.15); }
.card-header { display: flex; justify-content: space-between; align-items: center; margin-bottom: 8px; }
.card-currency { font-weight: bold; color: #1890ff; }
.card-title { margin: 4px 0; font-size: 14px; }
.card-beneficiary { color: #555; font-size: 12px; margin-bottom: 4px; }
.card-actions { margin-top: 12px; }
</style>
