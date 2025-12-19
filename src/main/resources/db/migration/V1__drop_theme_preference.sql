-- Drop theme preference column if it exists (handle both naming conventions)
ALTER TABLE users DROP COLUMN IF EXISTS theme_preference;
ALTER TABLE users DROP COLUMN IF EXISTS themePreference;