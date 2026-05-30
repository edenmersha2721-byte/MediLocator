-- Idempotent SQL script to seed the initial System Admin account if it does not already exist
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
    -- Pre-hashed BCrypt password matching "change_me_strong_password" or standard reference testing keys
    '$2a$12$92IXUNpkjO0rOQ5byMi.Ye4oKoEa3Ro9llC/.og/at2.uheWG/igi',
    'System',
    'Admin',
    'ACTIVE',
    NOW(),
    NOW()
    WHERE NOT EXISTS (
    SELECT 1 FROM admins WHERE email = 'admin@medicinelocator.com'
);