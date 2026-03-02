-- Remove comments field from form_fields table since comments are only needed during form submission
-- Comments should only exist in form_field_responses table, not in the form definition
ALTER TABLE form_fields DROP COLUMN IF EXISTS comments;