import { type FormEvent, useEffect, useState } from 'react'
import { useNavigate } from 'react-router-dom'
import { checkout, listAddresses } from '../api/shop'
import { getErrorMessage } from '../api/client'
import { useCart } from '../cart'
import { formatMoney } from '../lib/format'
import type { Address } from '../types'

export function CheckoutPage() {
  const { cart, refresh } = useCart()
  const navigate = useNavigate()
  const [addresses, setAddresses] = useState<Address[]>([])
  const [error, setError] = useState('')
  const [form, setForm] = useState({
    recipientName: '',
    phone: '',
    addressLine: '',
    city: '',
    postalCode: '',
    deliveryNote: '',
  })

  useEffect(() => {
    listAddresses()
      .then((items) => {
        setAddresses(items)
        const preferred = items.find((a) => a.defaultAddress) || items[0]
        if (preferred) {
          setForm((current) => ({
            ...current,
            recipientName: preferred.recipientName,
            phone: preferred.phone,
            addressLine: preferred.addressLine,
            city: preferred.city,
            postalCode: preferred.postalCode,
          }))
        }
      })
      .catch(() => setAddresses([]))
  }, [])

  async function onSubmit(event: FormEvent) {
    event.preventDefault()
    setError('')
    try {
      const order = await checkout(form)
      await refresh()
      navigate(`/orders/${order.id}`)
    } catch (err) {
      setError(getErrorMessage(err, 'Checkout failed'))
    }
  }

  if (!cart.items.length) {
    return <p className="px-6 py-20 text-center">Your cart is empty.</p>
  }

  return (
    <div className="mx-auto grid max-w-5xl gap-10 px-4 py-10 md:grid-cols-[1fr_280px] md:px-6">
      <form onSubmit={(e) => void onSubmit(e)} className="grid gap-4">
        <h1 className="font-display text-4xl">Checkout</h1>
        <p className="text-sm text-muted">Payment: Cash on Delivery. Online wallets can be added later.</p>
        {addresses.length > 0 && (
          <label className="text-sm">
            Saved address
            <select
              className="mt-1 w-full border border-line px-3 py-2"
              onChange={(e) => {
                const address = addresses.find((item) => String(item.id) === e.target.value)
                if (!address) return
                setForm({
                  ...form,
                  recipientName: address.recipientName,
                  phone: address.phone,
                  addressLine: address.addressLine,
                  city: address.city,
                  postalCode: address.postalCode,
                })
              }}
            >
              {addresses.map((address) => (
                <option key={address.id} value={address.id}>
                  {address.recipientName} — {address.city}
                </option>
              ))}
            </select>
          </label>
        )}
        <input className="border border-line px-3 py-3" placeholder="Full name" value={form.recipientName} onChange={(e) => setForm({ ...form, recipientName: e.target.value })} required />
        <input className="border border-line px-3 py-3" placeholder="Phone" value={form.phone} onChange={(e) => setForm({ ...form, phone: e.target.value })} required />
        <input className="border border-line px-3 py-3" placeholder="Shipping address" value={form.addressLine} onChange={(e) => setForm({ ...form, addressLine: e.target.value })} required />
        <div className="grid grid-cols-2 gap-3">
          <input className="border border-line px-3 py-3" placeholder="City" value={form.city} onChange={(e) => setForm({ ...form, city: e.target.value })} required />
          <input className="border border-line px-3 py-3" placeholder="Postal code" value={form.postalCode} onChange={(e) => setForm({ ...form, postalCode: e.target.value })} required />
        </div>
        <textarea className="border border-line px-3 py-3" placeholder="Delivery note (optional)" value={form.deliveryNote} onChange={(e) => setForm({ ...form, deliveryNote: e.target.value })} />
        {error && <p className="text-sm text-copper">{error}</p>}
        <button className="bg-ink py-3 text-paper">Place order</button>
      </form>
      <aside className="h-fit border border-line bg-white p-5 text-sm">
        {cart.items.map((item) => (
          <p key={item.id} className="mb-2 flex justify-between">
            <span>
              {item.productName} × {item.quantity}
            </span>
            <span>{formatMoney(item.lineTotal)}</span>
          </p>
        ))}
        <p className="mt-4 flex justify-between">
          <span>Discount</span>
          <span>−{formatMoney(cart.discount)}</span>
        </p>
        <p className="mt-2 flex justify-between">
          <span>Shipping</span>
          <span>৳80</span>
        </p>
        <p className="mt-4 flex justify-between font-medium">
          <span>To pay on delivery</span>
          <span>{formatMoney(Number(cart.total) + 80)}</span>
        </p>
      </aside>
    </div>
  )
}
