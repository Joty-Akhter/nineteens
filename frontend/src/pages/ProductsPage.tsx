import { useSearchParams } from 'react-router-dom'
import { ProductListView } from './ProductListView'

export function ProductsPage() {
  return <ProductListView title="All products" />
}

export function SearchPage() {
  const [params] = useSearchParams()
  const q = params.get('q') || ''
  return <ProductListView title={q ? `Search: ${q}` : 'Search'} searchQuery={q} />
}
