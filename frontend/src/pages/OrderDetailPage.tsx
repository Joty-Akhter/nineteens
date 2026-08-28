import { useEffect, useState } from 'react'
import { Link, useParams } from 'react-router-dom'
import { getOrder } from '../api/shop'
import { formatMoney } from '../lib/format'
import type { Order } from '../types'

export function OrderDetailPage() {
  const { orderId } = useParams()
  const [order, setOrder] = useState<Order | null>(null)

  useEffect(() => {
    if (!orderId) return
    getOrder(Number(orderId)).then(setOrder).catch(() => setOrder(null))
  }, [orderId])

  if (!order) return <p className="px-6 py-20 text-center">Loading order…</p>

  return (
    <div className="mx-auto max-w-3xl px-4 py-10 md:px-6">
      <p className="text-xs uppercase tracking-[0.2em] text-copper">Order</p>
      <h1 className="mt-2 font-display text-4xl">{order.orderNumber}</h1>
      <p className="mt-2 text-sm text-muted">
        {order.status} · {order.payment?.provider.replaceAll('_', ' ')} · {order.payment?.status}
      </p>
      <div className="mt-8 space-y-3 text-sm">
        {order.items.map((item) => (
          <div key={item.id} className="flex justify-between border-b border-line pb-3">
            <span>
              {item.productName} × {item.quantity}
            </span>
            <span>{formatMoney(item.totalPrice)}</span>
          </div>
        ))}
      </div>
      <div className="mt-6 space-y-1 text-sm">
        <p className="flex justify-between"><span>Subtotal</span><span>{formatMoney(order.subtotal)}</span></p>
        <p className="flex justify-between"><span>Discount</span><span>−{formatMoney(order.discount)}</span></p>
        <p className="flex justify-between"><span>Shipping</span><span>{formatMoney(order.shippingCost)}</span></p>
        <p className="flex justify-between font-medium"><span>Total</span><span>{formatMoney(order.totalAmount)}</span></p>
      </div>
      <div className="mt-8 text-sm">
        <p className="font-medium">Shipping to</p>
        <p className="mt-1 text-muted">
          {order.shippingName}, {order.shippingPhone}
          <br />
          {order.shippingAddress}, {order.shippingCity} {order.shippingPostalCode}
        </p>
        {order.deliveryNote && <p className="mt-2">Note: {order.deliveryNote}</p>}
      </div>
      <Link to="/account" className="mt-8 inline-block underline">
        Back to account
      </Link>
    </div>
  )
}
