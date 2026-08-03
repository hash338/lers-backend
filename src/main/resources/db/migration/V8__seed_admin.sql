INSERT INTO users (id, email, password, role, full_name, status)
SELECT
    gen_random_uuid(),
    'admin@lers.lb',
    '$2a$10$92IXUNpkjO0rOQ5byMi.Ye4oKoEa3Ro9llC/.og/at2uheWG/igi.',
    'SUPER_ADMIN',
    'Super Admin',
    'active'
    WHERE NOT EXISTS (
  SELECT 1 FROM users WHERE email = 'admin@lers.lb'
);