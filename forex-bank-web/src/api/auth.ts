import request from './request'
import type { ApiResponse, TokenInfo } from '@/types/api'

export const authApi = {
  login: (username: string, password: string) =>
    request.post<ApiResponse<TokenInfo>>('/auth/login', { username, password }),
  logout: () =>
    request.post<ApiResponse>('/auth/logout'),
  refresh: (refreshToken: string) =>
    request.post<ApiResponse<TokenInfo>>('/auth/refresh', null, {
      headers: { Authorization: `Bearer ${refreshToken}` }
    }),
  me: () =>
    request.get<ApiResponse<TokenInfo>>('/auth/me')
}
