import { type FormEvent, type ReactNode, useEffect, useState } from 'react'
import { Link, NavLink, useNavigate } from 'react-router-dom'
import { listCategories } from '../api/shop'
import { useAuth } from '../auth'
import { useCart } from '../cart'
import type { Category } from '../types'

export function Header() {
  const { user, logout } = useAuth()
  const { cart } = useCart()
  const navigate = useNavigate()
  const [query, setQuery] = useState('')
  const [open, setOpen] = useState(false)
  const [categories, setCategories] = useState<Category[]>([])

  useEffect(() => {
    listCategories().then(setCategories).catch(() => setCategories([]))
  }, [])

  function onSearch(event: FormEvent) {
    event.preventDefault()
    const q = query.trim()
    if (!q) return
    navigate(`/search?q=${encodeURIComponent(q)}`)
    setOpen(false)
  }

  return (
    <header className="sticky top-0 z-40 border-b border-line bg-paper/95 backdrop-blur">
      <div className="mx-auto flex max-w-6xl items-center justify-between gap-4 px-4 py-3 md:px-6">
        <button type="button" className="md:hidden" onClick={() => setOpen((v) => !v)} aria-label="Menu">
          <span className="block h-0.5 w-6 bg-ink" />
          <span className="mt-1.5 block h-0.5 w-6 bg-ink" />
        </button>
        <Link to="/" className="font-display text-2xl tracking-tight">
          Nineteens
        </Link>
        <form onSubmit={onSearch} className="hidden flex-1 md:flex">
          <input
            value={query}
            onChange={(e) => setQuery(e.target.value)}
            placeholder="Search shirts, coats, bags…"
            className="w-full border border-line bg-white px-3 py-2 text-sm outline-none focus:border-copper"
          />
        </form>
        <nav className="flex items-center gap-4 text-sm">
          {user?.role === 'ADMIN' && (
            <Link to="/admin" className="hidden text-copper md:inline">
              Studio
            </Link>
          )}
          {user ? (
            <>
              <Link to="/account" className="hidden md:inline">
                {user.firstName}
              </Link>
              <button type="button" onClick={() => void logout()} className="hidden md:inline">
                Logout
              </button>
            </>
          ) : (
            <Link to="/login" className="hidden md:inline">
              Login
            </Link>
            <Link to="/register" className="hidden md:inline">
              Register
            </Link>
          )}
          <Link to="/cart" className="relative">
            Cart
            {cart.itemCount > 0 && (
              <span className="absolute -right-3 -top-2 bg-copper px-1.5 text-[10px] text-white">{cart.itemCount}</span>
            )}
          </Link>
        </nav>
      </div>
      <div className="hidden border-t border-line md:block">
        <div className="mx-auto flex max-w-6xl gap-6 px-6 py-2 text-sm">
          {categories.map((category) => (
            <NavLink
              key={category.id}
              to={`/category/${category.slug}`}
              className={({ isActive }) => (isActive ? 'text-copper' : 'text-ink hover:text-copper')}
            >
              {category.name}
            </NavLink>
          ))}
          <NavLink to="/products" className={({ isActive }) => (isActive ? 'text-copper' : 'hover:text-copper')}>
            All products
          </NavLink>
          <NavLink to="/offers" className={({ isActive }) => (isActive ? 'text-copper' : 'hover:text-copper')}>
            Offers
          </NavLink>
        </div>
      </div>
      {open && (
        <div className="border-t border-line bg-paper px-4 py-4 md:hidden">
          <form onSubmit={onSearch} className="mb-4">
            <input
              value={query}
              onChange={(e) => setQuery(e.target.value)}
              placeholder="Search…"
              className="w-full border border-line bg-white px-3 py-2 text-sm"
            />
          </form>
          <div className="grid gap-3 text-sm">
            {categories.map((category) => (
              <Link key={category.id} to={`/category/${category.slug}`} onClick={() => setOpen(false)}>
                {category.name}
              </Link>
            ))}
            <Link to="/products" onClick={() => setOpen(false)}>
              All products
            </Link>
            <Link to="/offers" onClick={() => setOpen(false)}>
              Offers
            </Link>
            {user ? (
              <>
                <Link to="/account" onClick={() => setOpen(false)}>
                  Account
                </Link>
                {user.role === 'ADMIN' && (
                  <Link to="/admin" onClick={() => setOpen(false)}>
                    Studio
                  </Link>
                )}
                <button
                  type="button"
                  onClick={() => {
                    setOpen(false)
                    void logout()
                  }}
                >
                  Logout
                </button>
              </>
            ) : (
              <>
                <Link to="/login" onClick={() => setOpen(false)}>
                  Login
                </Link>
                <Link to="/register" onClick={() => setOpen(false)}>
                  Register
                </Link>
              </>
            )}
          </div>
        </div>
      )}
    </header>
  )
}

export function Footer() {
  return (
    <footer className="mt-20 border-t border-line bg-ink text-paper">
      <div className="mx-auto grid max-w-6xl gap-8 px-4 py-12 md:grid-cols-4 md:px-6">
        <div>
          <p className="font-display text-2xl">Nineteens</p>
          <p className="mt-3 max-w-xs text-sm text-paper/70">
            Contemporary essentials from Dhaka. Cloth with a quiet life, priced in taka, delivered cash on delivery.
          </p>
        </div>
        <div>
          <p className="text-xs uppercase tracking-[0.2em] text-copper">Shop</p>
          <div className="mt-3 grid gap-2 text-sm">
            <Link to="/products">All products</Link>
            <Link to="/offers">Current offers</Link>
            <Link to="/category/women">Women</Link>
            <Link to="/category/men">Men</Link>
          </div>
        </div>
        <div>
          <p className="text-xs uppercase tracking-[0.2em] text-copper">Help</p>
          <div className="mt-3 grid gap-2 text-sm text-paper/80">
            <p>Cash on delivery across Bangladesh</p>
            <p>Returns within 7 days</p>
            <p>support@nineteens.com</p>
          </div>
        </div>
        <div>
          <p className="text-xs uppercase tracking-[0.2em] text-copper">Hours</p>
          <p className="mt-3 text-sm text-paper/80">Studio desk: Sat–Thu, 11:00–19:00</p>
        </div>
      </div>
    </footer>
  )
}

export function Layout({ children }: { children: ReactNode }) {
  return (
    <div className="flex min-h-screen flex-col">
      <Header />
      <main className="flex-1">{children}</main>
      <Footer />
    </div>
  )
}
