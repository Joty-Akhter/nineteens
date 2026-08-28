import { type FormEvent, useEffect, useState } from 'react'
import { adminDeactivateOffer, adminOffers, adminProducts, adminSaveOffer } from '../../api/shop'
import { getErrorMessage } from '../../api/client'
import type { Offer, Product } from '../../types'

export function AdminOffersPage() {
  const [offers, setOffers] = useState<Offer[]>([])
  const [products, setProducts] = useState<Product[]>([])
  const [error, setError] = useState('')
  const [editingId, setEditingId] = useState<number | undefined>()
  const [form, setForm] = useState({
    name: '',
    description: '',
    discountType: 'PERCENTAGE',
    discountValue: '10',
    startAt: '',
    endAt: '',
    status: 'ACTIVE',
    productIds: [] as number[],
  })

  function load() {
    adminOffers().then(setOffers)
    adminProducts({ size: 48 }).then((page) => setProducts(page.content))
  }

  useEffect(() => {
    load()
  }, [])

  async function onSubmit(event: FormEvent) {
    event.preventDefault()
    setError('')
    try {
      await adminSaveOffer(
        {
          ...form,
          discountValue: Number(form.discountValue),
          startAt: new Date(form.startAt).toISOString(),
          endAt: new Date(form.endAt).toISOString(),
        },
        editingId,
      )
      setEditingId(undefined)
      load()
    } catch (err) {
      setError(getErrorMessage(err))
    }
  }

  return (
    <div>
      <h1 className="font-display text-4xl">Offers</h1>
      <form onSubmit={(e) => void onSubmit(e)} className="mt-6 grid max-w-xl gap-3">
        <input className="border border-line px-3 py-2" placeholder="Name" value={form.name} onChange={(e) => setForm({ ...form, name: e.target.value })} />
        <textarea className="border border-line px-3 py-2" placeholder="Description" value={form.description} onChange={(e) => setForm({ ...form, description: e.target.value })} />
        <select className="border border-line px-3 py-2" value={form.discountType} onChange={(e) => setForm({ ...form, discountType: e.target.value })}>
          <option value="PERCENTAGE">Percentage</option>
          <option value="FIXED">Fixed amount</option>
        </select>
        <input className="border border-line px-3 py-2" placeholder="Value" value={form.discountValue} onChange={(e) => setForm({ ...form, discountValue: e.target.value })} />
        <input className="border border-line px-3 py-2" type="datetime-local" value={form.startAt} onChange={(e) => setForm({ ...form, startAt: e.target.value })} />
        <input className="border border-line px-3 py-2" type="datetime-local" value={form.endAt} onChange={(e) => setForm({ ...form, endAt: e.target.value })} />
        <select
          multiple
          className="h-40 border border-line px-3 py-2"
          value={form.productIds.map(String)}
          onChange={(e) =>
            setForm({
              ...form,
              productIds: Array.from(e.target.selectedOptions).map((opt) => Number(opt.value)),
            })
          }
        >
          {products.map((product) => (
            <option key={product.id} value={product.id}>
              {product.name}
            </option>
          ))}
        </select>
        {error && <p className="text-sm text-copper">{error}</p>}
        <button className="bg-ink py-2 text-paper">{editingId ? 'Update offer' : 'Create offer'}</button>
      </form>
      <div className="mt-8 space-y-3 text-sm">
        {offers.map((offer) => (
          <div key={offer.id} className="flex items-center justify-between border border-line p-3">
            <span>
              {offer.name} · {offer.status} {offer.live ? '· live' : ''}
            </span>
            <span className="space-x-3">
              <button
                type="button"
                className="underline"
                onClick={() => {
                  setEditingId(offer.id)
                  setForm({
                    name: offer.name,
                    description: offer.description || '',
                    discountType: offer.discountType,
                    discountValue: String(offer.discountValue),
                    startAt: offer.startAt.slice(0, 16),
                    endAt: offer.endAt.slice(0, 16),
                    status: offer.status,
                    productIds: offer.productIds,
                  })
                }}
              >
                Edit
              </button>
              <button
                type="button"
                className="underline"
                onClick={async () => {
                  await adminDeactivateOffer(offer.id)
                  load()
                }}
              >
                Deactivate
              </button>
            </span>
          </div>
        ))}
      </div>
    </div>
  )
}
