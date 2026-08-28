# Nineteens

A full-stack e-commerce shop: contemporary clothing and home goods, priced in BDT, with cash on delivery.

## Stack

- **Frontend:** React, Vite, TypeScript, React Router, Axios, Tailwind CSS
- **Backend:** Java 21, Spring Boot 4, Spring Security, JWT, Spring Data JPA, Bean Validation
- **Database:** PostgreSQL, Flyway migrations, Hibernate

## Quick start

### 1. Database

Start PostgreSQL (Docker Desktop must be running):

```bash
docker compose up -d
```

This creates database `nineteens` with user/password `nineteens`.

### 2. Backend

```bash
cd backend
mvn spring-boot:run
```

API: `http://localhost:8080`

### 3. Frontend

```bash
cd frontend
npm install
npm run dev
```

Storefront: `http://localhost:5173`

## Demo accounts

| Role  | Email                 | Password  |
|-------|-----------------------|-----------|
| Admin | admin@nineteens.com   | Admin@123 |
| User  | user@nineteens.com    | User@123  |

Demo catalog, images, and a live **Monsoon Edit** offer are seeded on first backend start.

## Roles

- **USER** — register/login, browse and search products, cart, checkout (COD), orders, profile, shipping addresses, offers
- **ADMIN** — dashboard, products (including image upload), categories, inventory/prices, offers, orders/status, users activate/deactivate

Admin UI lives at `/admin`. APIs under `/api/admin/**` require `ROLE_ADMIN`.

## Payments

Checkout uses **Cash on Delivery** only. `Payment` + `PaymentProcessor` are the extension points for Stripe, SSLCommerz, bKash, or Nagad later.

Order lines store product name and price snapshots. Checkout always reloads current prices from the database.

## Image storage

Product images are stored on disk (`backend/uploads` by default) through a `StorageService`. Swap the implementation later for S3 without changing admin controllers. Seeded demo images use remote Unsplash URLs.

## API sketch

- `POST /api/auth/register` `POST /api/auth/login`
- `GET /api/products` (filters, sort, pagination) `GET /api/products/{id}` `GET /api/search?q=`
- `GET /api/categories` `GET /api/categories/{slug}/products`
- `GET /api/offers`
- `GET/POST/PUT/DELETE /api/cart` and `/api/cart/items`
- `POST /api/orders` `GET /api/orders` `GET /api/orders/{id}`
- `GET/PUT /api/profile` and `/api/profile/addresses`
- Admin: `/api/admin/dashboard|products|categories|offers|orders|users`
