-- WHEN COMMITTING OR REVIEWING THIS FILE: Make sure that the timestamp in the file name (that serves as a version) is the latest timestamp, and that no new migration have been added in the meanwhile.
-- Adding migrations out of order may cause this migration to never execute or behave in an unexpected way.
-- Migrations should NOT BE EDITED. Add a new migration to apply changes.
--
-- Migration to make 'title', 'gender', and 'dob' nullable in the 'assessors' table.

BEGIN;

-- 1. Remove NOT NULL constraint from the 'title' column
ALTER TABLE assessors
    ALTER COLUMN title DROP NOT NULL;

-- 2. Remove NOT NULL constraint from the 'gender' column
ALTER TABLE assessors
    ALTER COLUMN gender DROP NOT NULL;

-- 3. Remove NOT NULL constraint from the 'dob' column
ALTER TABLE assessors
    ALTER COLUMN dob DROP NOT NULL;

COMMIT;

