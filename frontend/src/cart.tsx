import { createContext, useContext, useEffect, useMemo, useState, type ReactNode } from 'react'
import { addToCart, getCart, removeCartItem, updateCartItem } from './api/shop'
import { useAuth } from './auth'
import type { Cart } from './types'

const emptyCart: Cart = { id: 0, items: [], itemCount: 0, subtotal: 0, discount: 0, total: 0 }

interface CartState {
  cart: Cart
  loading: boolean
  refresh: () => Promise<void>
  add: (productId: number, quantity?: number) => Promise<void>
  update: (itemId: number, quantity: number) => Promise<void>
  remove: (itemId: number) => Promise<void>
}

const CartContext = createContext<CartState | undefined>(undefined)

export function CartProvider({ children }: { children: ReactNode }) {
  const { user } = useAuth()
  const [cart, setCart] = useState<Cart>(emptyCart)
  const [loading, setLoading] = useState(false)

  async function refresh() {
    if (!user) {
      setCart(emptyCart)
      return
    }
    setLoading(true)
    try {
      setCart(await getCart())
    } catch {
      setCart(emptyCart)
    } finally {
      setLoading(false)
    }
  }

  useEffect(() => {
    void refresh()
  }, [user?.id])

  const value = useMemo<CartState>(
    () => ({
      cart,
      loading,
      refresh,
      async add(productId, quantity = 1) {
        setCart(await addToCart(productId, quantity))
      },
      async update(itemId, quantity) {
        setCart(await updateCartItem(itemId, quantity))
      },
      async remove(itemId) {
        setCart(await removeCartItem(itemId))
      },
    }),
    [cart, loading],
  )

  return <CartContext.Provider value={value}>{children}</CartContext.Provider>
}

export function useCart() {
  const ctx = useContext(CartContext)
  if (!ctx) throw new Error('useCart must be used within CartProvider')
  return ctx
}
