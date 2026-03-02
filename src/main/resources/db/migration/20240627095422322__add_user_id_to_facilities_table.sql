-- WHEN COMMITTING OR REVIEWING THIS FILE: Make sure that the timestamp in the file name (that serves as a version) is the latest timestamp, and that no new migrations have been added in the meanwhile.
-- Adding migrations out of order may cause this migration to never execute or behave in an unexpected way.
-- Migrations should NOT BE EDITED. Add a new migration to apply changes.

-- Add the user_id column to the facilities table
ALTER TABLE facilities ADD COLUMN user_id INT;

-- Update the user_id column based on the email in the users table
UPDATE facilities
SET user_id = (SELECT id FROM users WHERE email = facilities.created_by);

-- Make the user_id column non-nullable
ALTER TABLE facilities ALTER COLUMN user_id SET NOT NULL;

-- Add a foreign key constraint on the user_id column
ALTER TABLE facilities
ADD CONSTRAINT FK_facilities_user
FOREIGN KEY (user_id) REFERENCES users (id);

