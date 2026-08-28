import { useEffect, useMemo, useState } from 'react'
import { useSearchParams } from 'react-router-dom'
import { listCategories, listProducts, searchProducts } from '../api/shop'
import { ProductGrid } from '../components/ProductGrid'
import { Pagination } from '../components/ui'
import type { Category, PageResponse, Product } from '../types'

const sorts = [
  { value: 'newest', label: 'Newest' },
  { value: 'price_asc', label: 'Price: low to high' },
  { value: 'price_desc', label: 'Price: high to low' },
  { value: 'popular', label: 'Popular' },
]

export function ProductListView({
  title,
  categorySlug,
  searchQuery,
}: {
  title: string
  categorySlug?: string
  searchQuery?: string
}) {
  const [params, setParams] = useSearchParams()
  const [pageData, setPageData] = useState<PageResponse<Product> | null>(null)
  const [categories, setCategories] = useState<Category[]>([])
  const [loading, setLoading] = useState(true)

  const filters = useMemo(
    () => ({
      category: categorySlug || params.get('category') || undefined,
      minPrice: params.get('minPrice') ? Number(params.get('minPrice')) : undefined,
      maxPrice: params.get('maxPrice') ? Number(params.get('maxPrice')) : undefined,
      inStock: params.get('inStock') === 'true' ? true : undefined,
      onSale: params.get('onSale') === 'true' ? true : undefined,
      sort: params.get('sort') || 'newest',
      page: Number(params.get('page') || 0),
      q: searchQuery || params.get('q') || undefined,
    }),
    [params, categorySlug, searchQuery],
  )

  useEffect(() => {
    listCategories().then(setCategories).catch(() => setCategories([]))
  }, [])

  useEffect(() => {
    setLoading(true)
    const request = searchQuery || filters.q
      ? searchProducts({ ...filters, q: searchQuery || filters.q, category: filters.category, size: 12 })
      : listProducts({ ...filters, category: filters.category, size: 12 })
    request
      .then(setPageData)
      .catch(() => setPageData(null))
      .finally(() => setLoading(false))
  }, [filters, searchQuery])

  function update(next: Record<string, string | undefined>) {
    const copy = new URLSearchParams(params)
    Object.entries(next).forEach(([key, value]) => {
      if (!value) copy.delete(key)
      else copy.set(key, value)
    })
    if (!('page' in next)) copy.delete('page')
    setParams(copy)
  }

  return (
    <div className="mx-auto max-w-6xl px-4 py-10 md:px-6">
      <p className="text-xs uppercase tracking-[0.22em] text-copper">Catalog</p>
      <h1 className="mt-2 font-display text-4xl">{title}</h1>
      <div className="mt-8 grid gap-8 lg:grid-cols-[220px_1fr]">
        <aside className="space-y-6 text-sm">
          {!categorySlug && (
            <label className="block">
              Category
              <select
                className="mt-1 w-full border border-line bg-white px-2 py-2"
                value={filters.category || ''}
                onChange={(e) => update({ category: e.target.value || undefined })}
              >
                <option value="">All</option>
                {categories.map((category) => (
                  <option key={category.id} value={category.slug}>
                    {category.name}
                  </option>
                ))}
              </select>
            </label>
          )}
          <div>
            <p className="mb-2">Price range (৳)</p>
            <div className="flex gap-2">
              <input
                type="number"
                placeholder="Min"
                defaultValue={filters.minPrice || ''}
                className="w-full border border-line px-2 py-2"
                onBlur={(e) => update({ minPrice: e.target.value || undefined })}
              />
              <input
                type="number"
                placeholder="Max"
                defaultValue={filters.maxPrice || ''}
                className="w-full border border-line px-2 py-2"
                onBlur={(e) => update({ maxPrice: e.target.value || undefined })}
              />
            </div>
          </div>
          <label className="flex items-center gap-2">
            <input
              type="checkbox"
              checked={!!filters.inStock}
              onChange={(e) => update({ inStock: e.target.checked ? 'true' : undefined })}
            />
            In stock
          </label>
          <label className="flex items-center gap-2">
            <input
              type="checkbox"
              checked={!!filters.onSale}
              onChange={(e) => update({ onSale: e.target.checked ? 'true' : undefined })}
            />
            On sale / offer
          </label>
          <label className="block">
            Sort
            <select
              className="mt-1 w-full border border-line bg-white px-2 py-2"
              value={filters.sort}
              onChange={(e) => update({ sort: e.target.value })}
            >
              {sorts.map((sort) => (
                <option key={sort.value} value={sort.value}>
                  {sort.label}
                </option>
              ))}
            </select>
          </label>
        </aside>
        <div>
          {loading ? (
            <p className="py-16 text-center text-muted">Loading the rack…</p>
          ) : (
            <>
              <ProductGrid products={pageData?.content || []} />
              <Pagination
                page={pageData?.page || 0}
                totalPages={pageData?.totalPages || 0}
                onPage={(page) => update({ page: String(page) })}
              />
            </>
          )}
        </div>
      </div>
    </div>
  )
}
