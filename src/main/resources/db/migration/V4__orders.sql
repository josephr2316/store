CREATE TABLE orders (
    id BIGSERIAL PRIMARY KEY,
    external_id VARCHAR(100),
    channel VARCHAR(50) NOT NULL DEFAULT 'OTHER',
    status VARCHAR(50) NOT NULL DEFAULT 'PENDING',
    customer_name VARCHAR(500),
    customer_phone VARCHAR(50),
    customer_email VARCHAR(255),
    shipping_address TEXT,
    shipping_city VARCHAR(255),
    shipping_region VARCHAR(255),
    shipping_postal_code VARCHAR(50),
    total_amount DECIMAL(19,4) NOT NULL DEFAULT 0,
    currency VARCHAR(3) DEFAULT 'USD',
    notes TEXT,
    created_at TIMESTAMPTZ NOT NULL DEFAULT now(),
    updated_at TIMESTAMPTZ,
    created_by VARCHAR(255),
    updated_by VARCHAR(255)
);

CREATE TABLE order_items (
    id BIGSERIAL PRIMARY KEY,
    order_id BIGINT NOT NULL REFERENCES orders(id) ON DELETE CASCADE,
    variant_id BIGINT NOT NULL REFERENCES variants(id),
    quantity INT NOT NULL CHECK (quantity > 0),
    unit_price DECIMAL(19,4) NOT NULL,
    created_at TIMESTAMPTZ NOT NULL DEFAULT now(),
    updated_at TIMESTAMPTZ,
    created_by VARCHAR(255),
    updated_by VARCHAR(255)
);

CREATE TABLE order_status_history (
    id BIGSERIAL PRIMARY KEY,
    order_id BIGINT NOT NULL REFERENCES orders(id) ON DELETE CASCADE,
    from_status VARCHAR(50),
    to_status VARCHAR(50) NOT NULL,
    reason TEXT,
    changed_by VARCHAR(255),
    changed_at TIMESTAMPTZ NOT NULL DEFAULT now()
);

CREATE INDEX idx_orders_status ON orders(status);
CREATE INDEX idx_orders_channel ON orders(channel);
CREATE INDEX idx_order_items_order ON order_items(order_id);
CREATE INDEX idx_order_status_history_order ON order_status_history(order_id);
