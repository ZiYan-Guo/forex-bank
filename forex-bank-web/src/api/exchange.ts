import request from './request'
import type { ApiResponse, PageResp } from '@/types/api'

export const exchangeApi = {
  createOrder: (data: any) => request.post<ApiResponse>('/exchange/create', data),
  lockRate: (data: any) => request.post<ApiResponse>('/exchange/lock-rate', data),
  confirm: (orderNo: string) => request.post<ApiResponse>(`/exchange/confirm/${orderNo}`),
  cancel: (data: any) => request.post<ApiResponse>('/exchange/cancel', data),
  reverse: (orderNo: string) => request.post<ApiResponse>(`/exchange/reverse/${orderNo}`),
  getDetail: (orderNo: string) => request.get<ApiResponse>(`/exchange/${orderNo}`),
  pageQuery: (params: any) => request.post<ApiResponse<PageResp<any>>>('/exchange/page', params),
  getQuote: (data: any) => request.post<ApiResponse>('/exchange/quote', data),
  calculate: (data: any) => request.post<ApiResponse<number>>('/exchange/calculate', data)
}

export const rateApi = {
  getLatest: (pair: string) => request.get<ApiResponse>(`/rate/latest/${pair}`),
  getAll: () => request.get<ApiResponse<any[]>>('/rate/latest/all'),
  pageQuery: (params: any) => request.post<ApiResponse<PageResp<any>>>('/rate/page', params),
  convert: (data: any) => request.post<ApiResponse>('/rate/convert', data),
  save: (data: any) => request.post<ApiResponse>('/rate/save', data),
  publish: (rateId: number) => request.post<ApiResponse>(`/rate/publish/${rateId}`)
}
