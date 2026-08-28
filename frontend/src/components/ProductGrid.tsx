import { ProductCard } from './ProductCard'
import type { Product } from '../types'

export function ProductGrid({ products }: { products: Product[] }) {
  if (!products.length) {
    return <p className="py-16 text-center text-muted">No pieces match these filters yet.</p>
  }
  return (
    <div className="grid grid-cols-2 gap-x-4 gap-y-10 md:grid-cols-3 lg:grid-cols-4">
      {products.map((product) => (
        <ProductCard key={product.id} product={product} />
      ))}
    </div>
  )
}
