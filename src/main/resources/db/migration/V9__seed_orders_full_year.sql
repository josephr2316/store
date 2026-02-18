-- DELIVERED orders with created_at spread over the last 365 days for "last year" reports.
-- PostgreSQL

INSERT INTO orders (id, channel, status, customer_name, customer_phone, total_amount, currency, created_at, updated_at)
SELECT
  5160 + g,
  (ARRAY['WHATSAPP', 'INSTAGRAM', 'DIRECT', 'OTHER'])[1 + (g % 4)],
  'DELIVERED',
  'Cliente Año ' || (5160 + g),
  '809' || lpad((g + 300000)::text, 6, '0'),
  (200 + (random() * 1800))::decimal(19,4),
  'USD',
  (current_date - g)::timestamp,
  (current_date - g)::timestamp
FROM generate_series(0, 364) AS g
ON CONFLICT (id) DO NOTHING;

INSERT INTO order_items (id, order_id, variant_id, quantity, unit_price, created_at, updated_at)
SELECT
  6320 + g * 2 + i,
  5160 + g,
  2000 + ((g * 7 + i * 3) % 140),
  (1 + (random() * 4))::int,
  (60 + random() * 350)::decimal(19,4),
  (current_date - g)::timestamp,
  (current_date - g)::timestamp
FROM generate_series(0, 364) AS g, generate_series(0, 1) AS i
ON CONFLICT (id) DO NOTHING;

INSERT INTO order_status_history (id, order_id, from_status, to_status, changed_at)
SELECT
  7160 + g,
  5160 + g,
  NULL,
  'DELIVERED',
  (current_date - g)::timestamp
FROM generate_series(0, 364) AS g
ON CONFLICT (id) DO NOTHING;

UPDATE orders o
SET total_amount = COALESCE(
  (SELECT SUM(quantity * unit_price) FROM order_items WHERE order_id = o.id),
  0
)
WHERE o.id BETWEEN 5160 AND 5524;

SELECT setval(pg_get_serial_sequence('orders', 'id'), (SELECT MAX(id) FROM orders));
SELECT setval(pg_get_serial_sequence('order_items', 'id'), (SELECT MAX(id) FROM order_items));
SELECT setval(pg_get_serial_sequence('order_status_history', 'id'), (SELECT MAX(id) FROM order_status_history));
