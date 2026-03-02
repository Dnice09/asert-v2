-- WHEN COMMITTING OR REVIEWING THIS FILE: Make sure that the timestamp in the file name (that serves as a version) is the latest timestamp, and that no new migration have been added in the meanwhile.
-- Adding migrations out of order may cause this migration to never execute or behave in an unexpected way.
-- Migrations should NOT BE EDITED. Add a new migration to apply changes.

BEGIN;

-- Step 1: Add a temporary column to store the IP addresses as VARCHAR(255)
ALTER TABLE api_key_metadata
ADD COLUMN created_ip_varchar VARCHAR(255);

-- Step 2: Copy data from the INET column to the temporary VARCHAR column
UPDATE api_key_metadata
SET created_ip_varchar = created_ip::VARCHAR;

-- Step 3: Drop the old INET column
ALTER TABLE api_key_metadata
DROP COLUMN created_ip;

-- Step 4: Rename the temporary VARCHAR column to the original column name
ALTER TABLE api_key_metadata
RENAME COLUMN created_ip_varchar TO created_ip;

-- Step 5: (Optional) Add a check constraint to ensure the IP addresses are valid, if necessary
-- ALTER TABLE api_key_metadata
-- ADD CONSTRAINT chk_created_ip_valid_ip CHECK (created_ip ~ '^(?:(?:25[0-5]|2[0-4][0-9]|[01]?[0-9][0-9]?)\.){3}(?:25[0-5]|2[0-4][0-9]|[01]?[0-9][0-9]?)$|^([0-9a-fA-F]{1,4}:){7}[0-9a-fA-F]{1,4}$');

COMMIT;
