import { type FormEvent, useEffect, useState } from 'react'
import { adminCategories, adminDeactivateCategory, adminSaveCategory } from '../../api/shop'
import { getErrorMessage } from '../../api/client'
import type { Category } from '../../types'

export function AdminCategoriesPage() {
  const [categories, setCategories] = useState<Category[]>([])
  const [error, setError] = useState('')
  const [form, setForm] = useState({ name: '', slug: '', description: '', imageUrl: '', status: 'ACTIVE' })
  const [editingId, setEditingId] = useState<number | undefined>()

  function load() {
    adminCategories().then(setCategories)
  }

  useEffect(() => {
    load()
  }, [])

  async function onSubmit(event: FormEvent) {
    event.preventDefault()
    setError('')
    try {
      await adminSaveCategory(form, editingId)
      setForm({ name: '', slug: '', description: '', imageUrl: '', status: 'ACTIVE' })
      setEditingId(undefined)
      load()
    } catch (err) {
      setError(getErrorMessage(err))
    }
  }

  return (
    <div>
      <h1 className="font-display text-4xl">Categories</h1>
      <form onSubmit={(e) => void onSubmit(e)} className="mt-6 grid max-w-xl gap-3">
        <input className="border border-line px-3 py-2" placeholder="Name" value={form.name} onChange={(e) => setForm({ ...form, name: e.target.value })} />
        <input className="border border-line px-3 py-2" placeholder="Slug" value={form.slug} onChange={(e) => setForm({ ...form, slug: e.target.value })} />
        <input className="border border-line px-3 py-2" placeholder="Description" value={form.description} onChange={(e) => setForm({ ...form, description: e.target.value })} />
        <input className="border border-line px-3 py-2" placeholder="Image URL" value={form.imageUrl} onChange={(e) => setForm({ ...form, imageUrl: e.target.value })} />
        <select className="border border-line px-3 py-2" value={form.status} onChange={(e) => setForm({ ...form, status: e.target.value })}>
          <option>ACTIVE</option>
          <option>INACTIVE</option>
        </select>
        {error && <p className="text-sm text-copper">{error}</p>}
        <button className="bg-ink py-2 text-paper">{editingId ? 'Update' : 'Create'}</button>
      </form>
      <div className="mt-8 space-y-3 text-sm">
        {categories.map((category) => (
          <div key={category.id} className="flex items-center justify-between border border-line p-3">
            <span>
              {category.name} · {category.status}
            </span>
            <span className="space-x-3">
              <button
                type="button"
                className="underline"
                onClick={() => {
                  setEditingId(category.id)
                  setForm({
                    name: category.name,
                    slug: category.slug,
                    description: category.description || '',
                    imageUrl: category.imageUrl || '',
                    status: category.status,
                  })
                }}
              >
                Edit
              </button>
              <button
                type="button"
                className="underline"
                onClick={async () => {
                  await adminDeactivateCategory(category.id)
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
