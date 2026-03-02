-- WHEN COMMITTING OR REVIEWING THIS FILE: Make sure that the timestamp in the file name (that serves as a version) is the latest timestamp, and that no new migration have been added in the meanwhile.
-- Adding migrations out of order may cause this migration to never execute or behave in an unexpected way.
-- Migrations should NOT BE EDITED. Add a new migration to apply changes.

-- Add has_unresolved_variances column to form_submissions table
-- This column captures the historical variance status at the moment the submission was created
-- It's a point-in-time flag, NOT a real-time query of current variance logs
ALTER TABLE form_submissions
ADD COLUMN has_unresolved_variances BOOLEAN DEFAULT FALSE;

-- Add comment explaining the purpose of this column
COMMENT ON COLUMN form_submissions.has_unresolved_variances IS 'Historical flag indicating whether there were unresolved variances when this submission was created. This is NOT a real-time check - it represents the state at submission time.';
