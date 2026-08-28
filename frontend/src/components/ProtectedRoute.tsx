import { Navigate, Outlet, useLocation } from 'react-router-dom'
import { useAuth } from '../auth'

export function ProtectedRoute({ role }: { role?: 'ADMIN' | 'USER' }) {
  const { user, loading } = useAuth()
  const location = useLocation()

  if (loading) {
    return <p className="px-6 py-20 text-center text-muted">Loading…</p>
  }
  if (!user) {
    return <Navigate to="/login" replace state={{ from: location.pathname }} />
  }
  if (role && user.role !== role) {
    return <Navigate to="/" replace />
  }
  return <Outlet />
}
