import { type FormEvent, useEffect, useState } from 'react'
import { Link } from 'react-router-dom'
import { createAddress, deleteAddress, listAddresses, listOrders } from '../api/shop'
import { updateProfile } from '../api/auth'
import { getErrorMessage } from '../api/client'
import { useAuth } from '../auth'
import { formatMoney } from '../lib/format'
import type { Address, Order } from '../types'

const emptyAddress = {
  recipientName: '',
  phone: '',
  addressLine: '',
  city: '',
  postalCode: '',
  defaultAddress: true,
}

export function AccountPage() {
  const { user } = useAuth()
  const [profile, setProfile] = useState({ firstName: user?.firstName || '', lastName: user?.lastName || '', phone: user?.phone || '' })
  const [addresses, setAddresses] = useState<Address[]>([])
  const [orders, setOrders] = useState<Order[]>([])
  const [form, setForm] = useState(emptyAddress)
  const [message, setMessage] = useState('')

  useEffect(() => {
    listAddresses().then(setAddresses).catch(() => setAddresses([]))
    listOrders().then((page) => setOrders(page.content)).catch(() => setOrders([]))
  }, [])

  async function saveProfile(event: FormEvent) {
    event.preventDefault()
    try {
      await updateProfile(profile)
      setMessage('Profile updated')
    } catch (error) {
      setMessage(getErrorMessage(error))
    }
  }

  async function saveAddress(event: FormEvent) {
    event.preventDefault()
    try {
      await createAddress(form)
      setForm(emptyAddress)
      setAddresses(await listAddresses())
    } catch (error) {
      setMessage(getErrorMessage(error))
    }
  }

  return (
    <div className="mx-auto max-w-5xl px-4 py-10 md:px-6">
      <h1 className="font-display text-4xl">Account</h1>
      {message && <p className="mt-3 text-sm text-copper">{message}</p>}
      <div className="mt-8 grid gap-10 md:grid-cols-2">
        <form onSubmit={(e) => void saveProfile(e)} className="grid gap-3">
          <h2 className="font-display text-2xl">Profile</h2>
          <input className="border border-line px-3 py-2" value={profile.firstName} onChange={(e) => setProfile({ ...profile, firstName: e.target.value })} />
          <input className="border border-line px-3 py-2" value={profile.lastName} onChange={(e) => setProfile({ ...profile, lastName: e.target.value })} />
          <input className="border border-line px-3 py-2" value={profile.phone} onChange={(e) => setProfile({ ...profile, phone: e.target.value })} />
          <button className="bg-ink py-2 text-paper">Save profile</button>
        </form>
        <form onSubmit={(e) => void saveAddress(e)} className="grid gap-3">
          <h2 className="font-display text-2xl">Add shipping address</h2>
          <input className="border border-line px-3 py-2" placeholder="Recipient" value={form.recipientName} onChange={(e) => setForm({ ...form, recipientName: e.target.value })} />
          <input className="border border-line px-3 py-2" placeholder="Phone" value={form.phone} onChange={(e) => setForm({ ...form, phone: e.target.value })} />
          <input className="border border-line px-3 py-2" placeholder="Address" value={form.addressLine} onChange={(e) => setForm({ ...form, addressLine: e.target.value })} />
          <input className="border border-line px-3 py-2" placeholder="City" value={form.city} onChange={(e) => setForm({ ...form, city: e.target.value })} />
          <input className="border border-line px-3 py-2" placeholder="Postal code" value={form.postalCode} onChange={(e) => setForm({ ...form, postalCode: e.target.value })} />
          <label className="text-sm">
            <input type="checkbox" checked={form.defaultAddress} onChange={(e) => setForm({ ...form, defaultAddress: e.target.checked })} /> Default
          </label>
          <button className="bg-ink py-2 text-paper">Save address</button>
        </form>
      </div>
      <section className="mt-10">
        <h2 className="font-display text-2xl">Saved addresses</h2>
        <div className="mt-4 grid gap-3">
          {addresses.map((address) => (
            <div key={address.id} className="flex items-start justify-between border border-line p-4 text-sm">
              <p>
                {address.recipientName}, {address.phone}
                <br />
                {address.addressLine}, {address.city} {address.postalCode}
                {address.defaultAddress ? ' · Default' : ''}
              </p>
              <button
                type="button"
                className="underline"
                onClick={async () => {
                  await deleteAddress(address.id)
                  setAddresses(await listAddresses())
                }}
              >
                Remove
              </button>
            </div>
          ))}
        </div>
      </section>
      <section className="mt-10">
        <h2 className="font-display text-2xl">Order history</h2>
        <div className="mt-4 divide-y divide-line border border-line">
          {orders.map((order) => (
            <Link key={order.id} to={`/orders/${order.id}`} className="flex justify-between px-4 py-3 text-sm">
              <span>{order.orderNumber}</span>
              <span>{order.status}</span>
              <span>{formatMoney(order.totalAmount)}</span>
            </Link>
          ))}
          {!orders.length && <p className="px-4 py-6 text-muted">No orders yet.</p>}
        </div>
      </section>
    </div>
  )
}

