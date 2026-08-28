import { useEffect, useState } from 'react'
import { useParams } from 'react-router-dom'
import { getCategory } from '../api/shop'
import { ProductListView } from './ProductListView'
import type { Category } from '../types'

export function CategoryPage() {
  const { categorySlug } = useParams()
  const [category, setCategory] = useState<Category | null>(null)

  useEffect(() => {
    if (!categorySlug) return
    getCategory(categorySlug).then(setCategory).catch(() => setCategory(null))
  }, [categorySlug])

  return (
    <ProductListView
      title={category?.name || categorySlug || 'Category'}
      categorySlug={categorySlug}
    />
  )
}
