-- WHEN COMMITTING OR REVIEWING THIS FILE: Make sure that the timestamp in the file name (that serves as a version) is the latest timestamp, and that no new migration have been added in the meanwhile.
-- Adding migrations out of order may cause this migration to never execute or behave in an unexpected way.
-- Migrations should NOT BE EDITED. Add a new migration to apply changes.

-- Update assessor status enum values from old format to new format
-- Old: 'Pending', 'Approved', 'Rejected'  
-- New: 'PENDING', 'APPROVED', 'REJECTED', 'DRAFT'

-- Drop the old constraint first
ALTER TABLE assessors DROP CONSTRAINT IF EXISTS chk_assessor_status;

-- Update existing data to use new enum values
UPDATE assessors SET status = 'PENDING' WHERE status = 'Pending';
UPDATE assessors SET status = 'APPROVED' WHERE status = 'Approved';
UPDATE assessors SET status = 'REJECTED' WHERE status = 'Rejected';

-- Add new constraint with updated enum values
ALTER TABLE assessors ADD CONSTRAINT chk_assessor_status
    CHECK (status IN ('DRAFT', 'PENDING', 'APPROVED', 'REJECTED'));

-- Update default value for new assessors
ALTER TABLE assessors ALTER COLUMN status SET DEFAULT 'DRAFT';