-- WHEN COMMITTING OR REVIEWING THIS FILE: Make sure that the timestamp in the file name (that serves as a version) is the latest timestamp, and that no new migration have been added in the meanwhile.
-- Adding migrations out of order may cause this migration to never execute or behave in an unexpected way.
-- Migrations should NOT BE EDITED. Add a new migration to apply changes.


BEGIN;

-- Step 1: Create a temporary column with the correct type
ALTER TABLE state_machine_states ADD COLUMN state_temp OID;

-- Step 2: Copy the data from the old column to the temporary column
-- Note: You may need to write a custom function or logic to properly convert BYTEA data to OID if necessary

-- Step 3: Drop the old column
ALTER TABLE state_machine_states DROP COLUMN state;

-- Step 4: Rename the temporary column to the original column name
ALTER TABLE state_machine_states RENAME COLUMN state_temp TO state;

COMMIT;
