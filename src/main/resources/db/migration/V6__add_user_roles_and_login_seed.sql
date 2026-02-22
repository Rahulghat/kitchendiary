-- V6__add_user_roles_and_login_seed.sql
-- Adds role-based authorization support and sets a working demo login.

ALTER TABLE users
    ADD COLUMN IF NOT EXISTS role VARCHAR(20);

UPDATE users
SET role = 'USER'
WHERE role IS NULL OR TRIM(role) = '';

ALTER TABLE users
    ALTER COLUMN role SET NOT NULL;

-- Demo admin credentials:
-- email: rahul@example.com
-- password: rahul123
UPDATE users
SET role = 'ADMIN',
    password_hash = '{noop}rahul123'
WHERE email = 'rahul@example.com';
