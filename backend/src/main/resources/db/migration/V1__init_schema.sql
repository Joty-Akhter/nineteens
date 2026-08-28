CREATE TABLE users (
    id              BIGSERIAL PRIMARY KEY,
    email           VARCHAR(255) NOT NULL UNIQUE,
    password        VARCHAR(255) NOT NULL,
    first_name      VARCHAR(100) NOT NULL,
    last_name       VARCHAR(100) NOT NULL,
    phone           VARCHAR(30),
    role            VARCHAR(20)  NOT NULL,
    status          VARCHAR(20)  NOT NULL,
    created_at      TIMESTAMPTZ  NOT NULL DEFAULT NOW(),
    updated_at      TIMESTAMPTZ  NOT NULL DEFAULT NOW()
);

CREATE TABLE addresses (
    id              BIGSERIAL PRIMARY KEY,
    user_id         BIGINT       NOT NULL REFERENCES users (id) ON DELETE CASCADE,
    recipient_name  VARCHAR(150) NOT NULL,
    phone           VARCHAR(30)  NOT NULL,
    address_line    VARCHAR(500) NOT NULL,
    city            VARCHAR(100) NOT NULL,
    postal_code     VARCHAR(20)  NOT NULL,
    is_default      BOOLEAN      NOT NULL DEFAULT FALSE,
    created_at      TIMESTAMPTZ  NOT NULL DEFAULT NOW(),
    updated_at      TIMESTAMPTZ  NOT NULL DEFAULT NOW()
);

CREATE TABLE categories (
    id              BIGSERIAL PRIMARY KEY,
    name            VARCHAR(150) NOT NULL,
    slug            VARCHAR(180) NOT NULL UNIQUE,
    description     TEXT,
    image_url       VARCHAR(500),
    status          VARCHAR(20)  NOT NULL,
    created_at      TIMESTAMPTZ  NOT NULL DEFAULT NOW(),
    updated_at      TIMESTAMPTZ  NOT NULL DEFAULT NOW()
);

CREATE TABLE products (
    id              BIGSERIAL PRIMARY KEY,
    name            VARCHAR(255) NOT NULL,
    slug            VARCHAR(280) NOT NULL UNIQUE,
    description     TEXT,
    price           NUMERIC(12, 2) NOT NULL,
    sale_price      NUMERIC(12, 2),
    stock_quantity  INTEGER      NOT NULL DEFAULT 0,
    category_id     BIGINT       NOT NULL REFERENCES categories (id),
    status          VARCHAR(20)  NOT NULL,
    sold_count      INTEGER      NOT NULL DEFAULT 0,
    created_at      TIMESTAMPTZ  NOT NULL DEFAULT NOW(),
    updated_at      TIMESTAMPTZ  NOT NULL DEFAULT NOW()
);

CREATE INDEX idx_products_category ON products (category_id);
CREATE INDEX idx_products_status ON products (status);
CREATE INDEX idx_products_created_at ON products (created_at DESC);

CREATE TABLE product_images (
    id              BIGSERIAL PRIMARY KEY,
    product_id      BIGINT       NOT NULL REFERENCES products (id) ON DELETE CASCADE,
    url             VARCHAR(500) NOT NULL,
    sort_order      INTEGER      NOT NULL DEFAULT 0,
    is_primary      BOOLEAN      NOT NULL DEFAULT FALSE
);

CREATE INDEX idx_product_images_product ON product_images (product_id);

CREATE TABLE offers (
    id              BIGSERIAL PRIMARY KEY,
    name            VARCHAR(200) NOT NULL,
    description     TEXT,
    discount_type   VARCHAR(20)  NOT NULL,
    discount_value  NUMERIC(12, 2) NOT NULL,
    start_at        TIMESTAMPTZ  NOT NULL,
    end_at          TIMESTAMPTZ  NOT NULL,
    status          VARCHAR(20)  NOT NULL,
    created_at      TIMESTAMPTZ  NOT NULL DEFAULT NOW(),
    updated_at      TIMESTAMPTZ  NOT NULL DEFAULT NOW()
);

CREATE TABLE offer_products (
    offer_id        BIGINT NOT NULL REFERENCES offers (id) ON DELETE CASCADE,
    product_id      BIGINT NOT NULL REFERENCES products (id) ON DELETE CASCADE,
    PRIMARY KEY (offer_id, product_id)
);

CREATE TABLE carts (
    id              BIGSERIAL PRIMARY KEY,
    user_id         BIGINT      NOT NULL UNIQUE REFERENCES users (id) ON DELETE CASCADE,
    created_at      TIMESTAMPTZ NOT NULL DEFAULT NOW(),
    updated_at      TIMESTAMPTZ NOT NULL DEFAULT NOW()
);

CREATE TABLE cart_items (
    id              BIGSERIAL PRIMARY KEY,
    cart_id         BIGINT         NOT NULL REFERENCES carts (id) ON DELETE CASCADE,
    product_id      BIGINT         NOT NULL REFERENCES products (id),
    quantity        INTEGER        NOT NULL,
    unit_price      NUMERIC(12, 2) NOT NULL,
    UNIQUE (cart_id, product_id)
);

CREATE TABLE orders (
    id                    BIGSERIAL PRIMARY KEY,
    order_number          VARCHAR(40)    NOT NULL UNIQUE,
    user_id               BIGINT         NOT NULL REFERENCES users (id),
    subtotal              NUMERIC(12, 2) NOT NULL,
    discount              NUMERIC(12, 2) NOT NULL DEFAULT 0,
    shipping_cost         NUMERIC(12, 2) NOT NULL DEFAULT 0,
    total_amount          NUMERIC(12, 2) NOT NULL,
    status                VARCHAR(20)    NOT NULL,
    shipping_name         VARCHAR(150)   NOT NULL,
    shipping_phone        VARCHAR(30)    NOT NULL,
    shipping_address      VARCHAR(500)   NOT NULL,
    shipping_city         VARCHAR(100)   NOT NULL,
    shipping_postal_code  VARCHAR(20)    NOT NULL,
    delivery_note         VARCHAR(500),
    created_at            TIMESTAMPTZ    NOT NULL DEFAULT NOW(),
    updated_at            TIMESTAMPTZ    NOT NULL DEFAULT NOW()
);

CREATE INDEX idx_orders_user ON orders (user_id);
CREATE INDEX idx_orders_status ON orders (status);
CREATE INDEX idx_orders_created_at ON orders (created_at DESC);

CREATE TABLE order_items (
    id              BIGSERIAL PRIMARY KEY,
    order_id        BIGINT         NOT NULL REFERENCES orders (id) ON DELETE CASCADE,
    product_id      BIGINT         REFERENCES products (id) ON DELETE SET NULL,
    product_name    VARCHAR(255)   NOT NULL,
    quantity        INTEGER        NOT NULL,
    unit_price      NUMERIC(12, 2) NOT NULL,
    discount        NUMERIC(12, 2) NOT NULL DEFAULT 0,
    total_price     NUMERIC(12, 2) NOT NULL
);

CREATE TABLE payments (
    id               BIGSERIAL PRIMARY KEY,
    order_id         BIGINT         NOT NULL UNIQUE REFERENCES orders (id) ON DELETE CASCADE,
    provider         VARCHAR(30)    NOT NULL,
    status           VARCHAR(20)    NOT NULL,
    amount           NUMERIC(12, 2) NOT NULL,
    transaction_ref  VARCHAR(100),
    created_at       TIMESTAMPTZ    NOT NULL DEFAULT NOW(),
    updated_at       TIMESTAMPTZ    NOT NULL DEFAULT NOW()
);
