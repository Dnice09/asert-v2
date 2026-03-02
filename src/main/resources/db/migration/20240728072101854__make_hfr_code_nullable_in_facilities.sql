-- WHEN COMMITTING OR REVIEWING THIS FILE: Make sure that the timestamp in the file name (that serves as a version) is the latest timestamp, and that no new migration have been added in the meanwhile.
-- Adding migrations out of order may cause this migration to never execute or behave in an unexpected way.
-- Migrations should NOT BE EDITED. Add a new migration to apply changes.

-- Set hfr_code to be nullable
ALTER TABLE facilities
ALTER COLUMN hfr_code DROP NOT NULL;

-- Ensure hfr_code remains unique
ALTER TABLE facilities
DROP CONSTRAINT IF EXISTS uc_facilities_hfr_code;

ALTER TABLE facilities
ADD CONSTRAINT uc_facilities_hfr_code UNIQUE(hfr_code);

