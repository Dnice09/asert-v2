-- WHEN COMMITTING OR REVIEWING THIS FILE: Make sure that the timestamp in the file name (that serves as a version) is the latest timestamp, and that no new migration have been added in the meanwhile.
-- Adding migrations out of order may cause this migration to never execute or behave in an unexpected way.
-- Migrations should NOT BE EDITED. Add a new migration to apply changes.

-- Start a transaction
BEGIN;

-- Step 1: Drop the mobile_number column
ALTER TABLE users
DROP COLUMN mobile_number;

-- Step 2: Make the phone_number column non-nullable
ALTER TABLE users
ALTER COLUMN phone_number SET NOT NULL;

-- Commit the transaction
COMMIT;
