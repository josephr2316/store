-- V10: Refresh seed DELIVERED orders with dates relative to TODAY.
-- Deletes old V7/V8/V9 seed data and re-inserts with current_date so reports
-- show data for today, past days, months, and last year.
-- PostgreSQL

-- ─── Cleanup old seed ranges ──────────────────────────────────────────────────
DELETE FROM order_status_history WHERE order_id BETWEEN 5000 AND 5999;
DELETE FROM order_items          WHERE order_id BETWEEN 5000 AND 5999;
DELETE FROM orders               WHERE id       BETWEEN 5000 AND 5999;

-- ─── 365 DELIVERED orders — one per day for last year ─────────────────────────
-- variant_id cycles through the first 140 variants actually in the DB.
INSERT INTO orders (
    id, channel, status,
    customer_name, customer_phone,
    total_amount, currency,
    created_at, updated_at
)
SELECT
    5000 + g,
    (ARRAY['WHATSAPP','INSTAGRAM','DIRECT','OTHER'])[1 + (g % 4)],
    'DELIVERED',
    'Cliente ' || (5000 + g),
    '809' || lpad((g + 100000)::text, 6, '0'),
    (400 + (random() * 2800))::decimal(19,4),
    'USD',
    (now() - (g || ' days')::interval),
    (now() - (g || ' days')::interval)
FROM generate_series(0, 364) AS g
ON CONFLICT (id) DO UPDATE
    SET created_at   = (now() - (EXCLUDED.id - 5000 || ' days')::interval),
        updated_at   = (now() - (EXCLUDED.id - 5000 || ' days')::interval),
        status       = 'DELIVERED',
        total_amount = EXCLUDED.total_amount;

-- ─── 2 items per order (use actual variant IDs from DB) ───────────────────────
INSERT INTO order_items (
    id, order_id, variant_id,
    quantity, unit_price,
    created_at, updated_at
)
SELECT
    6000 + g * 2 + i,
    5000 + g,
    COALESCE(
        (SELECT id FROM variants ORDER BY id OFFSET ((g * 7 + i * 3) % GREATEST(1,(SELECT COUNT(*) FROM variants)::int)) LIMIT 1),
        (SELECT MIN(id) FROM variants)
    ),
    (1 + (random() * 4))::int,
    (80 + random() * 400)::decimal(19,4),
    (now() - (g || ' days')::interval),
    (now() - (g || ' days')::interval)
FROM generate_series(0, 364) AS g, generate_series(0, 1) AS i
ON CONFLICT (id) DO NOTHING;

-- ─── Status history for every order ──────────────────────────────────────────
INSERT INTO order_status_history (id, order_id, from_status, to_status, reason, changed_at)
SELECT
    7000 + g,
    5000 + g,
    NULL,
    'DELIVERED',
    'Seed data',
    (now() - (g || ' days')::interval)
FROM generate_series(0, 364) AS g
ON CONFLICT (id) DO UPDATE
    SET changed_at = (now() - (EXCLUDED.id - 7000 || ' days')::interval);

-- ─── Extra DELIVERED orders within the SAME day (today) for daily chart ───────
INSERT INTO orders (
    id, channel, status,
    customer_name, customer_phone,
    total_amount, currency,
    created_at, updated_at
)
SELECT
    5400 + g,
    (ARRAY['WHATSAPP','INSTAGRAM','DIRECT','OTHER'])[1 + (g % 4)],
    'DELIVERED',
    'Cliente Hoy ' || (5400 + g),
    '809' || lpad((g + 500000)::text, 6, '0'),
    (300 + (random() * 1500))::decimal(19,4),
    'USD',
    (now() - (g * 2 || ' hours')::interval),
    (now() - (g * 2 || ' hours')::interval)
FROM generate_series(0, 4) AS g
ON CONFLICT (id) DO UPDATE
    SET created_at   = (now() - ((EXCLUDED.id - 5400) * 2 || ' hours')::interval),
        updated_at   = (now() - ((EXCLUDED.id - 5400) * 2 || ' hours')::interval),
        status       = 'DELIVERED';

-- ─── Items for today's extra orders ──────────────────────────────────────────
INSERT INTO order_items (id, order_id, variant_id, quantity, unit_price, created_at, updated_at)
SELECT
    6750 + g * 2 + i,
    5400 + g,
    COALESCE(
        (SELECT id FROM variants ORDER BY id OFFSET ((g * 5 + i) % GREATEST(1,(SELECT COUNT(*) FROM variants)::int)) LIMIT 1),
        (SELECT MIN(id) FROM variants)
    ),
    (1 + (random() * 3))::int,
    (100 + random() * 500)::decimal(19,4),
    now(), now()
FROM generate_series(0, 4) AS g, generate_series(0, 1) AS i
ON CONFLICT (id) DO NOTHING;

-- Status history for today's extras
INSERT INTO order_status_history (id, order_id, from_status, to_status, reason, changed_at)
SELECT 7400 + g, 5400 + g, NULL, 'DELIVERED', 'Seed today', now()
FROM generate_series(0, 4) AS g
ON CONFLICT (id) DO UPDATE SET changed_at = now();

-- ─── PENDING / CONFIRMED orders for realistic UI (not DELIVERED) ─────────────
INSERT INTO orders (
    id, channel, status,
    customer_name, customer_phone,
    shipping_address, shipping_city,
    total_amount, currency,
    created_at, updated_at
)
VALUES
    (5500,'WHATSAPP','PENDING',   'Juan Pérez',   '8095550001','Calle 1 #12, Naco','Santo Domingo', 1250.00,'USD', now() - interval '2 days', now()),
    (5501,'INSTAGRAM','CONFIRMED', 'María López',  '8095550002','Av. Winston Churchill 15','Santiago',  2100.00,'USD', now() - interval '1 day',  now()),
    (5502,'DIRECT',  'PREPARING', 'Carlos García', '8095550003','C/El Conde 45','La Romana',    3000.00,'USD', now() - interval '12 hours',now()),
    (5503,'WHATSAPP','SHIPPED',   'Ana Torres',   '8095550004','Av. 27 de Febrero 200','Santo Domingo', 4500.00,'USD', now() - interval '3 days', now()),
    (5504,'OTHER',   'CANCELLED', 'Luis Martínez','8095550005',NULL,NULL,               950.00,'USD', now() - interval '5 days', now())
ON CONFLICT (id) DO NOTHING;

INSERT INTO order_items (id, order_id, variant_id, quantity, unit_price, created_at, updated_at)
SELECT
    6800 + i,
    5500 + (i / 2),
    COALESCE(
        (SELECT id FROM variants ORDER BY id OFFSET (i % GREATEST(1,(SELECT COUNT(*) FROM variants)::int)) LIMIT 1),
        (SELECT MIN(id) FROM variants)
    ),
    2, (200 + i * 50)::decimal(19,4),
    now(), now()
FROM generate_series(0, 9) AS i
ON CONFLICT (id) DO NOTHING;

-- History for the non-delivered seed orders
INSERT INTO order_status_history (id, order_id, from_status, to_status, reason, changed_at)
VALUES
    (7500, 5500, NULL,        'PENDING',   'Order created', now() - interval '2 days'),
    (7501, 5501, NULL,        'PENDING',   'Order created', now() - interval '1 day'),
    (7502, 5501, 'PENDING',   'CONFIRMED', 'Confirmed',     now() - interval '23 hours'),
    (7503, 5502, NULL,        'PENDING',   'Order created', now() - interval '12 hours'),
    (7504, 5502, 'PENDING',   'CONFIRMED', 'Confirmed',     now() - interval '11 hours'),
    (7505, 5502, 'CONFIRMED', 'PREPARING', 'Preparing',     now() - interval '10 hours'),
    (7506, 5503, NULL,        'PENDING',   'Order created', now() - interval '3 days'),
    (7507, 5503, 'PENDING',   'CONFIRMED', 'Confirmed',     now() - interval '2 days' + interval '2 hours'),
    (7508, 5503, 'CONFIRMED', 'PREPARING', 'Preparing',     now() - interval '2 days' + interval '4 hours'),
    (7509, 5503, 'PREPARING', 'SHIPPED',   'Shipped',       now() - interval '1 day'),
    (7510, 5504, NULL,        'PENDING',   'Order created', now() - interval '5 days'),
    (7511, 5504, 'PENDING',   'CANCELLED', 'Customer request', now() - interval '4 days')
ON CONFLICT (id) DO NOTHING;

-- ─── Update totals for all seed orders ───────────────────────────────────────
UPDATE orders o
SET total_amount = COALESCE(
    (SELECT SUM(quantity * unit_price) FROM order_items WHERE order_id = o.id), 0
)
WHERE o.id BETWEEN 5000 AND 5999;

-- ─── Reset sequences so new orders don't conflict ────────────────────────────
SELECT setval(pg_get_serial_sequence('orders','id'),               (SELECT MAX(id) FROM orders) + 1);
SELECT setval(pg_get_serial_sequence('order_items','id'),          (SELECT MAX(id) FROM order_items) + 1);
SELECT setval(pg_get_serial_sequence('order_status_history','id'), (SELECT MAX(id) FROM order_status_history) + 1);
