import { useEffect, useState } from 'react'
import { Link } from 'react-router-dom'
import { listOffers, listProducts } from '../api/shop'
import { ProductGrid } from '../components/ProductGrid'
import type { Offer, Product } from '../types'

export function OffersPage() {
  const [offers, setOffers] = useState<Offer[]>([])
  const [sale, setSale] = useState<Product[]>([])

  useEffect(() => {
    listOffers().then(setOffers).catch(() => setOffers([]))
    listProducts({ onSale: true, size: 12 }).then((page) => setSale(page.content)).catch(() => setSale([]))
  }, [])

  return (
    <div className="mx-auto max-w-6xl px-4 py-10 md:px-6">
      <h1 className="font-display text-4xl">Offers & sales</h1>
      <div className="mt-8 grid gap-4 md:grid-cols-2">
        {offers.map((offer) => (
          <article key={offer.id} className="border border-line bg-white p-6">
            <p className="text-xs uppercase tracking-[0.2em] text-copper">{offer.discountType === 'PERCENTAGE' ? `${offer.discountValue}% off` : `৳${offer.discountValue} off`}</p>
            <h2 className="mt-2 font-display text-3xl">{offer.name}</h2>
            <p className="mt-2 text-muted">{offer.description}</p>
          </article>
        ))}
        {!offers.length && <p className="text-muted">No live offers right now.</p>}
      </div>
      <section className="mt-12">
        <div className="mb-6 flex items-end justify-between">
          <h2 className="font-display text-3xl">Sale products</h2>
          <Link to="/products?onSale=true" className="text-sm underline">
            View all
          </Link>
        </div>
        <ProductGrid products={sale} />
      </section>
    </div>
  )
}
