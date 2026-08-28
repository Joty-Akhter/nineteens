import { useEffect, useState } from 'react'
import { adminGetOrder, adminOrders, adminUpdateOrderStatus } from '../../api/shop'
import { Pagination } from '../../components/ui'
import { formatMoney } from '../../lib/format'
import type { Order, OrderStatus, PageResponse } from '../../types'

const statuses: OrderStatus[] = ['PENDING', 'CONFIRMED', 'PROCESSING', 'SHIPPED', 'DELIVERED', 'CANCELLED']

export function AdminOrdersPage() {
  const [page, setPage] = useState<PageResponse<Order> | null>(null)
  const [selected, setSelected] = useState<Order | null>(null)

  function load(p = 0) {
    adminOrders(p).then(setPage)
  }

  useEffect(() => {
    load()
  }, [])

  return (
    <div>
      <h1 className="font-display text-4xl">Orders</h1>
      <div className="mt-6 overflow-x-auto">
        <table className="w-full text-left text-sm">
          <thead>
            <tr className="border-b border-line">
              <th className="py-2">Number</th>
              <th>Status</th>
              <th>Total</th>
              <th />
            </tr>
          </thead>
          <tbody>
            {page?.content.map((order) => (
              <tr key={order.id} className="border-b border-line">
                <td className="py-3">{order.orderNumber}</td>
                <td>{order.status}</td>
                <td>{formatMoney(order.totalAmount)}</td>
                <td>
                  <button
                    type="button"
                    className="underline"
                    onClick={async () => setSelected(await adminGetOrder(order.id))}
                  >
                    Details
                  </button>
                </td>
              </tr>
            ))}
          </tbody>
        </table>
      </div>
      <Pagination page={page?.page || 0} totalPages={page?.totalPages || 0} onPage={(p) => load(p)} />
      {selected && (
        <div className="mt-8 border border-line bg-white p-5 text-sm">
          <h2 className="font-display text-2xl">{selected.orderNumber}</h2>
          <p className="mt-2">
            {selected.shippingName} · {selected.shippingPhone}
            <br />
            {selected.shippingAddress}, {selected.shippingCity}
          </p>
          <ul className="mt-4 space-y-1">
            {selected.items.map((item) => (
              <li key={item.id}>
                {item.productName} × {item.quantity} — {formatMoney(item.totalPrice)}
              </li>
            ))}
          </ul>
          <label className="mt-4 block">
            Status
            <select
              className="ml-2 border border-line px-2 py-1"
              value={selected.status}
              onChange={async (e) => {
                const updated = await adminUpdateOrderStatus(selected.id, e.target.value)
                setSelected(updated)
                load(page?.page || 0)
              }}
            >
              {statuses.map((status) => (
                <option key={status}>{status}</option>
              ))}
            </select>
          </label>
        </div>
      )}
    </div>
  )
}
