-- WHEN COMMITTING OR REVIEWING THIS FILE: Make sure that the timestamp in the file name (that serves as a version) is the latest timestamp, and that no new migration have been added in the meanwhile.
-- Adding migrations out of order may cause this migration to never execute or behave in an unexpected way.
-- Migrations should NOT BE EDITED. Add a new migration to apply changes.

-- Add submission status fields to form_submissions table

-- Add status column with default value DRAFT
ALTER TABLE form_submissions
ADD COLUMN status VARCHAR(20) DEFAULT 'DRAFT' NOT NULL;

-- Add status transition timestamp columns
ALTER TABLE form_submissions
ADD COLUMN submitted_for_approval_at TIMESTAMP WITHOUT TIME ZONE;

ALTER TABLE form_submissions
ADD COLUMN approved_at TIMESTAMP WITHOUT TIME ZONE;

ALTER TABLE form_submissions
ADD COLUMN rejected_at TIMESTAMP WITHOUT TIME ZONE;

ALTER TABLE form_submissions
ADD COLUMN rejection_reason TEXT;

-- Create index for status queries (performance optimization)
CREATE INDEX idx_form_submissions_status ON form_submissions(status);

-- Create composite index for assessor + status queries
CREATE INDEX idx_form_submissions_assessor_status ON form_submissions(assessor_id, status);

-- Create composite index for hotel + form + status queries
CREATE INDEX idx_form_submissions_hotel_form_status ON form_submissions(hotel_id, form_id, status);

-- Migrate existing data: Set all existing submissions to APPROVED
-- This ensures backward compatibility - existing submissions were already "live"
UPDATE form_submissions
SET status = 'APPROVED',
    approved_at = submitted_at
WHERE status = 'DRAFT';

-- Add comment for documentation
COMMENT ON COLUMN form_submissions.status IS 'Submission workflow status: DRAFT, SUBMITTED, APPROVED, REJECTED';
COMMENT ON COLUMN form_submissions.submitted_for_approval_at IS 'Timestamp when assessor submitted for DT approval';
COMMENT ON COLUMN form_submissions.approved_at IS 'Timestamp when DT approved the submission';
COMMENT ON COLUMN form_submissions.rejected_at IS 'Timestamp when DT rejected the submission';
COMMENT ON COLUMN form_submissions.rejection_reason IS 'Reason provided by DT for rejection';
