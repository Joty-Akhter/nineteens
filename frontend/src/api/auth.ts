import { api } from './client'
import type { AuthResponse, UserSummary } from '../types'

export function register(payload: {
  email: string
  password: string
  firstName: string
  lastName: string
  phone?: string
}) {
  return api.post<AuthResponse>('/auth/register', payload).then((r) => r.data)
}

export function login(payload: { email: string; password: string }) {
  return api.post<AuthResponse>('/auth/login', payload).then((r) => r.data)
}

export function logout() {
  return api.post('/auth/logout')
}

export function getProfile() {
  return api.get<UserSummary>('/profile').then((r) => r.data)
}

export function updateProfile(payload: { firstName: string; lastName: string; phone?: string }) {
  return api.put<UserSummary>('/profile', payload).then((r) => r.data)
}
