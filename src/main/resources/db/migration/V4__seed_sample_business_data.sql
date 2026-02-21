-- V4__seed_sample_business_data.sql
-- Adds sample platforms, orders, and expenses for the demo business.

-- 1) Seed platforms for Demo Kitchen
WITH demo_business AS (
    SELECT id
    FROM businesses
    WHERE name = 'Demo Kitchen'
    ORDER BY id
    LIMIT 1
)
INSERT INTO platforms (business_id, code, name)
SELECT db.id, p.code, p.name
FROM demo_business db
CROSS JOIN (
    VALUES
        ('ZOMATO', 'Zomato'),
        ('SWIGGY', 'Swiggy'),
        ('DIRECT', 'Direct Orders')
) AS p(code, name)
WHERE NOT EXISTS (
    SELECT 1
    FROM platforms existing
    WHERE existing.business_id = db.id
      AND existing.code = p.code
);

-- 2) Seed sample orders
WITH demo_business AS (
    SELECT id
    FROM businesses
    WHERE name = 'Demo Kitchen'
    ORDER BY id
    LIMIT 1
),
order_seed AS (
    SELECT
        v.order_date::date AS order_date,
        v.platform_code::varchar(20) AS platform_code,
        v.gross_amount::numeric(12,2) AS gross_amount,
        v.commission_rate::numeric(5,2) AS commission_rate,
        v.gst_rate_on_comm::numeric(5,2) AS gst_rate_on_comm,
        v.net_received::numeric(12,2) AS net_received,
        v.notes::text AS notes
    FROM (
        VALUES
            ('2026-02-10', 'ZOMATO', 2400.00, 22.00, 18.00, 1768.64, 'Lunch peak'),
            ('2026-02-11', 'SWIGGY', 1800.00, 21.00, 18.00, 1373.00, 'Evening orders'),
            ('2026-02-12', 'DIRECT', 950.00, 0.00, 0.00, 950.00, 'WhatsApp direct orders'),
            ('2026-02-13', 'ZOMATO', 3150.00, 22.00, 18.00, 2285.23, 'Festival promo day'),
            ('2026-02-14', 'SWIGGY', 2100.00, 21.00, 18.00, 1600.00, 'Minor payout mismatch')
    ) AS v(order_date, platform_code, gross_amount, commission_rate, gst_rate_on_comm, net_received, notes)
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
    db.id AS business_id,
    p.id AS platform_id,
    os.order_date,
    os.gross_amount,
    os.commission_rate,
    os.gst_rate_on_comm,
    ROUND(os.gross_amount * os.commission_rate / 100.0, 2) AS commission_amount,
    ROUND(
        ROUND(os.gross_amount * os.commission_rate / 100.0, 2) * os.gst_rate_on_comm / 100.0,
        2
    ) AS gst_on_commission,
    ROUND(
        os.gross_amount
        - ROUND(os.gross_amount * os.commission_rate / 100.0, 2)
        - ROUND(
            ROUND(os.gross_amount * os.commission_rate / 100.0, 2) * os.gst_rate_on_comm / 100.0,
            2
        ),
        2
    ) AS net_expected,
    os.net_received,
    ROUND(
        os.net_received
        - (
            os.gross_amount
            - ROUND(os.gross_amount * os.commission_rate / 100.0, 2)
            - ROUND(
                ROUND(os.gross_amount * os.commission_rate / 100.0, 2) * os.gst_rate_on_comm / 100.0,
                2
            )
        ),
        2
    ) AS mismatch_amount,
    os.notes
FROM order_seed os
JOIN demo_business db ON TRUE
JOIN platforms p
    ON p.business_id = db.id
   AND p.code = os.platform_code
WHERE NOT EXISTS (
    SELECT 1
    FROM orders o
    WHERE o.business_id = db.id
      AND o.platform_id = p.id
      AND o.order_date = os.order_date
      AND o.gross_amount = os.gross_amount
);

-- 3) Seed sample expenses
WITH demo_business AS (
    SELECT id
    FROM businesses
    WHERE name = 'Demo Kitchen'
    ORDER BY id
    LIMIT 1
),
expense_seed AS (
    SELECT
        v.expense_date::date AS expense_date,
        v.category::varchar(80) AS category,
        v.amount::numeric(12,2) AS amount,
        v.notes::text AS notes
    FROM (
        VALUES
            ('2026-02-10', 'Packaging', 320.00, 'Meal boxes and carry bags'),
            ('2026-02-11', 'Vegetables', 780.00, 'Daily fresh stock'),
            ('2026-02-12', 'Gas', 1150.00, 'Commercial cylinder refill'),
            ('2026-02-13', 'Staff Meal', 260.00, 'Kitchen staff dinner'),
            ('2026-02-14', 'Marketing', 500.00, 'Local leaflet campaign')
    ) AS v(expense_date, category, amount, notes)
)
INSERT INTO expenses (business_id, expense_date, category, amount, notes)
SELECT
    db.id,
    es.expense_date,
    es.category,
    es.amount,
    es.notes
FROM expense_seed es
JOIN demo_business db ON TRUE
WHERE NOT EXISTS (
    SELECT 1
    FROM expenses e
    WHERE e.business_id = db.id
      AND e.expense_date = es.expense_date
      AND e.category = es.category
      AND e.amount = es.amount
);
