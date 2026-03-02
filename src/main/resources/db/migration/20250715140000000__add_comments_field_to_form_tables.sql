-- Add comments field to form_field_responses table for storing user comments during form submission
ALTER TABLE form_field_responses ADD COLUMN comments TEXT DEFAULT 'N/A';

-- Update existing form_field_responses to have default 'N/A' for comments where null
UPDATE form_field_responses SET comments = 'N/A' WHERE comments IS NULL;