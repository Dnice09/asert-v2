-- WHEN COMMITTING OR REVIEWING THIS FILE: Make sure that the timestamp in the file name (that serves as a version) is the latest timestamp, and that no new migration have been added in the meanwhile.
-- Adding migrations out of order may cause this migration to never execute or behave in an unexpected way.
-- Migrations should NOT BE EDITED. Add a new migration to apply changes.

-- Step 1: Add the new column with TIMESTAMP WITHOUT TIME ZONE type
ALTER TABLE payments
    ADD COLUMN date_paid_new TIMESTAMP WITHOUT TIME ZONE;

-- Step 2: Copy data from the old column to the new column
UPDATE payments
SET date_paid_new = date_paid::TIMESTAMP;

-- Step 3: Drop the old date_paid column
ALTER TABLE payments
    DROP COLUMN date_paid;

-- Step 4: Rename the new column to date_paid
ALTER TABLE payments
    RENAME COLUMN date_paid_new TO date_paid;
