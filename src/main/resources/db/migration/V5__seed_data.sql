-- Seed data (products, variants, inventory); runs once when migration is applied

INSERT INTO products (id, name, description, sku, created_at)
VALUES
  (100, 'Camiseta Básica', 'Camiseta de algodón unisex', 'PROD-CAM-001', now()),
  (101, 'Pantalón Jeans', 'Pantalón jeans clásico', 'PROD-PAN-001', now()),
  (102, 'Zapatillas Urban', 'Zapatillas casual', 'PROD-ZAP-001', now())
ON CONFLICT (id) DO NOTHING;

INSERT INTO variants (id, product_id, name, sku, created_at)
VALUES
  (200, 100, 'Camiseta S - Negro', 'VAR-CAM-001-S-BLK', now()),
  (201, 100, 'Camiseta M - Blanco', 'VAR-CAM-001-M-WHT', now()),
  (202, 101, 'Jeans 32 - Azul', 'VAR-PAN-001-32', now()),
  (203, 102, 'Zapatillas 42 - Negro', 'VAR-ZAP-001-42', now())
ON CONFLICT (id) DO NOTHING;

INSERT INTO inventory_balance (variant_id, quantity, reserved, created_at)
SELECT v.id, 50, 0, now()
FROM variants v
WHERE v.id IN (200, 201, 202, 203)
ON CONFLICT (variant_id) DO NOTHING;
