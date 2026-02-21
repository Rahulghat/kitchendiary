INSERT INTO users (name, email, password_hash)
VALUES ('Rahul', 'rahul@example.com', 'demo_hash')
    ON CONFLICT (email) DO NOTHING;

INSERT INTO businesses (owner_user_id, name, city, state)
SELECT u.id, 'Demo Kitchen', 'Mumbai', 'MH'
FROM users u
WHERE u.email = 'rahul@example.com'
    ON CONFLICT DO NOTHING;