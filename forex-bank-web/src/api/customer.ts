import request from './request'
import type { ApiResponse, PageResp } from '@/types/api'

export const customerApi = {
  create: (data: any) => request.post<ApiResponse>('/customer/create', data),
  update: (data: any) => request.put<ApiResponse>('/customer/update', data),
  getById: (id: number) => request.get<ApiResponse>(`/customer/${id}`),
  pageQuery: (params: any) => request.post<ApiResponse<PageResp<any>>>('/customer/page', params),
  updateRiskLevel: (data: any) => request.put<ApiResponse>('/customer/risk-level', data),
  checkCredit: (data: any) => request.post<ApiResponse<boolean>>('/customer/check-credit', data),
  deductCredit: (data: any) => request.post<ApiResponse>('/customer/deduct-credit', data),
  performDueDiligence: (id: number) => request.put<ApiResponse>(`/customer/due-diligence/${id}`)
}
