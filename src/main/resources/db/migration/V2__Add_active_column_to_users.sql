-- Add active column with default true (allows nulls initially)
ALTER TABLE users ADD COLUMN IF NOT EXISTS active BOOLEAN DEFAULT true;

-- Update any null values to true
UPDATE users SET active = true WHERE active IS NULL;

-- Now add NOT NULL constraint
ALTER TABLE users ALTER COLUMN active SET NOT NULL;
