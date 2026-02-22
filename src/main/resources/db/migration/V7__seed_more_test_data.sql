-- V7__seed_more_test_data.sql
-- Seeds extra users and large sample business data for testing.

-- 1) Seed additional test users (non-admin + admin)
INSERT INTO users (name, email, password_hash, role)
VALUES
    ('Demo User', 'user@example.com', '{noop}user123', 'USER'),
    ('Ops Admin', 'opsadmin@example.com', '{noop}ops123', 'ADMIN'),
    ('Tester One', 'tester1@example.com', '{noop}tester123', 'USER'),
    ('Tester Two', 'tester2@example.com', '{noop}tester123', 'USER')
ON CONFLICT (email) DO NOTHING;

-- 2) Seed businesses for new users
INSERT INTO businesses (owner_user_id, name, city, state, address)
SELECT u.id, b.name, b.city, b.state, b.address
FROM users u
JOIN (
    VALUES
        ('user@example.com',   'User Test Kitchen',   'Pune',      'MH', 'Baner High Street'),
        ('tester1@example.com','Tester One Kitchen',  'Bengaluru', 'KA', 'Koramangala 5th Block'),
        ('tester2@example.com','Tester Two Kitchen',  'Hyderabad', 'TS', 'Madhapur Main Road')
) AS b(email, name, city, state, address)
    ON b.email = u.email
WHERE NOT EXISTS (
    SELECT 1
    FROM businesses x
    WHERE x.owner_user_id = u.id
      AND x.name = b.name
);

-- 3) Seed platforms for each test business
WITH target_businesses AS (
    SELECT id
    FROM businesses
    WHERE name IN ('User Test Kitchen', 'Tester One Kitchen', 'Tester Two Kitchen')
)
INSERT INTO platforms (business_id, code, name)
SELECT tb.id, p.code, p.name
FROM target_businesses tb
CROSS JOIN (
    VALUES
        ('ZOMATO', 'Zomato'),
        ('SWIGGY', 'Swiggy'),
        ('DIRECT', 'Direct Orders')
) AS p(code, name)
WHERE NOT EXISTS (
    SELECT 1
    FROM platforms x
    WHERE x.business_id = tb.id
      AND x.code = p.code
);

-- 4) Seed 45 orders per business with deterministic patterns
WITH target_businesses AS (
    SELECT id, name
    FROM businesses
    WHERE name IN ('User Test Kitchen', 'Tester One Kitchen', 'Tester Two Kitchen')
),
series AS (
    SELECT generate_series(1, 45) AS i
),
order_seed AS (
    SELECT
        tb.id AS business_id,
        tb.name AS business_name,
        s.i AS seq,
        (DATE '2026-01-01' + ((s.i - 1) % 45))::date AS order_date,
        CASE
            WHEN s.i % 3 = 1 THEN 'ZOMATO'
            WHEN s.i % 3 = 2 THEN 'SWIGGY'
            ELSE 'DIRECT'
        END AS platform_code,
        (550 + s.i * 23)::numeric(12,2) AS gross_amount,
        CASE
            WHEN s.i % 3 = 0 THEN 0.00
            WHEN s.i % 3 = 1 THEN 22.00
            ELSE 21.00
        END::numeric(5,2) AS commission_rate,
        CASE
            WHEN s.i % 3 = 0 THEN 0.00
            ELSE 18.00
        END::numeric(5,2) AS gst_rate_on_comm,
        ('BULK-SEED-' || REPLACE(LOWER(tb.name), ' ', '-') || '-ORDER-' || s.i)::text AS note_key
    FROM target_businesses tb
    CROSS JOIN series s
),
computed_orders AS (
    SELECT
        os.business_id,
        os.order_date,
        os.platform_code,
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
        os.note_key,
        os.seq
    FROM order_seed os
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
    co.business_id,
    p.id AS platform_id,
    co.order_date,
    co.gross_amount,
    co.commission_rate,
    co.gst_rate_on_comm,
    co.commission_amount,
    co.gst_on_commission,
    co.net_expected,
    co.net_expected + CASE WHEN co.seq % 9 = 0 THEN 3.50 ELSE 0.00 END,
    CASE WHEN co.seq % 9 = 0 THEN 3.50 ELSE 0.00 END,
    co.note_key
FROM computed_orders co
JOIN platforms p
  ON p.business_id = co.business_id
 AND p.code = co.platform_code
WHERE NOT EXISTS (
    SELECT 1
    FROM orders x
    WHERE x.business_id = co.business_id
      AND x.notes = co.note_key
);

-- 5) Seed 45 expenses per business
WITH target_businesses AS (
    SELECT id, name
    FROM businesses
    WHERE name IN ('User Test Kitchen', 'Tester One Kitchen', 'Tester Two Kitchen')
),
series AS (
    SELECT generate_series(1, 45) AS i
),
expense_seed AS (
    SELECT
        tb.id AS business_id,
        (DATE '2026-01-01' + ((s.i - 1) % 45))::date AS expense_date,
        CASE (s.i % 6)
            WHEN 0 THEN 'Packaging'
            WHEN 1 THEN 'Vegetables'
            WHEN 2 THEN 'Gas'
            WHEN 3 THEN 'Staff Meal'
            WHEN 4 THEN 'Marketing'
            ELSE 'Utilities'
        END::varchar(80) AS category,
        (130 + s.i * 11)::numeric(12,2) AS amount,
        ('BULK-SEED-' || REPLACE(LOWER(tb.name), ' ', '-') || '-EXPENSE-' || s.i)::text AS note_key
    FROM target_businesses tb
    CROSS JOIN series s
)
INSERT INTO expenses (business_id, expense_date, category, amount, notes)
SELECT
    es.business_id,
    es.expense_date,
    es.category,
    es.amount,
    es.note_key
FROM expense_seed es
WHERE NOT EXISTS (
    SELECT 1
    FROM expenses x
    WHERE x.business_id = es.business_id
      AND x.notes = es.note_key
);
