-- WHEN COMMITTING OR REVIEWING THIS FILE: Make sure that the timestamp in the file name (that serves as a version) is the latest timestamp, and that no new migration have been added in the meanwhile.
-- Adding migrations out of order may cause this migration to never execute or behave in an unexpected way.
-- Migrations should NOT BE EDITED. Add a new migration to apply changes.

ALTER TABLE service_change_requests
ADD COLUMN data_text TEXT;

UPDATE service_change_requests
SET data_text = data::TEXT;

ALTER TABLE service_change_requests
DROP COLUMN data;

ALTER TABLE service_change_requests
RENAME COLUMN data_text TO data;
