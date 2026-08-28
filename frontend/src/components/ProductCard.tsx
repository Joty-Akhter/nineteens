import { useState } from 'react'
import { Link, useNavigate } from 'react-router-dom'
import { getErrorMessage } from '../api/client'
import { useAuth } from '../auth'
import { useCart } from '../cart'
import { formatMoney, primaryImage } from '../lib/format'
import type { Product } from '../types'

export function ProductCard({ product }: { product: Product }) {
  const image = primaryImage(product.images)
  const { user } = useAuth()
  const { add } = useCart()
  const navigate = useNavigate()
  const [message, setMessage] = useState('')

  async function addItem() {
    if (!user) {
      navigate('/login')
      return
    }
    try {
      await add(product.id, 1)
      setMessage('Added')
    } catch (error) {
      setMessage(getErrorMessage(error, 'Could not add'))
    }
  }

  return (
    <article className="group flex flex-col">
      <Link to={`/products/${product.id}`} className="relative block overflow-hidden bg-paper-2">
        {image ? (
          <img
            src={image}
            alt={product.name}
            className="aspect-[4/5] w-full object-cover transition duration-500 group-hover:scale-[1.03]"
          />
        ) : (
          <div className="aspect-[4/5] bg-paper-2" />
        )}
        {product.discountPercent > 0 && (
          <span className="absolute left-3 top-3 bg-copper px-2 py-1 text-xs font-medium tracking-wide text-white">
            −{product.discountPercent}%
          </span>
        )}
        {!product.inStock && (
          <span className="absolute right-3 top-3 bg-ink/80 px-2 py-1 text-xs text-white">Sold out</span>
        )}
      </Link>
      <div className="mt-3 flex flex-1 flex-col">
        <p className="text-xs uppercase tracking-[0.18em] text-muted">{product.category.name}</p>
        <Link to={`/products/${product.id}`} className="mt-1 font-display text-lg leading-snug">
          {product.name}
        </Link>
        <div className="mt-2 flex items-baseline gap-2">
          <span className="font-medium">{formatMoney(product.effectivePrice)}</span>
          {product.discountPercent > 0 && (
            <span className="text-sm text-muted line-through">{formatMoney(product.price)}</span>
          )}
        </div>
        <div className="mt-3 flex items-center gap-3 text-sm">
          <button
            type="button"
            disabled={!product.inStock}
            onClick={() => void addItem()}
            className="border border-ink px-3 py-1.5 disabled:opacity-40"
          >
            Add to cart
          </button>
          <Link to={`/products/${product.id}`} className="underline underline-offset-4">
            Details
          </Link>
        </div>
        {message && <p className="mt-1 text-xs text-copper">{message}</p>}
      </div>
    </article>
  )
}
