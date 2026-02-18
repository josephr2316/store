-- Seed 100+ rows per table for reports and UI (products, variants, inventory, orders, order_items, order_status_history)

-- 100 products (id 1000-1099)
INSERT INTO products (id, name, description, sku, created_at)
SELECT 1000 + s, 'Producto ' || (1000 + s), 'Descripción del producto ' || (1000 + s), 'PROD-SEED-' || (1000 + s), now()
FROM generate_series(0, 99) AS s
ON CONFLICT (id) DO NOTHING;

-- 120 variants (2 per product for first 60 products; ids 2000-2119)
INSERT INTO variants (id, product_id, name, sku, created_at)
SELECT 2000 + s * 2 + v, 1000 + s, 'Variante ' || (2000 + s * 2 + v), 'VAR-SEED-' || (2000 + s * 2 + v), now()
FROM generate_series(0, 59) s, generate_series(0, 1) v
ON CONFLICT (id) DO NOTHING;

-- 20 more variants for products 1060-1069 (ids 2120-2139) to reach 140 variants total
INSERT INTO variants (id, product_id, name, sku, created_at)
SELECT 2120 + s * 2 + v, 1060 + s, 'Variante B ' || (2120 + s * 2 + v), 'VAR-SEED-B-' || (2120 + s * 2 + v), now()
FROM generate_series(0, 9) s, generate_series(0, 1) v
ON CONFLICT (id) DO NOTHING;

-- inventory_balance: one per variant (2000-2139)
INSERT INTO inventory_balance (variant_id, quantity, reserved, created_at)
SELECT 2000 + s, 5 + (random() * 95)::int, 0, now()
FROM generate_series(0, 139) s
ON CONFLICT (variant_id) DO NOTHING;

-- 100 orders (id 5000-5099); mix statuses, many DELIVERED for reports
INSERT INTO orders (id, channel, status, customer_name, customer_phone, total_amount, currency, created_at)
SELECT
  5000 + s,
  (ARRAY['WHATSAPP', 'INSTAGRAM', 'DIRECT', 'OTHER'])[1 + (s % 4)],
  (ARRAY['PENDING', 'CONFIRMED', 'DELIVERED', 'DELIVERED', 'SHIPPED', 'DELIVERED'])[1 + (s % 6)],
  'Cliente ' || (5000 + s),
  '809' || lpad((s + 100000)::text, 6, '0'),
  (500 + (random() * 2500))::decimal(19,4),
  'USD',
  now() - (s % 90 || ' days')::interval
FROM generate_series(0, 99) AS s
ON CONFLICT (id) DO NOTHING;

-- order_items: 2 items per order = 200 rows (order_id 5000-5099, variant_id from 2000-2139)
INSERT INTO order_items (id, order_id, variant_id, quantity, unit_price, created_at)
SELECT
  6000 + s * 2 + i,
  5000 + s,
  2000 + ((s * 7 + i * 3) % 140),
  (1 + (random() * 4))::int,
  (50 + random() * 450)::decimal(19,4),
  now() - (s % 90 || ' days')::interval
FROM generate_series(0, 99) AS s, generate_series(0, 1) AS i
ON CONFLICT (id) DO NOTHING;

-- order_status_history: 1 row per order (100 rows)
INSERT INTO order_status_history (id, order_id, from_status, to_status, changed_at)
SELECT
  7000 + s,
  5000 + s,
  NULL,
  (SELECT status FROM orders WHERE id = 5000 + s LIMIT 1),
  now() - (s % 90 || ' days')::interval
FROM generate_series(0, 99) AS s
ON CONFLICT (id) DO NOTHING;

-- Update orders.total_amount from order_items sum (so reports show correct totals)
UPDATE orders o
SET total_amount = COALESCE(
  (SELECT SUM(quantity * unit_price) FROM order_items WHERE order_id = o.id),
  0
)
WHERE o.id BETWEEN 5000 AND 5099;
