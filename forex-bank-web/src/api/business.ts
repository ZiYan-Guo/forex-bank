import request from './request'
import type { ApiResponse, PageResp } from '@/types/api'

export const tradingApi = {
  createSpot: (data: any) => request.post<ApiResponse>('/trading/spot', data),
  createForward: (data: any) => request.post<ApiResponse>('/trading/forward', data),
  createSwap: (data: any) => request.post<ApiResponse>('/trading/swap', data),
  createOption: (data: any) => request.post<ApiResponse>('/trading/option', data),
  getDetail: (tradeNo: string) => request.get<ApiResponse>(`/trading/${tradeNo}`),
  pageQuery: (params: any) => request.post<ApiResponse<PageResp<any>>>('/trading/page', params),
  confirm: (tradeNo: string) => request.post<ApiResponse>(`/trading/confirm/${tradeNo}`),
  execute: (tradeNo: string) => request.post<ApiResponse>(`/trading/execute/${tradeNo}`),
  settle: (tradeNo: string) => request.post<ApiResponse>(`/trading/settle/${tradeNo}`),
  rollOver: (data: any) => request.post<ApiResponse>('/trading/roll-over', data),
  closeOut: (tradeNo: string) => request.post<ApiResponse>(`/trading/close/${tradeNo}`),
  cancel: (tradeNo: string, reason: string) =>
    request.post<ApiResponse>(`/trading/cancel/${tradeNo}?reason=${reason}`)
}

export const paymentApi = {
  createOutward: (data: any) => request.post<ApiResponse>('/payment/outward', data),
  createInward: (data: any) => request.post<ApiResponse>('/payment/inward', data),
  getDetail: (paymentNo: string) => request.get<ApiResponse>(`/payment/${paymentNo}`),
  pageQuery: (params: any) => request.post<ApiResponse<PageResp<any>>>('/payment/page', params),
  submit: (paymentNo: string) => request.post<ApiResponse>(`/payment/submit/${paymentNo}`),
  approve: (paymentNo: string) => request.post<ApiResponse>(`/payment/approve/${paymentNo}`),
  amlCheck: (data: any) => request.post<ApiResponse>('/payment/aml-check', data),
  send: (data: any) => request.post<ApiResponse>('/payment/send', data),
  cancel: (paymentNo: string, reason: string) =>
    request.post<ApiResponse>(`/payment/cancel/${paymentNo}?reason=${reason}`),
  updateGpiStatus: (data: any) => request.put<ApiResponse>('/payment/gpi-status', data)
}

export const settlementApi = {
  createLc: (data: any) => request.post<ApiResponse>('/settlement/lc/create', data),
  getLcDetail: (lcNo: string) => request.get<ApiResponse>(`/settlement/lc/${lcNo}`),
  lcPageQuery: (params: any) => request.post<ApiResponse<PageResp<any>>>('/settlement/lc/page', params),
  issueLc: (lcNo: string) => request.post<ApiResponse>(`/settlement/lc/issue/${lcNo}`),
  amendLc: (data: any) => request.post<ApiResponse>('/settlement/lc/amend', data),
  presentLcDocs: (lcNo: string) => request.post<ApiResponse>(`/settlement/lc/present/${lcNo}`),
  checkLcDocs: (lcNo: string, discrepant: boolean) =>
    request.post<ApiResponse>(`/settlement/lc/check-docs?lcNo=${lcNo}&discrepant=${discrepant}`),
  acceptLc: (lcNo: string) => request.post<ApiResponse>(`/settlement/lc/accept/${lcNo}`),
  payLc: (lcNo: string) => request.post<ApiResponse>(`/settlement/lc/pay/${lcNo}`),
  createCollection: (data: any) => request.post<ApiResponse>('/settlement/collection/create', data),
  getCollection: (collectionNo: string) => request.get<ApiResponse>(`/settlement/collection/${collectionNo}`),
  collectionPageQuery: (params: any) =>
    request.post<ApiResponse<PageResp<any>>>('/settlement/collection/page', params),
  payCollection: (collectionNo: string) => request.post<ApiResponse>(`/settlement/collection/pay/${collectionNo}`),
  createGuarantee: (data: any) => request.post<ApiResponse>('/settlement/guarantee/create', data),
  getGuarantee: (guaranteeNo: string) => request.get<ApiResponse>(`/settlement/guarantee/${guaranteeNo}`),
  guaranteePageQuery: (params: any) =>
    request.post<ApiResponse<PageResp<any>>>('/settlement/guarantee/page', params),
  issueGuarantee: (guaranteeNo: string) => request.post<ApiResponse>(`/settlement/guarantee/issue/${guaranteeNo}`)
}

export const riskApi = {
  evaluate: (data: any) => request.post<ApiResponse>('/risk/evaluate', data),
  createLog: (data: any) => request.post<ApiResponse>('/risk/log/create', data),
  getLog: (logNo: string) => request.get<ApiResponse>(`/risk/log/${logNo}`),
  logPageQuery: (params: any) => request.post<ApiResponse<PageResp<any>>>('/risk/log/page', params),
  generateReport: (data: any) => request.post<ApiResponse>('/risk/report/generate', data),
  submitReport: (reportNo: string) => request.post<ApiResponse>(`/risk/report/submit/${reportNo}`),
  getReport: (reportNo: string) => request.get<ApiResponse>(`/risk/report/${reportNo}`),
  listSamplingRules: () => request.get<ApiResponse<any[]>>('/risk/sampling/rules'),
  createSamplingRule: (data: any) => request.post<ApiResponse>('/risk/sampling/rules', data),
  updateSamplingRule: (id: number, data: any) => request.put<ApiResponse>(`/risk/sampling/rules/${id}`, data),
  updateSamplingRuleStatus: (id: number, status: string) =>
    request.put<ApiResponse>(`/risk/sampling/rules/${id}/status`, { status }),
  deleteSamplingRule: (id: number) => request.post<ApiResponse>(`/risk/sampling/rules/${id}/delete`),
  generateSamplingTasks: (data: any) => request.post<ApiResponse>('/risk/sampling/tasks/generate', data),
  listSamplingTasks: () => request.get<ApiResponse>('/risk/sampling/tasks'),
  completeSamplingTask: (taskId: string, data: any) =>
    request.put<ApiResponse>(`/risk/sampling/tasks/${taskId}/complete`, data),
  getSamplingStatistics: () => request.get<ApiResponse>('/risk/sampling/statistics')
}

export const scheduleApi = {
  addJob: (data: any) => request.post<ApiResponse>('/schedule/job/add', data),
  updateJob: (id: number, data: any) => request.put<ApiResponse>(`/schedule/job/update/${id}`, data),
  toggleJob: (id: number) => request.post<ApiResponse>(`/schedule/job/toggle/${id}`),
  triggerJob: (id: number) => request.post<ApiResponse>(`/schedule/job/trigger/${id}`),
  getJob: (id: number) => request.get<ApiResponse>(`/schedule/job/${id}`),
  pageJobs: (params: any) => request.post<ApiResponse<PageResp<any>>>('/schedule/job/page', params),
  getJobLogs: (jobId: number) => request.get<ApiResponse<any[]>>(`/schedule/job/${jobId}/logs`)
}

export const marginApi = {
  pageQuery: (params: any) => request.post<ApiResponse<PageResp<any>>>('/margin/page', params),
  getDetail: (marginNo: string) => request.get<ApiResponse>(`/margin/${marginNo}`),
  deposit: (marginNo: string, amount: number) =>
    request.post<ApiResponse>(`/margin/deposit?marginNo=${marginNo}&amount=${amount}`),
  call: (marginNo: string, amount: number) =>
    request.post<ApiResponse>(`/margin/call?marginNo=${marginNo}&amount=${amount}`),
  release: (marginNo: string, amount: number, reason: string) =>
    request.post<ApiResponse>(`/margin/release?marginNo=${marginNo}&amount=${amount}&reason=${encodeURIComponent(reason)}`),
  calculateVm: (data: any) => request.post<ApiResponse>('/margin/calculate/vm', data),
  calculateImStandard: (data: any) => request.post<ApiResponse>('/margin/calculate/im-standard', data),
  valueCollateral: (data: any) => request.post<ApiResponse>('/margin/collateral/valuation', data),
  ledgerSummary: () => request.get<ApiResponse>('/margin/collateral/ledger-summary')
}

export const accountApi = {
  open: (data: any) => request.post<ApiResponse>('/account/open', data),
  close: (id: number) => request.post<ApiResponse>(`/account/close/${id}`),
  getById: (id: number) => request.get<ApiResponse>(`/account/${id}`),
  getByAccountNo: (accountNo: string) => request.get<ApiResponse>(`/account/no/${accountNo}`),
  getCustomerAccounts: (customerId: number) => request.get<ApiResponse<any[]>>(`/account/customer/${customerId}`),
  deposit: (data: any) => request.post<ApiResponse>('/account/deposit', data),
  withdraw: (data: any) => request.post<ApiResponse>('/account/withdraw', data),
  freeze: (data: any) => request.post<ApiResponse>('/account/freeze', data),
  unfreeze: (data: any) => request.post<ApiResponse>('/account/unfreeze', data),
  getTransactions: (accountId: number, params: any) =>
    request.get<ApiResponse<PageResp<any>>>(`/account/${accountId}/transactions`, { params })
}

export const reportingApi = {
  createBop: (data: any) => request.post<ApiResponse>('/reporting/bop/create', data),
  createSettlementReport: (data: any) => request.post<ApiResponse>('/reporting/settlement/create', data),
  createCapitalReport: (data: any) => request.post<ApiResponse>('/reporting/capital/create', data),
  submitBatch: (data: any) => request.post<ApiResponse>('/reporting/batch/submit', data),
  getBopReport: (reportNo: string) => request.get<ApiResponse>(`/reporting/bop/${reportNo}`),
  bopPageQuery: (params: any) => request.post<ApiResponse<PageResp<any>>>('/reporting/bop/page', params)
}

export const positionApi = {
  create: (data: any) => request.post<ApiResponse>('/position/create', data),
  update: (id: number, data: any) => request.put<ApiResponse>(`/position/update/${id}`, data),
  getDetail: (positionNo: string) => request.get<ApiResponse>(`/position/${positionNo}`),
  pageQuery: (params: any) => request.post<ApiResponse<PageResp<any>>>('/position/page', params),
  aggregate: (params: any) => request.post<ApiResponse>('/position/aggregate', params),
  checkBreach: () => request.get<ApiResponse>('/position/breach/check')
}

export const workflowApi = {
  start: (data: any) => request.post<ApiResponse>('/workflow/start', data),
  complete: (taskId: string, data: any) => request.post<ApiResponse>(`/workflow/complete/${taskId}`, data),
  getTask: (taskId: string) => request.get<ApiResponse>(`/workflow/task/${taskId}`),
  pageQuery: (params: any) => request.post<ApiResponse<PageResp<any>>>('/workflow/task/page', params),
  getMyTasks: (assignee: string) => request.get<ApiResponse<any[]>>(`/workflow/my-tasks/${assignee}`)
}
