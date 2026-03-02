-- WHEN COMMITTING OR REVIEWING THIS FILE: Make sure that the timestamp in the file name (that serves as a version) is the latest timestamp, and that no new migration have been added in the meanwhile.
-- Adding migrations out of order may cause this migration to never execute or behave in an unexpected way.
-- Migrations should NOT BE EDITED. Add a new migration to apply changes.

-- Add performance indexes for assessment_variance_logs table
-- These indexes optimize the findByUuid queries which are currently very slow

-- Note: The uuid column already has a unique constraint which creates an index,
-- but we need a composite index for the common query pattern (uuid + is_deleted)

-- Add composite index on (uuid, is_deleted) for optimized single record lookup
-- This supports the findByUuidWithRelations query efficiently
CREATE INDEX IF NOT EXISTS idx_variance_logs_uuid_not_deleted
    ON assessment_variance_logs(uuid, is_deleted)
    WHERE is_deleted = false;

-- Add index on is_deleted for filtering queries
-- This helps with any query that filters by is_deleted
CREATE INDEX IF NOT EXISTS idx_variance_logs_is_deleted
    ON assessment_variance_logs(is_deleted);

-- Add index on created_at for sorting and date range queries
CREATE INDEX IF NOT EXISTS idx_variance_logs_created_at
    ON assessment_variance_logs(created_at DESC);

-- Add composite index on (hotel_id, form_id, is_deleted) for filtered queries
-- This supports queries that filter by hotel and form together
CREATE INDEX IF NOT EXISTS idx_variance_logs_hotel_form_not_deleted
    ON assessment_variance_logs(hotel_id, form_id, is_deleted)
    WHERE is_deleted = false;

COMMENT ON INDEX idx_variance_logs_uuid_not_deleted IS 'Optimizes findByUuid queries by UUID with soft delete filter';
COMMENT ON INDEX idx_variance_logs_is_deleted IS 'Optimizes soft delete filtering across all queries';
COMMENT ON INDEX idx_variance_logs_created_at IS 'Optimizes date-based sorting and filtering';
COMMENT ON INDEX idx_variance_logs_hotel_form_not_deleted IS 'Optimizes hotel+form filtered queries with soft delete';
