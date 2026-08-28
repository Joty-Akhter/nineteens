import { useEffect, useState } from 'react'
import { adminDashboard } from '../../api/shop'
import { formatMoney } from '../../lib/format'
import type { DashboardStats } from '../../types'

export function AdminDashboardPage() {
  const [stats, setStats] = useState<DashboardStats | null>(null)

  useEffect(() => {
    adminDashboard().then(setStats).catch(() => setStats(null))
  }, [])

  if (!stats) return <p>Loading dashboard…</p>

  const cards = [
    { label: 'Revenue', value: formatMoney(stats.totalRevenue) },
    { label: 'Orders', value: stats.totalOrders },
    { label: 'Pending', value: stats.pendingOrders },
    { label: 'Products', value: stats.totalProducts },
    { label: 'Users', value: stats.totalUsers },
  ]

  return (
    <div>
      <h1 className="font-display text-4xl">Dashboard</h1>
      <div className="mt-8 grid grid-cols-2 gap-4 md:grid-cols-3 lg:grid-cols-5">
        {cards.map((card) => (
          <div key={card.label} className="border border-line bg-white p-4">
            <p className="text-xs uppercase tracking-[0.18em] text-muted">{card.label}</p>
            <p className="mt-2 font-display text-3xl">{card.value}</p>
          </div>
        ))}
      </div>
    </div>
  )
}
