-- Migration to drop staff_id column from staff_change_requests table
-- WHEN COMMITTING OR REVIEWING THIS FILE: Make sure that the timestamp in the file name (that serves as a version) is the latest timestamp, and that no new migration has been added in the meantime.
-- Adding migrations out of order may cause this migration to never execute or behave in an unexpected way.
-- Migrations should NOT BE EDITED. Add a new migration to apply changes.

BEGIN;

ALTER TABLE staff_change_requests
    DROP COLUMN IF EXISTS staff_id;

-- Optionally, you may also want to drop the foreign key constraint if it exists
ALTER TABLE staff_change_requests
    DROP CONSTRAINT IF EXISTS FK_staff_change_requests_ON_staff;

COMMIT;
