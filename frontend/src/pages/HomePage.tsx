import { useEffect, useState } from 'react'
import { Link } from 'react-router-dom'
import { getFeatured, getNewProducts, getSaleProducts, listCategories, listOffers } from '../api/shop'
import { ProductGrid } from '../components/ProductGrid'
import { SectionHeading } from '../components/ui'
import type { Category, Offer, Product } from '../types'

export function HomePage() {
  const [featured, setFeatured] = useState<Product[]>([])
  const [newest, setNewest] = useState<Product[]>([])
  const [sale, setSale] = useState<Product[]>([])
  const [categories, setCategories] = useState<Category[]>([])
  const [offers, setOffers] = useState<Offer[]>([])

  useEffect(() => {
    getFeatured().then(setFeatured).catch(() => setFeatured([]))
    getNewProducts().then(setNewest).catch(() => setNewest([]))
    getSaleProducts().then(setSale).catch(() => setSale([]))
    listCategories().then(setCategories).catch(() => setCategories([]))
    listOffers().then(setOffers).catch(() => setOffers([]))
  }, [])

  return (
    <div>
      <section className="relative overflow-hidden bg-ink text-paper">
        <img
          src="https://images.unsplash.com/photo-1490481651871-ab68de25d43d?auto=format&fit=crop&w=1800&q=80"
          alt=""
          className="absolute inset-0 h-full w-full object-cover opacity-45"
        />
        <div className="relative mx-auto flex min-h-[70vh] max-w-6xl flex-col justify-end px-4 py-20 md:px-6">
          <p className="text-xs uppercase tracking-[0.28em] text-copper">Spring / monsoon 2026</p>
          <h1 className="mt-4 max-w-xl font-display text-5xl leading-none md:text-7xl">Cloth with a quieter life.</h1>
          <p className="mt-4 max-w-md text-paper/80">
            Linen, oxford, and leather from the Nineteens studio. Cash on delivery, across Bangladesh.
          </p>
          <div className="mt-8 flex gap-3">
            <Link to="/products" className="bg-copper px-5 py-3 text-sm text-white">
              Shop the edit
            </Link>
            <Link to="/offers" className="border border-paper/40 px-5 py-3 text-sm">
              Current offers
            </Link>
          </div>
        </div>
      </section>

      {offers.length > 0 && (
        <section className="border-b border-line bg-paper-2">
          <div className="mx-auto flex max-w-6xl items-center justify-between gap-4 px-4 py-4 md:px-6">
            <p className="text-sm">
              <span className="font-medium">{offers[0].name}</span> — {offers[0].description}
            </p>
            <Link to="/offers" className="text-sm underline underline-offset-4">
              Details
            </Link>
          </div>
        </section>
      )}

      <section className="mx-auto max-w-6xl px-4 py-16 md:px-6">
        <SectionHeading eyebrow="Departments" title="Popular categories" />
        <div className="grid grid-cols-2 gap-4 md:grid-cols-3 lg:grid-cols-5">
          {categories.map((category) => (
            <Link key={category.id} to={`/category/${category.slug}`} className="group relative block overflow-hidden">
              <img src={category.imageUrl || ''} alt="" className="aspect-[3/4] w-full object-cover" />
              <span className="absolute inset-x-0 bottom-0 bg-ink/70 px-3 py-3 font-display text-lg text-paper">
                {category.name}
              </span>
            </Link>
          ))}
        </div>
      </section>

      <section className="mx-auto max-w-6xl px-4 pb-16 md:px-6">
        <SectionHeading eyebrow="Staff picks" title="Featured" to="/products?sort=popular" />
        <ProductGrid products={featured} />
      </section>
      <section className="mx-auto max-w-6xl px-4 pb-16 md:px-6">
        <SectionHeading eyebrow="Just in" title="New products" to="/products?sort=newest" />
        <ProductGrid products={newest} />
      </section>
      <section className="mx-auto max-w-6xl px-4 pb-16 md:px-6">
        <SectionHeading eyebrow="Limited" title="On sale" to="/products?onSale=true" />
        <ProductGrid products={sale} />
      </section>
    </div>
  )
}
