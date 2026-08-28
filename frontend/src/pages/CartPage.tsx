import { Link } from 'react-router-dom'
import { useCart } from '../cart'
import { formatMoney } from '../lib/format'

export function CartPage() {
  const { cart, update, remove } = useCart()

  if (!cart.items.length) {
    return (
      <div className="mx-auto max-w-3xl px-4 py-20 text-center">
        <h1 className="font-display text-4xl">Your cart is empty</h1>
        <Link to="/products" className="mt-6 inline-block underline">
          Continue shopping
        </Link>
      </div>
    )
  }

  return (
    <div className="mx-auto max-w-5xl px-4 py-10 md:px-6">
      <h1 className="font-display text-4xl">Cart</h1>
      <div className="mt-8 grid gap-10 lg:grid-cols-[1fr_280px]">
        <div className="space-y-6">
          {cart.items.map((item) => (
            <div key={item.id} className="flex gap-4 border-b border-line pb-6">
              {item.imageUrl && <img src={item.imageUrl} alt="" className="h-28 w-24 object-cover" />}
              <div className="flex-1">
                <Link to={`/products/${item.productId}`} className="font-display text-xl">
                  {item.productName}
                </Link>
                <p className="mt-1 text-sm text-muted">{formatMoney(item.unitPrice)} each</p>
                <div className="mt-3 flex items-center gap-3">
                  <input
                    type="number"
                    min={1}
                    max={item.stockQuantity}
                    value={item.quantity}
                    onChange={(e) => void update(item.id, Math.max(1, Number(e.target.value)))}
                    className="w-16 border border-line px-2 py-1"
                  />
                  <button type="button" onClick={() => void remove(item.id)} className="text-sm underline">
                    Remove
                  </button>
                </div>
              </div>
              <p>{formatMoney(item.lineTotal)}</p>
            </div>
          ))}
        </div>
        <aside className="h-fit border border-line bg-white p-5">
          <p className="flex justify-between text-sm">
            <span>Subtotal</span>
            <span>{formatMoney(cart.subtotal)}</span>
          </p>
          <p className="mt-2 flex justify-between text-sm">
            <span>Discount</span>
            <span>−{formatMoney(cart.discount)}</span>
          </p>
          <p className="mt-4 flex justify-between font-medium">
            <span>Total</span>
            <span>{formatMoney(cart.total)}</span>
          </p>
          <Link to="/checkout" className="mt-6 block bg-ink py-3 text-center text-paper">
            Checkout
          </Link>
        </aside>
      </div>
    </div>
  )
}
