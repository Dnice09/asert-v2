-- WHEN COMMITTING OR REVIEWING THIS FILE: Make sure that the timestamp in the file name (that serves as a version) is the latest timestamp, and that no new migration have been added in the meanwhile.
-- Adding migrations out of order may cause this migration to never execute or behave in an unexpected way.
-- Migrations should NOT BE EDITED. Add a new migration to apply changes.
--
-- Make location_id nullable temporarily to allow partial profile updates
ALTER TABLE assessors ALTER COLUMN location_id DROP NOT NULL;

-- Update any existing rows with location_id = 0 to NULL
UPDATE assessors SET location_id = NULL WHERE location_id = 0;