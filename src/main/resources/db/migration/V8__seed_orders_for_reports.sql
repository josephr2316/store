-- Pedidos DELIVERED con created_at en los últimos 60 días para que los reportes muestren datos al elegir rango de fechas.
-- PostgreSQL

INSERT INTO orders (id, channel, status, customer_name, customer_phone, total_amount, currency, created_at, updated_at)
SELECT
  5100 + g,
  (ARRAY['WHATSAPP', 'INSTAGRAM', 'DIRECT', 'OTHER'])[1 + (g % 4)],
  'DELIVERED',
  'Cliente Reporte ' || (5100 + g),
  '809' || lpad((g + 200000)::text, 6, '0'),
  (300 + (random() * 2000))::decimal(19,4),
  'USD',
  (current_date - g)::timestamp,
  (current_date - g)::timestamp
FROM generate_series(0, 59) AS g
ON CONFLICT (id) DO NOTHING;

INSERT INTO order_items (id, order_id, variant_id, quantity, unit_price, created_at, updated_at)
SELECT
  6200 + g * 2 + i,
  5100 + g,
  2000 + ((g * 7 + i * 3) % 140),
  (1 + (random() * 3))::int,
  (80 + random() * 400)::decimal(19,4),
  (current_date - g)::timestamp,
  (current_date - g)::timestamp
FROM generate_series(0, 59) AS g, generate_series(0, 1) AS i
ON CONFLICT (id) DO NOTHING;

INSERT INTO order_status_history (id, order_id, from_status, to_status, changed_at)
SELECT
  7100 + g,
  5100 + g,
  NULL,
  'DELIVERED',
  (current_date - g)::timestamp
FROM generate_series(0, 59) AS g
ON CONFLICT (id) DO NOTHING;

UPDATE orders o
SET total_amount = COALESCE(
  (SELECT SUM(quantity * unit_price) FROM order_items WHERE order_id = o.id),
  0
)
WHERE o.id BETWEEN 5100 AND 5159;

SELECT setval(pg_get_serial_sequence('orders', 'id'), (SELECT MAX(id) FROM orders));
SELECT setval(pg_get_serial_sequence('order_items', 'id'), (SELECT MAX(id) FROM order_items));
SELECT setval(pg_get_serial_sequence('order_status_history', 'id'), (SELECT MAX(id) FROM order_status_history));
