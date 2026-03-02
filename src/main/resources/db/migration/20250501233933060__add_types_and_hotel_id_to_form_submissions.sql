-- WHEN COMMITTING OR REVIEWING THIS FILE: Make sure that the timestamp in the file name (that serves as a version) is the latest timestamp, and that no new migration have been added in the meanwhile.
-- Adding migrations out of order may cause this migration to never execute or behave in an unexpected way.
-- Migrations should NOT BE EDITED. Add a new migration to apply changes.

-- Add property_type column to forms table
ALTER TABLE forms
ADD COLUMN property_type VARCHAR(255);

-- Add unique constraint on property_type to ensure one form per type
ALTER TABLE forms
ADD CONSTRAINT uk_forms_property_type UNIQUE (property_type);

-- Add hotel_id column to form_submissions table
ALTER TABLE form_submissions
ADD COLUMN hotel_id BIGINT;

-- Add foreign key constraint to hotels table
ALTER TABLE form_submissions
ADD CONSTRAINT fk_form_submissions_hotel
FOREIGN KEY (hotel_id) REFERENCES hotels (id);

-- Add an index on the hotel_id column for better query performance
CREATE INDEX idx_form_submissions_hotel_id ON form_submissions (hotel_id);

-- Add comments to explain the purpose of these columns
COMMENT ON COLUMN forms.property_type IS 'Type of hotel property this form is for, must be unique';
COMMENT ON COLUMN form_submissions.hotel_id IS 'Reference to the hotel being assessed in this submission';
