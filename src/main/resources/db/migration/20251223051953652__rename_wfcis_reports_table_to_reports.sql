-- WHEN COMMITTING OR REVIEWING THIS FILE: Make sure that the timestamp in the file name (that serves as a version) is the latest timestamp, and that no new migration have been added in the meanwhile.
-- Adding migrations out of order may cause this migration to never execute or behave in an unexpected way.
-- Migrations should NOT BE EDITED. Add a new migration to apply changes.

-- 1. Rename the main table
ALTER TABLE wfis_reports RENAME TO reports;

-- 2. Rename the Primary Key index
ALTER INDEX pk_wfis_reports RENAME TO pk_reports;

-- 3. Rename the Unique Constraint index
ALTER INDEX uc_wfis_reports_uuid RENAME TO uc_reports_uuid;

-- 4. Rename the Foreign Key constraint
-- Note: PostgreSQL keeps the relationship intact, but this updates the label
ALTER TABLE reports RENAME CONSTRAINT fk_wfis_reports_on_parent TO fk_reports_on_parent;
