import { useEffect, useState } from 'react'
import { Link, useNavigate, useParams } from 'react-router-dom'
import { getProduct, getRelated } from '../api/shop'
import { getErrorMessage } from '../api/client'
import { useAuth } from '../auth'
import { useCart } from '../cart'
import { ProductGrid } from '../components/ProductGrid'
import { formatMoney, primaryImage } from '../lib/format'
import type { Product } from '../types'

export function ProductDetailPage() {
  const { productId } = useParams()
  const { user } = useAuth()
  const { add } = useCart()
  const navigate = useNavigate()
  const [product, setProduct] = useState<Product | null>(null)
  const [related, setRelated] = useState<Product[]>([])
  const [qty, setQty] = useState(1)
  const [active, setActive] = useState('')
  const [message, setMessage] = useState('')

  useEffect(() => {
    const id = Number(productId)
    if (!id) return
    getProduct(id).then((item) => {
      setProduct(item)
      setActive(primaryImage(item.images))
    })
    getRelated(id).then(setRelated).catch(() => setRelated([]))
  }, [productId])

  if (!product) return <p className="px-6 py-20 text-center text-muted">Loading product…</p>

  const current = product

  async function addItem() {
    if (!user) {
      navigate('/login')
      return
    }
    try {
      await add(current.id, qty)
      setMessage('Added to cart')
    } catch (error) {
      setMessage(getErrorMessage(error, 'Could not add to cart'))
    }
  }

  return (
    <div className="mx-auto max-w-6xl px-4 py-10 md:px-6">
      <div className="grid gap-10 lg:grid-cols-2">
        <div>
          <img src={active} alt={product.name} className="aspect-[4/5] w-full object-cover bg-paper-2" />
          {product.images.length > 1 && (
            <div className="mt-3 grid grid-cols-4 gap-2">
              {product.images.map((image) => (
                <button key={image.id} type="button" onClick={() => setActive(image.url)}>
                  <img src={image.url} alt="" className="aspect-square object-cover" />
                </button>
              ))}
            </div>
          )}
        </div>
        <div>
          <p className="text-xs uppercase tracking-[0.2em] text-muted">
            <Link to={`/category/${product.category.slug}`}>{product.category.name}</Link>
          </p>
          <h1 className="mt-2 font-display text-4xl">{product.name}</h1>
          <div className="mt-4 flex items-baseline gap-3">
            <span className="text-2xl">{formatMoney(product.effectivePrice)}</span>
            {product.discountPercent > 0 && (
              <>
                <span className="text-muted line-through">{formatMoney(product.price)}</span>
                <span className="text-copper">−{product.discountPercent}%</span>
              </>
            )}
          </div>
          {product.appliedOfferName && <p className="mt-2 text-sm text-copper">{product.appliedOfferName} applied</p>}
          <p className="mt-6 max-w-lg leading-relaxed text-muted">{product.description}</p>
          <p className="mt-4 text-sm">
            {product.inStock ? `${product.stockQuantity} in stock` : 'Currently out of stock'}
          </p>
          <div className="mt-6 flex items-center gap-3">
            <input
              type="number"
              min={1}
              max={product.stockQuantity}
              value={qty}
              onChange={(e) => setQty(Math.max(1, Number(e.target.value)))}
              className="w-20 border border-line px-2 py-2"
            />
            <button
              type="button"
              disabled={!product.inStock}
              onClick={() => void addItem()}
              className="bg-ink px-6 py-3 text-sm text-paper disabled:opacity-40"
            >
              Add to cart
            </button>
          </div>
          {message && <p className="mt-3 text-sm text-copper">{message}</p>}
        </div>
      </div>
      {related.length > 0 && (
        <section className="mt-16">
          <h2 className="mb-6 font-display text-3xl">Related products</h2>
          <ProductGrid products={related} />
        </section>
      )}
    </div>
  )
}
