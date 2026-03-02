-- WHEN COMMITTING OR REVIEWING THIS FILE: Make sure that the timestamp in the file name (that serves as a version) is the latest timestamp, and that no new migration have been added in the meanwhile.
-- Adding migrations out of order may cause this migration to never execute or behave in an unexpected way.
-- Migrations should NOT BE EDITED. Add a new migration to apply changes.

-- Add the user_id column to the hotesl table
ALTER TABLE hotels ADD COLUMN user_id BIGINT;

-- Update the user_id column based on the email in the users table
UPDATE hotels
SET user_id = (SELECT id FROM users WHERE email = hotels.created_by);

-- Make the user_id column non-nullable
ALTER TABLE hotels ALTER COLUMN user_id SET NOT NULL;

-- Add a foreign key constraint on the user_id column
ALTER TABLE hotels
ADD CONSTRAINT FK_hotels_user
FOREIGN KEY (user_id) REFERENCES users (id);

