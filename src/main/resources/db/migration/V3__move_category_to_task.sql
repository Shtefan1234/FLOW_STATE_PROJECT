ALTER TABLE tasks   ADD COLUMN category    VARCHAR(255);
ALTER TABLE tasks   ADD COLUMN date DATE;
ALTER TABLE tracks  DROP COLUMN category;
ALTER TABLE users   ADD COLUMN current_streak INT NOT NULL DEFAULT 0;
ALTER TABLE users   ADD COLUMN last_active_date DATE;