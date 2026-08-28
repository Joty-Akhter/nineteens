import { createContext, useContext, useEffect, useMemo, useState, type ReactNode } from 'react'
import { getProfile, login as loginApi, logout as logoutApi, register as registerApi } from './api/auth'
import { getToken, setToken } from './api/client'
import type { AuthResponse, UserSummary } from './types'

interface AuthState {
  user: UserSummary | null
  loading: boolean
  login: (email: string, password: string) => Promise<AuthResponse>
  register: (payload: {
    email: string
    password: string
    firstName: string
    lastName: string
    phone?: string
  }) => Promise<AuthResponse>
  logout: () => Promise<void>
}

const AuthContext = createContext<AuthState | undefined>(undefined)

export function AuthProvider({ children }: { children: ReactNode }) {
  const [user, setUser] = useState<UserSummary | null>(null)
  const [loading, setLoading] = useState(true)

  useEffect(() => {
    const token = getToken()
    if (!token) {
      setLoading(false)
      return
    }
    getProfile()
      .then(setUser)
      .catch(() => {
        setToken(null)
        setUser(null)
      })
      .finally(() => setLoading(false))
  }, [])

  const value = useMemo<AuthState>(
    () => ({
      user,
      loading,
      async login(email, password) {
        const response = await loginApi({ email, password })
        setToken(response.accessToken)
        setUser(response.user)
        return response
      },
      async register(payload) {
        const response = await registerApi(payload)
        setToken(response.accessToken)
        setUser(response.user)
        return response
      },
      async logout() {
        try {
          await logoutApi()
        } catch {
          /* token discard is enough */
        }
        setToken(null)
        setUser(null)
      },
    }),
    [user, loading],
  )

  return <AuthContext.Provider value={value}>{children}</AuthContext.Provider>
}

export function useAuth() {
  const ctx = useContext(AuthContext)
  if (!ctx) throw new Error('useAuth must be used within AuthProvider')
  return ctx
}
