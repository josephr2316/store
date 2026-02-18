-- V12: Ensure active orders are always visible in the dashboard.
-- Simple INSERT-only, no window functions, no complex subqueries.
-- Uses ON CONFLICT DO UPDATE so it's safe to run multiple times.
-- PostgreSQL

-- ─── Get first available variant id for order items ───────────────────────────
DO $$
DECLARE
    v1 BIGINT; v2 BIGINT; v3 BIGINT; v4 BIGINT; v5 BIGINT;
BEGIN
    SELECT id INTO v1 FROM variants ORDER BY id OFFSET 0 LIMIT 1;
    SELECT id INTO v2 FROM variants ORDER BY id OFFSET 1 LIMIT 1;
    SELECT id INTO v3 FROM variants ORDER BY id OFFSET 2 LIMIT 1;
    SELECT id INTO v4 FROM variants ORDER BY id OFFSET 3 LIMIT 1;
    SELECT id INTO v5 FROM variants ORDER BY id OFFSET 4 LIMIT 1;
    IF v1 IS NULL THEN RETURN; END IF;
    IF v2 IS NULL THEN v2 := v1; END IF;
    IF v3 IS NULL THEN v3 := v1; END IF;
    IF v4 IS NULL THEN v4 := v1; END IF;
    IF v5 IS NULL THEN v5 := v1; END IF;

    -- ── PENDING orders ────────────────────────────────────────────────────────
    INSERT INTO orders (id, external_id, channel, status, customer_name, customer_phone,
                        shipping_address, shipping_city, shipping_region,
                        total_amount, currency, notes, created_at, updated_at)
    VALUES
        (6001, 'IG-001', 'INSTAGRAM', 'PENDING', 'María García',    '8091110001',
         'Calle El Conde 45, Zona Colonial', 'Santo Domingo', 'Distrito Nacional',
         3500.00, 'USD', 'Primer pedido', now() - interval '5 hours', now()),
        (6002, 'WA-002', 'WHATSAPP',  'PENDING', 'Carlos Rodríguez','8091110002',
         'Av. Winston Churchill 120',        'Santo Domingo', 'Distrito Nacional',
         2800.00, 'USD', NULL,               now() - interval '3 hours', now()),
        (6003, 'IG-003', 'INSTAGRAM', 'PENDING', 'Ana Martínez',    '8091110003',
         'Calle 27 de Febrero 300',          'Santiago',      'Santiago',
         4200.00, 'USD', 'Preguntar color',  now() - interval '2 hours', now()),
        (6004, NULL,     'DIRECT',    'PENDING', 'Luis Pérez',      '8091110004',
         'Av. Abraham Lincoln 55',           'Santo Domingo', 'Distrito Nacional',
         1950.00, 'USD', NULL,               now() - interval '1 hour',  now()),
        (6005, 'WA-005', 'WHATSAPP',  'PENDING', 'Sofía Jiménez',   '8091110005',
         'Av. Luperón 200',                  'Santo Domingo', 'Distrito Nacional',
         5100.00, 'USD', 'Urgente',          now() - interval '30 minutes', now())
    ON CONFLICT (id) DO UPDATE
        SET status = EXCLUDED.status,
            customer_name = EXCLUDED.customer_name,
            updated_at = now();

    -- ── CONFIRMED orders ──────────────────────────────────────────────────────
    INSERT INTO orders (id, external_id, channel, status, customer_name, customer_phone,
                        shipping_address, shipping_city, shipping_region,
                        total_amount, currency, created_at, updated_at)
    VALUES
        (6010, 'WA-010', 'WHATSAPP',  'CONFIRMED', 'Roberto Núñez',  '8091110010',
         'Calle Las Damas 12, Naco',    'Santo Domingo', 'Distrito Nacional',
         6800.00, 'USD', now() - interval '1 day',  now()),
        (6011, 'IG-011', 'INSTAGRAM', 'CONFIRMED', 'Patricia López', '8091110011',
         'Calle del Sol 78, Piantini',  'Santo Domingo', 'Distrito Nacional',
         3200.00, 'USD', now() - interval '18 hours', now()),
        (6012, 'DI-012', 'DIRECT',    'CONFIRMED', 'Javier Santos',  '8091110012',
         'Av. Independencia 450',       'Santiago',      'Santiago',
         4750.00, 'USD', now() - interval '12 hours', now())
    ON CONFLICT (id) DO UPDATE
        SET status = EXCLUDED.status,
            customer_name = EXCLUDED.customer_name,
            updated_at = now();

    -- ── PREPARING orders ──────────────────────────────────────────────────────
    INSERT INTO orders (id, external_id, channel, status, customer_name, customer_phone,
                        shipping_address, shipping_city, shipping_region,
                        total_amount, currency, created_at, updated_at)
    VALUES
        (6020, 'WA-020', 'WHATSAPP',  'PREPARING', 'Carmen Reyes',   '8091110020',
         'Av. 27 de Febrero 180', 'Santo Domingo', 'Distrito Nacional',
         7200.00, 'USD', now() - interval '2 days',  now()),
        (6021, 'IG-021', 'INSTAGRAM', 'PREPARING', 'Andrés Castro',  '8091110021',
         'Calle Beller 34',        'La Romana',     'La Romana',
         2900.00, 'USD', now() - interval '28 hours', now())
    ON CONFLICT (id) DO UPDATE
        SET status = EXCLUDED.status,
            customer_name = EXCLUDED.customer_name,
            updated_at = now();

    -- ── SHIPPED orders ────────────────────────────────────────────────────────
    INSERT INTO orders (id, external_id, channel, status, customer_name, customer_phone,
                        shipping_address, shipping_city, shipping_region,
                        total_amount, currency, created_at, updated_at)
    VALUES
        (6030, 'WA-030', 'WHATSAPP', 'SHIPPED', 'Natalia Gómez',  '8091110030',
         'Calle Hostos 22, Gazcue',   'Santo Domingo', 'Distrito Nacional',
         8500.00, 'USD', now() - interval '3 days', now()),
        (6031, 'DI-031', 'DIRECT',   'SHIPPED', 'Fernando Díaz',  '8091110031',
         'Av. San Martin 99',          'Santiago',      'Santiago',
         3600.00, 'USD', now() - interval '2 days', now())
    ON CONFLICT (id) DO UPDATE
        SET status = EXCLUDED.status,
            customer_name = EXCLUDED.customer_name,
            updated_at = now();

    -- ── Order items (2 per order) ─────────────────────────────────────────────
    INSERT INTO order_items (id, order_id, variant_id, quantity, unit_price, created_at, updated_at) VALUES
        (8001, 6001, v1, 2, 875.00, now(), now()),
        (8002, 6001, v2, 1, 1750.00, now(), now()),
        (8003, 6002, v2, 2, 700.00, now(), now()),
        (8004, 6002, v3, 2, 700.00, now(), now()),
        (8005, 6003, v3, 3, 700.00, now(), now()),
        (8006, 6003, v4, 2, 1050.00, now(), now()),
        (8007, 6004, v4, 1, 975.00, now(), now()),
        (8008, 6004, v5, 2, 487.50, now(), now()),
        (8009, 6005, v1, 2, 1275.00, now(), now()),
        (8010, 6005, v2, 3, 850.00, now(), now()),
        (8011, 6010, v3, 2, 1700.00, now(), now()),
        (8012, 6010, v4, 2, 1700.00, now(), now()),
        (8013, 6011, v5, 4, 400.00, now(), now()),
        (8014, 6011, v1, 2, 800.00, now(), now()),
        (8015, 6012, v2, 3, 791.67, now(), now()),
        (8016, 6012, v3, 2, 1187.50, now(), now()),
        (8017, 6020, v4, 2, 1800.00, now(), now()),
        (8018, 6020, v5, 2, 1800.00, now(), now()),
        (8019, 6021, v1, 2, 725.00, now(), now()),
        (8020, 6021, v2, 3, 483.33, now(), now()),
        (8021, 6030, v3, 2, 2125.00, now(), now()),
        (8022, 6030, v4, 2, 2125.00, now(), now()),
        (8023, 6031, v5, 3, 600.00, now(), now()),
        (8024, 6031, v1, 2, 900.00, now(), now())
    ON CONFLICT (id) DO NOTHING;

    -- ── Status history ────────────────────────────────────────────────────────
    INSERT INTO order_status_history (id, order_id, from_status, to_status, reason, changed_at) VALUES
        -- PENDING
        (9001, 6001, NULL, 'PENDING', 'Pedido creado', now() - interval '5 hours'),
        (9002, 6002, NULL, 'PENDING', 'Pedido creado', now() - interval '3 hours'),
        (9003, 6003, NULL, 'PENDING', 'Pedido creado', now() - interval '2 hours'),
        (9004, 6004, NULL, 'PENDING', 'Pedido creado', now() - interval '1 hour'),
        (9005, 6005, NULL, 'PENDING', 'Pedido creado', now() - interval '30 minutes'),
        -- CONFIRMED
        (9010, 6010, NULL,      'PENDING',   'Pedido creado', now() - interval '1 day'),
        (9011, 6010, 'PENDING', 'CONFIRMED', 'Confirmado',    now() - interval '22 hours'),
        (9012, 6011, NULL,      'PENDING',   'Pedido creado', now() - interval '18 hours'),
        (9013, 6011, 'PENDING', 'CONFIRMED', 'Confirmado',    now() - interval '16 hours'),
        (9014, 6012, NULL,      'PENDING',   'Pedido creado', now() - interval '12 hours'),
        (9015, 6012, 'PENDING', 'CONFIRMED', 'Confirmado',    now() - interval '10 hours'),
        -- PREPARING
        (9020, 6020, NULL,        'PENDING',   'Pedido creado', now() - interval '2 days'),
        (9021, 6020, 'PENDING',   'CONFIRMED', 'Confirmado',    now() - interval '46 hours'),
        (9022, 6020, 'CONFIRMED', 'PREPARING', 'En preparación', now() - interval '44 hours'),
        (9023, 6021, NULL,        'PENDING',   'Pedido creado', now() - interval '28 hours'),
        (9024, 6021, 'PENDING',   'CONFIRMED', 'Confirmado',    now() - interval '26 hours'),
        (9025, 6021, 'CONFIRMED', 'PREPARING', 'En preparación', now() - interval '24 hours'),
        -- SHIPPED
        (9030, 6030, NULL,        'PENDING',   'Pedido creado', now() - interval '3 days'),
        (9031, 6030, 'PENDING',   'CONFIRMED', 'Confirmado',    now() - interval '70 hours'),
        (9032, 6030, 'CONFIRMED', 'PREPARING', 'En preparación', now() - interval '68 hours'),
        (9033, 6030, 'PREPARING', 'SHIPPED',   'Enviado',        now() - interval '60 hours'),
        (9034, 6031, NULL,        'PENDING',   'Pedido creado', now() - interval '2 days'),
        (9035, 6031, 'PENDING',   'CONFIRMED', 'Confirmado',    now() - interval '46 hours'),
        (9036, 6031, 'CONFIRMED', 'PREPARING', 'En preparación', now() - interval '44 hours'),
        (9037, 6031, 'PREPARING', 'SHIPPED',   'Enviado',        now() - interval '40 hours')
    ON CONFLICT (id) DO NOTHING;

    -- ── Recalculate order totals ──────────────────────────────────────────────
    UPDATE orders SET total_amount = COALESCE(
        (SELECT SUM(quantity * unit_price) FROM order_items WHERE order_id = orders.id), total_amount
    ) WHERE id BETWEEN 6001 AND 6031;

END;
$$;

-- ── Low stock inventory (simple fixed values, no window functions) ────────────
UPDATE inventory_balance SET quantity = 2, reserved = 1, updated_at = now()
WHERE variant_id = (SELECT id FROM variants ORDER BY id OFFSET 0 LIMIT 1)
  AND EXISTS (SELECT 1 FROM variants ORDER BY id OFFSET 0 LIMIT 1);

UPDATE inventory_balance SET quantity = 1, reserved = 0, updated_at = now()
WHERE variant_id = (SELECT id FROM variants ORDER BY id OFFSET 1 LIMIT 1)
  AND EXISTS (SELECT 1 FROM variants ORDER BY id OFFSET 1 LIMIT 1);

UPDATE inventory_balance SET quantity = 3, reserved = 2, updated_at = now()
WHERE variant_id = (SELECT id FROM variants ORDER BY id OFFSET 2 LIMIT 1)
  AND EXISTS (SELECT 1 FROM variants ORDER BY id OFFSET 2 LIMIT 1);

UPDATE inventory_balance SET quantity = 2, reserved = 0, updated_at = now()
WHERE variant_id = (SELECT id FROM variants ORDER BY id OFFSET 3 LIMIT 1)
  AND EXISTS (SELECT 1 FROM variants ORDER BY id OFFSET 3 LIMIT 1);

UPDATE inventory_balance SET quantity = 1, reserved = 1, updated_at = now()
WHERE variant_id = (SELECT id FROM variants ORDER BY id OFFSET 4 LIMIT 1)
  AND EXISTS (SELECT 1 FROM variants ORDER BY id OFFSET 4 LIMIT 1);

-- ── Reset sequences ───────────────────────────────────────────────────────────
SELECT setval(pg_get_serial_sequence('orders','id'),               (SELECT MAX(id) FROM orders) + 1);
SELECT setval(pg_get_serial_sequence('order_items','id'),          (SELECT MAX(id) FROM order_items) + 1);
SELECT setval(pg_get_serial_sequence('order_status_history','id'), (SELECT MAX(id) FROM order_status_history) + 1);
