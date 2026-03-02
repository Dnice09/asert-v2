-- WHEN COMMITTING OR REVIEWING THIS FILE: Make sure that the timestamp in the file name (that serves as a version) is the latest timestamp, and that no new migration have been added in the meanwhile.
-- Adding migrations out of order may cause this migration to never execute or behave in an unexpected way.
-- Migrations should NOT BE EDITED. Add a new migration to apply changes.

-- Add a path column to the admin_hierarchies table
ALTER TABLE admin_hierarchies ADD COLUMN path TEXT;

-- Update the path for all existing records
WITH RECURSIVE nodes(id, parent_id, path) AS (
    SELECT aa.id, aa.parent_id, aa.id::TEXT AS path
    FROM admin_hierarchies AS aa
    WHERE aa.parent_id IS NULL
    UNION ALL
    SELECT location.id, location.parent_id, (p.path || '->' || location.id::TEXT)
    FROM nodes AS p
    JOIN admin_hierarchies AS location ON location.parent_id = p.id
)
UPDATE admin_hierarchies
SET path = nodes.path
FROM nodes
WHERE admin_hierarchies.id = nodes.id;

-- Ensure path column is indexed for fast lookups
CREATE INDEX idx_admin_hierarchies_path ON admin_hierarchies(path);
