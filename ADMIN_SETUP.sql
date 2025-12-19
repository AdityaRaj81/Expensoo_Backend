-- Admin User Setup Script
-- This file contains SQL to manually create the initial ADMIN user in the database

-- Option 1: Create admin with plaintext password (for development/testing only)
-- NOTE: In production, use bcrypt-hashed passwords or a secure provisioning mechanism

-- Generate a UUID and insert the admin user
INSERT INTO users (id, name, email, password, role, active, created_at)
VALUES (
  gen_random_uuid(),
  'Super Admin',
  'admin@expenso.com',       -- Change this email as needed
  'admin123',                -- Change this password (plaintext for dev; hash in production)
  'ADMIN',
  true,
  NOW()
)
ON CONFLICT (email) DO NOTHING;

-- Verify the insertion
SELECT id, name, email, role, active, created_at FROM users WHERE role = 'ADMIN';
