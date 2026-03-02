-- WHEN COMMITTING OR REVIEWING THIS FILE: Make sure that the timestamp in the file name (that serves as a version) is the latest timestamp, and that no new migration have been added in the meanwhile.
-- Adding migrations out of order may cause this migration to never execute or behave in an unexpected way.
-- Migrations should NOT BE EDITED. Add a new migration to apply changes.

DO $$ 
BEGIN
    IF NOT EXISTS (SELECT 1 FROM pg_class WHERE relname = 'idx_admin_hierarchies_parent_id') THEN
        CREATE INDEX idx_admin_hierarchies_parent_id ON admin_hierarchies(parent_id);
    END IF;
END $$;

DO $$ 
BEGIN
    IF NOT EXISTS (SELECT 1 FROM pg_class WHERE relname = 'idx_admin_hierarchies_id') THEN
        CREATE INDEX idx_admin_hierarchies_id ON admin_hierarchies(id);
    END IF;
END $$;
