-- Ensure admin user can log in with password 'admin123' (BCrypt-compatible hash)
CREATE EXTENSION IF NOT EXISTS pgcrypto;

UPDATE users
SET password = crypt('admin123', gen_salt('bf', 10))
WHERE username = 'admin';
