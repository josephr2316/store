-- V11: Realistic pending/active orders + low-stock variants for dashboard
-- Adds data for: Pedidos Pendientes, Pedidos en Sistema, Variantes Bajo Stock
-- PostgreSQL

-- ─── 1. Remove old non-delivered seed orders if they exist ────────────────────
DELETE FROM order_status_history WHERE order_id BETWEEN 5500 AND 5599;
DELETE FROM order_items          WHERE order_id BETWEEN 5500 AND 5599;
DELETE FROM orders               WHERE id       BETWEEN 5500 AND 5599;

-- ─── 2. Insert realistic active orders (non-DELIVERED) ───────────────────────
-- Using the first available variant ids from the DB dynamically
INSERT INTO orders (
    id, external_id, channel, status,
    customer_name, customer_phone,
    shipping_address, shipping_city, shipping_region,
    total_amount, currency, notes,
    created_at, updated_at
) VALUES
-- PENDING (5 pedidos — aparecen en "Pedidos Pendientes" y "Pedidos en Sistema")
(5500, 'IG-2026-001', 'INSTAGRAM',  'PENDING',
 'María García',    '8091234001',
 'Calle El Conde 45, Zona Colonial', 'Santo Domingo', 'Distrito Nacional',
 3500.00, 'USD', 'Cliente quiere entrega rápida',
 now() - interval '6 hours', now()),

(5501, 'WA-2026-002', 'WHATSAPP',   'PENDING',
 'Carlos Rodríguez', '8091234002',
 'Av. Winston Churchill 120', 'Santo Domingo', 'Distrito Nacional',
 2800.00, 'USD', NULL,
 now() - interval '4 hours', now()),

(5502, 'IG-2026-003', 'INSTAGRAM',  'PENDING',
 'Ana Martínez',    '8091234003',
 'Calle 27 de Febrero 300', 'Santiago', 'Santiago',
 4200.00, 'USD', 'Preguntar color disponible',
 now() - interval '2 hours', now()),

(5503, NULL,          'DIRECT',     'PENDING',
 'Luis Pérez',      '8091234004',
 'Av. Abraham Lincoln 55', 'Santo Domingo', 'Distrito Nacional',
 1950.00, 'USD', NULL,
 now() - interval '1 hour', now()),

(5504, 'WA-2026-005', 'WHATSAPP',   'PENDING',
 'Sofía Jiménez',   '8091234005',
 'Av. Luperón 200, El Millón', 'Santo Domingo', 'Distrito Nacional',
 5100.00, 'USD', 'Confirmar antes de enviar',
 now() - interval '30 minutes', now()),

-- CONFIRMED (3 pedidos confirmados)
(5510, 'WA-2026-010', 'WHATSAPP',   'CONFIRMED',
 'Roberto Núñez',   '8091234010',
 'Calle Las Damas 12, Naco', 'Santo Domingo', 'Distrito Nacional',
 6800.00, 'USD', NULL,
 now() - interval '1 day', now()),

(5511, 'IG-2026-011', 'INSTAGRAM',  'CONFIRMED',
 'Patricia López',  '8091234011',
 'Calle del Sol 78, Piantini', 'Santo Domingo', 'Distrito Nacional',
 3200.00, 'USD', NULL,
 now() - interval '18 hours', now()),

(5512, 'DI-2026-012', 'DIRECT',     'CONFIRMED',
 'Javier Santos',   '8091234012',
 'Av. Independencia 450', 'Santiago', 'Santiago',
 4750.00, 'USD', 'Cliente habitual',
 now() - interval '12 hours', now()),

-- PREPARING (2 pedidos en preparación)
(5520, 'WA-2026-020', 'WHATSAPP',   'PREPARING',
 'Carmen Reyes',    '8091234020',
 'Av. 27 de Febrero 180, Los Prados', 'Santo Domingo', 'Distrito Nacional',
 7200.00, 'USD', NULL,
 now() - interval '2 days', now()),

(5521, 'IG-2026-021', 'INSTAGRAM',  'PREPARING',
 'Andrés Castro',   '8091234021',
 'Calle Beller 34', 'La Romana', 'La Romana',
 2900.00, 'USD', NULL,
 now() - interval '28 hours', now()),

-- SHIPPED (2 pedidos enviados)
(5530, 'WA-2026-030', 'WHATSAPP',   'SHIPPED',
 'Natalia Gómez',   '8091234030',
 'Calle Hostos 22, Gazcue', 'Santo Domingo', 'Distrito Nacional',
 8500.00, 'USD', NULL,
 now() - interval '3 days', now()),

(5531, 'DI-2026-031', 'DIRECT',     'SHIPPED',
 'Fernando Díaz',   '8091234031',
 'Av. San Martin 99', 'Santiago', 'Santiago',
 3600.00, 'USD', NULL,
 now() - interval '2 days', now()),

-- CANCELLED (1 cancelado)
(5540, NULL,          'OTHER',      'CANCELLED',
 'Isabel Torres',   '8091234040',
 NULL, NULL, NULL,
 1500.00, 'USD', 'Cliente desistió',
 now() - interval '4 days', now())

ON CONFLICT (id) DO UPDATE
    SET status = EXCLUDED.status,
        customer_name = EXCLUDED.customer_name,
        shipping_address = EXCLUDED.shipping_address,
        total_amount = EXCLUDED.total_amount,
        updated_at = now();

-- ─── 3. Items for each active order (2 items each) ────────────────────────────
INSERT INTO order_items (id, order_id, variant_id, quantity, unit_price, created_at, updated_at)
SELECT
    5600 + (rn - 1) * 2 + item_n,
    o.order_id,
    COALESCE(
        (SELECT id FROM variants ORDER BY id OFFSET ((rn * 3 + item_n * 7) % GREATEST(1,(SELECT COUNT(*) FROM variants)::int)) LIMIT 1),
        (SELECT MIN(id) FROM variants)
    ),
    item_n + 1,
    (o.total_amount / 2 / (item_n + 1))::decimal(19,4),
    now(), now()
FROM (
    SELECT id AS order_id, total_amount, ROW_NUMBER() OVER (ORDER BY id) AS rn
    FROM orders WHERE id BETWEEN 5500 AND 5599
) o, generate_series(0, 1) AS item_n
ON CONFLICT (id) DO NOTHING;

-- ─── 4. Status history for all new orders ─────────────────────────────────────
INSERT INTO order_status_history (id, order_id, from_status, to_status, reason, changed_at)
SELECT
    5700 + ROW_NUMBER() OVER (ORDER BY id) - 1,
    id,
    NULL,
    'PENDING',
    'Order created',
    created_at
FROM orders WHERE id BETWEEN 5500 AND 5599
ON CONFLICT (id) DO NOTHING;

-- CONFIRMED transitions
INSERT INTO order_status_history (id, order_id, from_status, to_status, reason, changed_at)
VALUES
    (5800, 5510, 'PENDING', 'CONFIRMED', 'Confirmado por agente',    now() - interval '22 hours'),
    (5801, 5511, 'PENDING', 'CONFIRMED', 'Confirmado',               now() - interval '16 hours'),
    (5802, 5512, 'PENDING', 'CONFIRMED', 'Confirmado por teléfono',  now() - interval '10 hours')
ON CONFLICT (id) DO NOTHING;

-- PREPARING transitions
INSERT INTO order_status_history (id, order_id, from_status, to_status, reason, changed_at)
VALUES
    (5810, 5520, 'PENDING',    'CONFIRMED',  'Confirmado',       now() - interval '46 hours'),
    (5811, 5520, 'CONFIRMED',  'PREPARING',  'Preparando envío', now() - interval '44 hours'),
    (5812, 5521, 'PENDING',    'CONFIRMED',  'Confirmado',       now() - interval '26 hours'),
    (5813, 5521, 'CONFIRMED',  'PREPARING',  'En empaque',       now() - interval '24 hours')
ON CONFLICT (id) DO NOTHING;

-- SHIPPED transitions
INSERT INTO order_status_history (id, order_id, from_status, to_status, reason, changed_at)
VALUES
    (5820, 5530, 'PENDING',    'CONFIRMED',  'Confirmado',          now() - interval '70 hours'),
    (5821, 5530, 'CONFIRMED',  'PREPARING',  'En preparación',      now() - interval '68 hours'),
    (5822, 5530, 'PREPARING',  'SHIPPED',    'Enviado con motoboy',  now() - interval '60 hours'),
    (5823, 5531, 'PENDING',    'CONFIRMED',  'Confirmado',           now() - interval '46 hours'),
    (5824, 5531, 'CONFIRMED',  'PREPARING',  'En preparación',       now() - interval '44 hours'),
    (5825, 5531, 'PREPARING',  'SHIPPED',    'Enviado por courier',  now() - interval '40 hours')
ON CONFLICT (id) DO NOTHING;

-- CANCELLED transition
INSERT INTO order_status_history (id, order_id, from_status, to_status, reason, changed_at)
VALUES (5830, 5540, 'PENDING', 'CANCELLED', 'Cliente desistió', now() - interval '3 days' + interval '2 hours')
ON CONFLICT (id) DO NOTHING;

-- ─── 5. Update order totals from items ────────────────────────────────────────
UPDATE orders o
SET total_amount = COALESCE(
    (SELECT SUM(quantity * unit_price) FROM order_items WHERE order_id = o.id), total_amount
)
WHERE o.id BETWEEN 5500 AND 5599;

-- ─── 6. Low-stock inventory: Update/insert variants with ≤3 available ─────────
-- First update cam-001-m to have a meaningful stock value
UPDATE inventory_balance
SET quantity = 2, reserved = 1, updated_at = now()
WHERE variant_id = (SELECT id FROM variants WHERE sku = 'cam-001-m' LIMIT 1);

-- Set 8 existing variants to low stock using a CTE (window functions not allowed in SET clause)
WITH low_stock_candidates AS (
    SELECT ib.variant_id,
           (ROW_NUMBER() OVER (ORDER BY ib.variant_id) % 3)::int AS rn
    FROM (
        SELECT ib2.variant_id
        FROM inventory_balance ib2
        JOIN variants v ON v.id = ib2.variant_id
        WHERE ib2.quantity > 3
        ORDER BY ib2.variant_id
        LIMIT 8
    ) ib
)
UPDATE inventory_balance ib
SET quantity   = 1 + low_stock_candidates.rn,
    reserved   = 0,
    updated_at = now()
FROM low_stock_candidates
WHERE ib.variant_id = low_stock_candidates.variant_id;

-- Insert low-stock balances for variants that have no balance yet (if any)
INSERT INTO inventory_balance (variant_id, quantity, reserved, created_at)
SELECT v.id, (1 + ROW_NUMBER() OVER (ORDER BY v.id) % 3)::int, 0, now()
FROM variants v
WHERE NOT EXISTS (SELECT 1 FROM inventory_balance ib WHERE ib.variant_id = v.id)
LIMIT 5
ON CONFLICT (variant_id) DO NOTHING;

-- Make sure at least 5 variants total have low stock
UPDATE inventory_balance
SET quantity = 2, reserved = 1, updated_at = now()
WHERE variant_id IN (
    SELECT variant_id FROM inventory_balance
    WHERE (quantity - reserved) > 3
    ORDER BY variant_id
    LIMIT GREATEST(0, 5 - (SELECT COUNT(*) FROM inventory_balance WHERE (quantity - reserved) <= 3)::int)
);

-- ─── 7. Reset sequences ───────────────────────────────────────────────────────
SELECT setval(pg_get_serial_sequence('orders','id'),               (SELECT MAX(id) FROM orders) + 1);
SELECT setval(pg_get_serial_sequence('order_items','id'),          (SELECT MAX(id) FROM order_items) + 1);
SELECT setval(pg_get_serial_sequence('order_status_history','id'), (SELECT MAX(id) FROM order_status_history) + 1);
