ALTER TABLE tracks ADD COLUMN current_streak INT NOT NULL DEFAULT 0;
ALTER TABLE tracks ADD COLUMN last_active_date DATE;
ALTER TABLE users DROP COLUMN current_streak;
ALTER TABLE users DROP COLUMN last_active_date;

