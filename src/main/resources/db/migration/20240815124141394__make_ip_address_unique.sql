-- WHEN COMMITTING OR REVIEWING THIS FILE: Make sure that the timestamp in the file name (that serves as a version) is the latest timestamp, and that no new migration have been added in the meanwhile.
-- Adding migrations out of order may cause this migration to never execute or behave in an unexpected way.
-- Migrations should NOT BE EDITED. Add a new migration to apply changes.
--
-- This migration adds a unique constraint to the `system_ip` column in the `api_keys` table.

BEGIN;

-- Step 1: Alter the table to add a unique constraint on the `system_ip` column
ALTER TABLE api_keys
ADD CONSTRAINT uc_api_keys_system_ip UNIQUE (system_ip);

COMMIT;
