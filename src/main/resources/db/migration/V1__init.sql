-- V1__init.sql
-- PostgreSQL schema for KitchenDiary (users, businesses, platforms, orders, expenses)

BEGIN;

-- =========================
-- users
-- =========================
CREATE TABLE IF NOT EXISTS users (
                                     id              BIGSERIAL PRIMARY KEY,
                                     name            VARCHAR(120) NOT NULL,
    email           VARCHAR(180) NOT NULL,
    password_hash   TEXT NOT NULL,
    created_at      TIMESTAMPTZ NOT NULL DEFAULT NOW()
    );

CREATE UNIQUE INDEX IF NOT EXISTS idx_users_email
    ON users (email);


-- =========================
-- businesses
-- =========================
CREATE TABLE IF NOT EXISTS businesses (
                                          id              BIGSERIAL PRIMARY KEY,
                                          owner_user_id   BIGINT NOT NULL,
                                          name            VARCHAR(160) NOT NULL,
    gstin           VARCHAR(15),
    address         TEXT,
    city            TEXT,
    state           TEXT,
    created_at      TIMESTAMPTZ NOT NULL DEFAULT NOW(),

    CONSTRAINT fk_business_owner
    FOREIGN KEY (owner_user_id)
    REFERENCES users (id)
    ON DELETE RESTRICT
    );

CREATE INDEX IF NOT EXISTS idx_business_owner
    ON businesses (owner_user_id);


-- =========================
-- platforms
-- =========================
CREATE TABLE IF NOT EXISTS platforms (
                                         id              BIGSERIAL PRIMARY KEY,
                                         business_id     BIGINT NOT NULL,
                                         code            VARCHAR(20) NOT NULL,
    name            VARCHAR(80) NOT NULL,
    created_at      TIMESTAMPTZ NOT NULL DEFAULT NOW(),

    CONSTRAINT fk_platform_business
    FOREIGN KEY (business_id)
    REFERENCES businesses (id)
    ON DELETE CASCADE,

    CONSTRAINT uq_platform_business_code
    UNIQUE (business_id, code)
    );

CREATE INDEX IF NOT EXISTS idx_platform_business
    ON platforms (business_id);


-- =========================
-- orders
-- =========================
CREATE TABLE IF NOT EXISTS orders (
                                      id                  BIGSERIAL PRIMARY KEY,
                                      business_id         BIGINT NOT NULL,
                                      platform_id         BIGINT NOT NULL,

                                      order_date          DATE NOT NULL,

                                      gross_amount        NUMERIC(12,2) NOT NULL,

    commission_rate     NUMERIC(5,2) NOT NULL,        -- percent
    gst_rate_on_comm    NUMERIC(5,2) NOT NULL,        -- percent

    commission_amount   NUMERIC(12,2) NOT NULL,
    gst_on_commission   NUMERIC(12,2) NOT NULL,

    net_expected        NUMERIC(12,2) NOT NULL,
    net_received        NUMERIC(12,2) NOT NULL,
    mismatch_amount     NUMERIC(12,2) NOT NULL,

    notes               TEXT,

    created_at          TIMESTAMPTZ NOT NULL DEFAULT NOW(),
    updated_at          TIMESTAMPTZ NOT NULL DEFAULT NOW(),

    CONSTRAINT fk_orders_business
    FOREIGN KEY (business_id)
    REFERENCES businesses (id)
    ON DELETE CASCADE,

    CONSTRAINT fk_orders_platform
    FOREIGN KEY (platform_id)
    REFERENCES platforms (id)
    ON DELETE RESTRICT
    );

CREATE INDEX IF NOT EXISTS idx_order_business_date
    ON orders (business_id, order_date);

CREATE INDEX IF NOT EXISTS idx_order_platform
    ON orders (platform_id);


-- =========================
-- expenses
-- =========================
CREATE TABLE IF NOT EXISTS expenses (
                                        id              BIGSERIAL PRIMARY KEY,
                                        business_id     BIGINT NOT NULL,
                                        expense_date    DATE NOT NULL,
                                        category        VARCHAR(80) NOT NULL,
    amount          NUMERIC(12,2) NOT NULL,
    notes           TEXT,
    created_at      TIMESTAMPTZ NOT NULL DEFAULT NOW(),

    CONSTRAINT fk_expense_business
    FOREIGN KEY (business_id)
    REFERENCES businesses (id)
    ON DELETE CASCADE
    );

CREATE INDEX IF NOT EXISTS idx_expense_business_date
    ON expenses (business_id, expense_date);

COMMIT;