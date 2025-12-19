-- Migration: Add active column to users table and role constraints
-- Created: 2025-12-19

-- Step 1: Add active column with default value
ALTER TABLE users ADD COLUMN active BOOLEAN NOT NULL DEFAULT true;

-- Step 2: Update role to NOT NULL and add constraint
ALTER TABLE users ALTER COLUMN role SET NOT NULL;
ALTER TABLE users ADD CONSTRAINT check_role CHECK (role IN ('USER', 'ADMIN', 'MANAGER'));

-- Step 3: Create initial ADMIN user (manual provisioning)
-- Uncomment and run after creating the first admin via API or CLI
-- INSERT INTO users (id, name, email, password, role, active, created_at)
-- VALUES (gen_random_uuid(), 'Admin', 'admin@expenso.com', 'your_hashed_password_here', 'ADMIN', true, NOW());

-- To create an admin, use bcrypt or a similar tool to hash the password first.
-- Example: password "admin123" hashed with bcrypt might be: $2b$10$... (40+ chars)
