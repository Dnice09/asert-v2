-- WHEN COMMITTING OR REVIEWING THIS FILE: Make sure that the timestamp in the file name (that serves as a version) is the latest timestamp, and that no new migration have been added in the meanwhile.
-- Adding migrations out of order may cause this migration to never execute or behave in an unexpected way.
-- Migrations should NOT BE EDITED. Add a new migration to apply changes.
ALTER TABLE translations ADD COLUMN is_deleted BOOLEAN DEFAULT FALSE;
ALTER TABLE users ADD COLUMN is_deleted BOOLEAN DEFAULT FALSE;
ALTER TABLE roles ADD COLUMN is_deleted BOOLEAN DEFAULT FALSE;
ALTER TABLE menu_items ADD COLUMN is_deleted BOOLEAN DEFAULT FALSE;
ALTER TABLE menu_groups ADD COLUMN is_deleted BOOLEAN DEFAULT FALSE;

