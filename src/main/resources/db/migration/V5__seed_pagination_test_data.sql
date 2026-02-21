-- V5__seed_pagination_test_data.sql
-- Adds bulk sample data to test UI pagination.

-- Ensure demo business exists
WITH demo_business AS (
    SELECT id
    FROM businesses
    WHERE name = 'Demo Kitchen'
    ORDER BY id
    LIMIT 1
),
platforms_needed AS (
    SELECT db.id AS business_id, p.code, p.name
    FROM demo_business db
    CROSS JOIN (
        VALUES
            ('ZOMATO', 'Zomato'),
            ('SWIGGY', 'Swiggy'),
            ('DIRECT', 'Direct Orders')
    ) AS p(code, name)
)
INSERT INTO platforms (business_id, code, name)
SELECT pn.business_id, pn.code, pn.name
FROM platforms_needed pn
WHERE NOT EXISTS (
    SELECT 1
    FROM platforms x
    WHERE x.business_id = pn.business_id
      AND x.code = pn.code
);

-- Seed 60 orders for pagination checks
WITH demo_business AS (
    SELECT id
    FROM businesses
    WHERE name = 'Demo Kitchen'
    ORDER BY id
    LIMIT 1
),
bulk_orders AS (
    SELECT
        gs.i AS seq,
        (DATE '2026-01-01' + ((gs.i - 1) % 60))::date AS order_date,
        CASE
            WHEN gs.i % 3 = 1 THEN 'ZOMATO'
            WHEN gs.i % 3 = 2 THEN 'SWIGGY'
            ELSE 'DIRECT'
        END AS platform_code,
        (600 + gs.i * 17)::numeric(12,2) AS gross_amount,
        CASE
            WHEN gs.i % 3 = 0 THEN 0.00
            WHEN gs.i % 3 = 1 THEN 22.00
            ELSE 21.00
        END::numeric(5,2) AS commission_rate,
        CASE
            WHEN gs.i % 3 = 0 THEN 0.00
            ELSE 18.00
        END::numeric(5,2) AS gst_rate_on_comm,
        ('PAGINATION-SEED-ORDER-' || gs.i)::text AS notes
    FROM generate_series(1, 60) AS gs(i)
),
computed_orders AS (
    SELECT
        bo.seq,
        bo.order_date,
        bo.platform_code,
        bo.gross_amount,
        bo.commission_rate,
        bo.gst_rate_on_comm,
        ROUND(bo.gross_amount * bo.commission_rate / 100.0, 2) AS commission_amount,
        ROUND(
            ROUND(bo.gross_amount * bo.commission_rate / 100.0, 2) * bo.gst_rate_on_comm / 100.0,
            2
        ) AS gst_on_commission,
        ROUND(
            bo.gross_amount
            - ROUND(bo.gross_amount * bo.commission_rate / 100.0, 2)
            - ROUND(
                ROUND(bo.gross_amount * bo.commission_rate / 100.0, 2) * bo.gst_rate_on_comm / 100.0,
                2
            ),
            2
        ) AS net_expected,
        bo.notes
    FROM bulk_orders bo
)
INSERT INTO orders (
    business_id,
    platform_id,
    order_date,
    gross_amount,
    commission_rate,
    gst_rate_on_comm,
    commission_amount,
    gst_on_commission,
    net_expected,
    net_received,
    mismatch_amount,
    notes
)
SELECT
    db.id,
    p.id,
    co.order_date,
    co.gross_amount,
    co.commission_rate,
    co.gst_rate_on_comm,
    co.commission_amount,
    co.gst_on_commission,
    co.net_expected,
    co.net_expected + CASE WHEN co.seq % 10 = 0 THEN 4.00 ELSE 0.00 END,
    CASE WHEN co.seq % 10 = 0 THEN 4.00 ELSE 0.00 END,
    co.notes
FROM computed_orders co
JOIN demo_business db ON TRUE
JOIN platforms p
    ON p.business_id = db.id
   AND p.code = co.platform_code
WHERE NOT EXISTS (
    SELECT 1
    FROM orders o
    WHERE o.business_id = db.id
      AND o.notes = co.notes
);

-- Seed 60 expenses for pagination checks
WITH demo_business AS (
    SELECT id
    FROM businesses
    WHERE name = 'Demo Kitchen'
    ORDER BY id
    LIMIT 1
),
bulk_expenses AS (
    SELECT
        gs.i AS seq,
        (DATE '2026-01-01' + ((gs.i - 1) % 60))::date AS expense_date,
        CASE (gs.i % 5)
            WHEN 0 THEN 'Packaging'
            WHEN 1 THEN 'Vegetables'
            WHEN 2 THEN 'Gas'
            WHEN 3 THEN 'Staff Meal'
            ELSE 'Marketing'
        END::varchar(80) AS category,
        (120 + gs.i * 9)::numeric(12,2) AS amount,
        ('PAGINATION-SEED-EXPENSE-' || gs.i)::text AS notes
    FROM generate_series(1, 60) AS gs(i)
)
INSERT INTO expenses (business_id, expense_date, category, amount, notes)
SELECT
    db.id,
    be.expense_date,
    be.category,
    be.amount,
    be.notes
FROM bulk_expenses be
JOIN demo_business db ON TRUE
WHERE NOT EXISTS (
    SELECT 1
    FROM expenses e
    WHERE e.business_id = db.id
      AND e.notes = be.notes
);
