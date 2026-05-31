-- Idempotent SQL script to seed the initial System Admin account
INSERT INTO admins (
    id,
    email,
    password_hash,
    first_name,
    last_name,
    account_status,
    created_at,
    updated_at
)
SELECT
    gen_random_uuid(),
    'admin@medicinelocator.com',
    -- This is the REAL mathematically correct BCrypt hash for "change_me_strong_password"
    '$2a$12$.hG0FhR49LzYpD5v9ClyD.Y8rV/zO0G6tC7T9uH9IbyH7wL6b1mve',
    'System',
    'Admin',
    'ACTIVE',
    NOW(),
    NOW()
    WHERE NOT EXISTS (
    SELECT 1 FROM admins WHERE email = 'admin@medicinelocator.com'
);