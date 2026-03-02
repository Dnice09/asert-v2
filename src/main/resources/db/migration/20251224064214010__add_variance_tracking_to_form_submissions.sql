-- WHEN COMMITTING OR REVIEWING THIS FILE: Make sure that the timestamp in the file name (that serves as a version) is the latest timestamp, and that no new migration have been added in the meanwhile.
-- Adding migrations out of order may cause this migration to never execute or behave in an unexpected way.
-- Migrations should NOT BE EDITED. Add a new migration to apply changes.

-- Add variance tracking columns to form_submissions table
ALTER TABLE form_submissions
    ADD COLUMN IF NOT EXISTS variance_check_status VARCHAR(50) DEFAULT 'PENDING',
    ADD COLUMN IF NOT EXISTS variance_resolution_version INTEGER DEFAULT 1,
    ADD COLUMN IF NOT EXISTS approved_for_dt_review BOOLEAN DEFAULT FALSE;

-- Create index for variance status queries
CREATE INDEX IF NOT EXISTS idx_form_submissions_variance_status ON form_submissions(variance_check_status);
CREATE INDEX IF NOT EXISTS idx_form_submissions_dt_review ON form_submissions(approved_for_dt_review);

-- Add comments
COMMENT ON COLUMN form_submissions.variance_check_status IS 'Variance check status: PENDING, NO_VARIANCE, HAS_VARIANCE, RESOLVED';
COMMENT ON COLUMN form_submissions.variance_resolution_version IS 'Increments each time assessor re-submits to fix variances';
COMMENT ON COLUMN form_submissions.approved_for_dt_review IS 'TRUE only when all variances resolved and ready for DT approval';