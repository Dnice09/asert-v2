-- Migration to convert single propertyType to multiple propertyTypes in forms
-- This migration creates the new form_property_types table and migrates existing data

-- Create the new form_property_types table
CREATE TABLE form_property_types (
    form_id BIGINT NOT NULL,
    property_type VARCHAR(50) NOT NULL,
    PRIMARY KEY (form_id, property_type),
    FOREIGN KEY (form_id) REFERENCES forms(id) ON DELETE CASCADE
);

-- Migrate existing data from property_type column to form_property_types table
-- Include ALL forms (both active and deleted) to maintain data integrity
INSERT INTO form_property_types (form_id, property_type)
SELECT id, property_type 
FROM forms 
WHERE property_type IS NOT NULL;

-- Drop the old property_type column
ALTER TABLE forms DROP COLUMN property_type;