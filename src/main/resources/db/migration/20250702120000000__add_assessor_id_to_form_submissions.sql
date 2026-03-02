-- Migration: Add assessor_id column to form_submissions table
-- Purpose: Replace string-based submitted_by with proper foreign key to assessors table
-- This enables proper referential integrity and better querying capabilities
-- 
-- WARNING: Do not edit this migration after it has been applied to any environment.

-- Add assessor_id column to form_submissions table
ALTER TABLE form_submissions 
ADD COLUMN assessor_id BIGINT;

-- Add foreign key constraint to assessors table
ALTER TABLE form_submissions 
ADD CONSTRAINT fk_form_submissions_assessor_id 
FOREIGN KEY (assessor_id) REFERENCES assessors(id);

-- Create index for performance on assessor_id lookups
CREATE INDEX idx_form_submissions_assessor_id ON form_submissions(assessor_id);

-- Create composite index for the new duplicate check query
CREATE INDEX idx_form_submissions_form_hotel_assessor ON form_submissions(form_id, hotel_id, assessor_id) 
WHERE is_deleted = FALSE;

-- Update existing form_submissions to set assessor_id based on submitted_by
-- This attempts to match the submitted_by string with assessor email or name
UPDATE form_submissions fs
SET assessor_id = (
    SELECT a.id 
    FROM assessors a 
    INNER JOIN users u ON a.user_id = u.id 
    WHERE 
        -- Try to match by email first (most reliable)
        u.email = fs.submitted_by 
        OR
        -- Then try to match by full name
        u.full_name = fs.submitted_by
        OR
        -- Try to match by first name + last name combination
        CONCAT(u.first_name, ' ', u.last_name) = fs.submitted_by
        OR
        -- Try to match by assessor email
        a.email = fs.submitted_by
        OR
        -- Try to match by assessor name combination
        CONCAT(a.first_name, ' ', a.last_name) = fs.submitted_by
    LIMIT 1
)
WHERE fs.submitted_by IS NOT NULL 
  AND fs.submitted_by != ''
  AND fs.assessor_id IS NULL;

-- Add comment to the new column
COMMENT ON COLUMN form_submissions.assessor_id IS 'Foreign key reference to assessors table, replacing string-based submitted_by field';