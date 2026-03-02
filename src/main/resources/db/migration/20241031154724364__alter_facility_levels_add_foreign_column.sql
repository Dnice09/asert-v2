-- WHEN COMMITTING OR REVIEWING THIS FILE: Make sure that the timestamp in the file name (that serves as a version) is the latest timestamp, and that no new migration have been added in the meanwhile.
-- Adding migrations out of order may cause this migration to never execute or behave in an unexpected way.
-- Migrations should NOT BE EDITED. Add a new migration to apply changes.

-- Add the facility_level_group_id column
ALTER TABLE facility_levels 
ADD COLUMN facility_level_group_id BIGINT;

-- Remove the level_group_id column
ALTER TABLE facility_levels 
DROP COLUMN level_group_id;
