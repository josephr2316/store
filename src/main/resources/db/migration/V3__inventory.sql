CREATE TABLE inventory_balance (
    id BIGSERIAL PRIMARY KEY,
    variant_id BIGINT NOT NULL UNIQUE REFERENCES variants(id) ON DELETE CASCADE,
    quantity INT NOT NULL DEFAULT 0 CHECK (quantity >= 0),
    reserved INT NOT NULL DEFAULT 0 CHECK (reserved >= 0),
    created_at TIMESTAMPTZ NOT NULL DEFAULT now(),
    updated_at TIMESTAMPTZ,
    created_by VARCHAR(255),
    updated_by VARCHAR(255),
    CONSTRAINT chk_reserved_lte_quantity CHECK (reserved <= quantity)
);

CREATE TABLE inventory_adjustment (
    id BIGSERIAL PRIMARY KEY,
    variant_id BIGINT NOT NULL REFERENCES variants(id) ON DELETE CASCADE,
    quantity_delta INT NOT NULL,
    reason VARCHAR(100) NOT NULL,
    note TEXT,
    created_at TIMESTAMPTZ NOT NULL DEFAULT now(),
    created_by VARCHAR(255)
);

CREATE INDEX idx_inventory_balance_variant ON inventory_balance(variant_id);
CREATE INDEX idx_inventory_adjustment_variant ON inventory_adjustment(variant_id);
