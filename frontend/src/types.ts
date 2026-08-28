export type Role = 'USER' | 'ADMIN'
export type UserStatus = 'ACTIVE' | 'INACTIVE'
export type ProductStatus = 'ACTIVE' | 'INACTIVE' | 'OUT_OF_STOCK'
export type CategoryStatus = 'ACTIVE' | 'INACTIVE'
export type OrderStatus = 'PENDING' | 'CONFIRMED' | 'PROCESSING' | 'SHIPPED' | 'DELIVERED' | 'CANCELLED'
export type DiscountType = 'PERCENTAGE' | 'FIXED'
export type OfferStatus = 'ACTIVE' | 'INACTIVE'
export type PaymentProvider = 'CASH_ON_DELIVERY' | 'STRIPE' | 'SSLCOMMERZ' | 'BKASH' | 'NAGAD'
export type PaymentStatus = 'PENDING' | 'COMPLETED' | 'FAILED' | 'REFUNDED'

export interface UserSummary {
  id: number
  email: string
  firstName: string
  lastName: string
  phone?: string | null
  role: Role
  status: string
}

export interface AuthResponse {
  accessToken: string
  tokenType: string
  expiresIn: number
  user: UserSummary
}

export interface PageResponse<T> {
  content: T[]
  page: number
  size: number
  totalElements: number
  totalPages: number
  last: boolean
}

export interface Category {
  id: number
  name: string
  slug: string
  description?: string | null
  imageUrl?: string | null
  status: CategoryStatus
}

export interface ProductImage {
  id: number
  url: string
  sortOrder: number
  primary: boolean
}

export interface Product {
  id: number
  name: string
  slug: string
  description?: string | null
  price: number
  salePrice?: number | null
  effectivePrice: number
  discountPercent: number
  stockQuantity: number
  inStock: boolean
  status: ProductStatus
  category: Category
  images: ProductImage[]
  appliedOfferName?: string | null
  createdAt: string
}

export interface Offer {
  id: number
  name: string
  description?: string | null
  discountType: DiscountType
  discountValue: number
  startAt: string
  endAt: string
  status: OfferStatus
  live: boolean
  productIds: number[]
}

export interface CartItem {
  id: number
  productId: number
  productName: string
  imageUrl?: string | null
  quantity: number
  stockQuantity: number
  unitPrice: number
  lineTotal: number
}

export interface Cart {
  id: number
  items: CartItem[]
  itemCount: number
  subtotal: number
  discount: number
  total: number
}

export interface Address {
  id: number
  recipientName: string
  phone: string
  addressLine: string
  city: string
  postalCode: string
  defaultAddress: boolean
}

export interface OrderItem {
  id: number
  productId?: number | null
  productName: string
  quantity: number
  unitPrice: number
  discount: number
  totalPrice: number
}

export interface Payment {
  provider: PaymentProvider
  status: PaymentStatus
  amount: number
  transactionRef?: string | null
}

export interface Order {
  id: number
  orderNumber: string
  status: OrderStatus
  subtotal: number
  discount: number
  shippingCost: number
  totalAmount: number
  shippingName: string
  shippingPhone: string
  shippingAddress: string
  shippingCity: string
  shippingPostalCode: string
  deliveryNote?: string | null
  items: OrderItem[]
  payment?: Payment | null
  createdAt: string
}

export interface DashboardStats {
  totalUsers: number
  totalOrders: number
  pendingOrders: number
  totalProducts: number
  totalRevenue: number
}

export interface ProductQuery {
  q?: string
  category?: string
  minPrice?: number
  maxPrice?: number
  inStock?: boolean
  onSale?: boolean
  sort?: string
  page?: number
  size?: number
}

export interface ApiError {
  timestamp: string
  status: number
  error: string
  message: string
  path: string
  details: string[]
}
