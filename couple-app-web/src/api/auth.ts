import { authApi } from './client'

export interface AuthResponse {
  accessToken: string
  refreshToken: string
  userId: string
  nickname: string
}

export async function login(email: string, password: string): Promise<AuthResponse> {
  const { data } = await authApi.post<AuthResponse>('/api/auth/login', { email, password })
  persist(data)
  return data
}

export async function register(email: string, password: string, nickname: string): Promise<AuthResponse> {
  const { data } = await authApi.post<AuthResponse>('/api/auth/register', { email, password, nickname })
  persist(data)
  return data
}

export function logout() {
  localStorage.clear()
}

function persist(data: AuthResponse) {
  localStorage.setItem('accessToken', data.accessToken)
  localStorage.setItem('refreshToken', data.refreshToken)
  localStorage.setItem('userId', data.userId)
  localStorage.setItem('nickname', data.nickname)
}

export function getCurrentUser() {
  return {
    userId: localStorage.getItem('userId'),
    nickname: localStorage.getItem('nickname'),
    isLoggedIn: !!localStorage.getItem('accessToken'),
  }
}

// Decode coupleId from JWT (no verification needed client-side)
export function getCoupleId(): string | null {
  const token = localStorage.getItem('accessToken')
  if (!token) return null
  try {
    const payload = JSON.parse(atob(token.split('.')[1]))
    return payload.coupleId || null
  } catch {
    return null
  }
}
