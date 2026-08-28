import { api } from './client'
import type { Address, Category, DashboardStats, Offer, Order, PageResponse, Product, ProductQuery, UserSummary } from '../types'

export function listProducts(params: ProductQuery = {}) {
  return api.get<PageResponse<Product>>('/products', { params }).then((r) => r.data)
}

export function searchProducts(params: ProductQuery) {
  return api.get<PageResponse<Product>>('/search', { params }).then((r) => r.data)
}

export function getProduct(id: number) {
  return api.get<Product>(`/products/${id}`).then((r) => r.data)
}

export function getRelated(id: number) {
  return api.get<Product[]>(`/products/${id}/related`).then((r) => r.data)
}

export function getFeatured() {
  return api.get<Product[]>('/products/featured').then((r) => r.data)
}

export function getNewProducts() {
  return api.get<Product[]>('/products/new').then((r) => r.data)
}

export function getSaleProducts() {
  return api.get<Product[]>('/products/sale').then((r) => r.data)
}

export function listCategories() {
  return api.get<Category[]>('/categories').then((r) => r.data)
}

export function getCategory(slug: string) {
  return api.get<Category>(`/categories/${slug}`).then((r) => r.data)
}

export function getCategoryProducts(slug: string, params: ProductQuery = {}) {
  return api.get<PageResponse<Product>>(`/categories/${slug}/products`, { params }).then((r) => r.data)
}

export function listOffers() {
  return api.get<Offer[]>('/offers').then((r) => r.data)
}

export function getCart() {
  return api.get('/cart').then((r) => r.data)
}

export function addToCart(productId: number, quantity: number) {
  return api.post('/cart/items', { productId, quantity }).then((r) => r.data)
}

export function updateCartItem(id: number, quantity: number) {
  return api.put(`/cart/items/${id}`, { quantity }).then((r) => r.data)
}

export function removeCartItem(id: number) {
  return api.delete(`/cart/items/${id}`).then((r) => r.data)
}

export function clearCart() {
  return api.delete('/cart').then((r) => r.data)
}

export function checkout(payload: {
  recipientName: string
  phone: string
  addressLine: string
  city: string
  postalCode: string
  deliveryNote?: string
  addressId?: number
}) {
  return api.post<Order>('/orders', payload).then((r) => r.data)
}

export function listOrders(page = 0, size = 10) {
  return api.get<PageResponse<Order>>('/orders', { params: { page, size } }).then((r) => r.data)
}

export function getOrder(id: number) {
  return api.get<Order>(`/orders/${id}`).then((r) => r.data)
}

export function listAddresses() {
  return api.get<Address[]>('/profile/addresses').then((r) => r.data)
}

export function createAddress(payload: Omit<Address, 'id'>) {
  return api.post<Address>('/profile/addresses', payload).then((r) => r.data)
}

export function updateAddress(id: number, payload: Omit<Address, 'id'>) {
  return api.put<Address>(`/profile/addresses/${id}`, payload).then((r) => r.data)
}

export function deleteAddress(id: number) {
  return api.delete(`/profile/addresses/${id}`)
}

export function adminDashboard() {
  return api.get<DashboardStats>('/admin/dashboard').then((r) => r.data)
}

export function adminProducts(params: ProductQuery = {}) {
  return api.get<PageResponse<Product>>('/admin/products', { params }).then((r) => r.data)
}

export function adminGetProduct(id: number) {
  return api.get<Product>(`/admin/products/${id}`).then((r) => r.data)
}

export function adminSaveProduct(payload: Record<string, unknown>, id?: number) {
  return id
    ? api.put<Product>(`/admin/products/${id}`, payload).then((r) => r.data)
    : api.post<Product>('/admin/products', payload).then((r) => r.data)
}

export function adminDeactivateProduct(id: number) {
  return api.delete(`/admin/products/${id}`)
}

export function adminUploadImage(id: number, file: File, primary = false) {
  const data = new FormData()
  data.append('file', file)
  data.append('primary', String(primary))
  return api.post<Product>(`/admin/products/${id}/images`, data).then((r) => r.data)
}

export function adminCategories() {
  return api.get<Category[]>('/admin/categories').then((r) => r.data)
}

export function adminSaveCategory(payload: Record<string, unknown>, id?: number) {
  return id
    ? api.put<Category>(`/admin/categories/${id}`, payload).then((r) => r.data)
    : api.post<Category>('/admin/categories', payload).then((r) => r.data)
}

export function adminDeactivateCategory(id: number) {
  return api.delete(`/admin/categories/${id}`)
}

export function adminOffers() {
  return api.get<Offer[]>('/admin/offers').then((r) => r.data)
}

export function adminSaveOffer(payload: Record<string, unknown>, id?: number) {
  return id
    ? api.put<Offer>(`/admin/offers/${id}`, payload).then((r) => r.data)
    : api.post<Offer>('/admin/offers', payload).then((r) => r.data)
}

export function adminDeactivateOffer(id: number) {
  return api.delete(`/admin/offers/${id}`)
}

export function adminOrders(page = 0, size = 12) {
  return api.get<PageResponse<Order>>('/admin/orders', { params: { page, size } }).then((r) => r.data)
}

export function adminGetOrder(id: number) {
  return api.get<Order>(`/admin/orders/${id}`).then((r) => r.data)
}

export function adminUpdateOrderStatus(id: number, status: string) {
  return api.put<Order>(`/admin/orders/${id}/status`, { status }).then((r) => r.data)
}

export function adminUsers() {
  return api.get<UserSummary[]>('/admin/users').then((r) => r.data)
}

export function adminUpdateUserStatus(id: number, status: 'ACTIVE' | 'INACTIVE') {
  return api.put<UserSummary>(`/admin/users/${id}/status`, { status }).then((r) => r.data)
}
