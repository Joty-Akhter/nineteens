import { type FormEvent, useEffect, useState } from 'react'
import { Link, useNavigate, useParams } from 'react-router-dom'
import {
  adminCategories,
  adminDeactivateProduct,
  adminGetProduct,
  adminProducts,
  adminSaveProduct,
  adminUploadImage,
} from '../../api/shop'
import { getErrorMessage } from '../../api/client'
import { Pagination } from '../../components/ui'
import { formatMoney } from '../../lib/format'
import type { Category, PageResponse, Product } from '../../types'

export function AdminProductsPage() {
  const [page, setPage] = useState<PageResponse<Product> | null>(null)

  function load(p = 0) {
    adminProducts({ page: p, size: 12 }).then(setPage)
  }

  useEffect(() => {
    load()
  }, [])

  return (
    <div>
      <div className="flex items-center justify-between">
        <h1 className="font-display text-4xl">Products</h1>
        <Link to="/admin/products/new" className="bg-ink px-4 py-2 text-sm text-paper">
          Add product
        </Link>
      </div>
      <div className="mt-6 overflow-x-auto">
        <table className="w-full text-left text-sm">
          <thead>
            <tr className="border-b border-line">
              <th className="py-2">Name</th>
              <th>Price</th>
              <th>Stock</th>
              <th>Status</th>
              <th />
            </tr>
          </thead>
          <tbody>
            {page?.content.map((product) => (
              <tr key={product.id} className="border-b border-line">
                <td className="py-3">{product.name}</td>
                <td>{formatMoney(product.effectivePrice)}</td>
                <td>{product.stockQuantity}</td>
                <td>{product.status}</td>
                <td className="space-x-3">
                  <Link to={`/admin/products/${product.id}`} className="underline">
                    Edit
                  </Link>
                  <button
                    type="button"
                    className="underline"
                    onClick={async () => {
                      await adminDeactivateProduct(product.id)
                      load(page.page)
                    }}
                  >
                    Deactivate
                  </button>
                </td>
              </tr>
            ))}
          </tbody>
        </table>
      </div>
      <Pagination page={page?.page || 0} totalPages={page?.totalPages || 0} onPage={(p) => load(p)} />
    </div>
  )
}

export function AdminProductFormPage() {
  const { productId } = useParams()
  const navigate = useNavigate()
  const [categories, setCategories] = useState<Category[]>([])
  const [error, setError] = useState('')
  const [file, setFile] = useState<File | null>(null)
  const [form, setForm] = useState({
    name: '',
    slug: '',
    description: '',
    price: '',
    salePrice: '',
    stockQuantity: '0',
    categoryId: '',
    status: 'ACTIVE',
  })

  useEffect(() => {
    adminCategories().then(setCategories)
    if (productId) {
      adminGetProduct(Number(productId)).then((product) => {
        setForm({
          name: product.name,
          slug: product.slug,
          description: product.description || '',
          price: String(product.price),
          salePrice: product.salePrice ? String(product.salePrice) : '',
          stockQuantity: String(product.stockQuantity),
          categoryId: String(product.category.id),
          status: product.status,
        })
      })
    }
  }, [productId])

  async function onSubmit(event: FormEvent) {
    event.preventDefault()
    setError('')
    try {
      const saved = await adminSaveProduct(
        {
          ...form,
          price: Number(form.price),
          salePrice: form.salePrice ? Number(form.salePrice) : null,
          stockQuantity: Number(form.stockQuantity),
          categoryId: Number(form.categoryId),
        },
        productId ? Number(productId) : undefined,
      )
      if (file) {
        await adminUploadImage(saved.id, file, true)
      }
      navigate('/admin/products')
    } catch (err) {
      setError(getErrorMessage(err))
    }
  }

  return (
    <form onSubmit={(e) => void onSubmit(e)} className="grid max-w-xl gap-3">
      <h1 className="font-display text-4xl">{productId ? 'Edit product' : 'Add product'}</h1>
      <input className="border border-line px-3 py-2" placeholder="Name" value={form.name} onChange={(e) => setForm({ ...form, name: e.target.value })} />
      <input className="border border-line px-3 py-2" placeholder="Slug (optional)" value={form.slug} onChange={(e) => setForm({ ...form, slug: e.target.value })} />
      <textarea className="border border-line px-3 py-2" placeholder="Description" value={form.description} onChange={(e) => setForm({ ...form, description: e.target.value })} />
      <input className="border border-line px-3 py-2" placeholder="Price" value={form.price} onChange={(e) => setForm({ ...form, price: e.target.value })} />
      <input className="border border-line px-3 py-2" placeholder="Sale price" value={form.salePrice} onChange={(e) => setForm({ ...form, salePrice: e.target.value })} />
      <input className="border border-line px-3 py-2" placeholder="Stock" value={form.stockQuantity} onChange={(e) => setForm({ ...form, stockQuantity: e.target.value })} />
      <select className="border border-line px-3 py-2" value={form.categoryId} onChange={(e) => setForm({ ...form, categoryId: e.target.value })}>
        <option value="">Category</option>
        {categories.map((category) => (
          <option key={category.id} value={category.id}>
            {category.name}
          </option>
        ))}
      </select>
      <select className="border border-line px-3 py-2" value={form.status} onChange={(e) => setForm({ ...form, status: e.target.value })}>
        <option>ACTIVE</option>
        <option>INACTIVE</option>
        <option>OUT_OF_STOCK</option>
      </select>
      <input type="file" accept="image/*" onChange={(e) => setFile(e.target.files?.[0] || null)} />
      {error && <p className="text-sm text-copper">{error}</p>}
      <button className="bg-ink py-2 text-paper">Save</button>
    </form>
  )
}
