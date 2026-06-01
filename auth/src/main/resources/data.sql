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
    -- ✅ Correct BCrypt hash (strength 12) for password: "change_me_strong_password"
    '$2a$12$xYdm4PrqvyUmX1k7gtpMZenkl1aF2LmMFk3NCTNkQOj6B0CxJSH66',
    'System',
    'Admin',
    'ACTIVE',
    NOW(),
    NOW()
    WHERE NOT EXISTS (
    SELECT 1 FROM admins WHERE email = 'admin@medicinelocator.com'
);