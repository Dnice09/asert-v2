-- WHEN COMMITTING OR REVIEWING THIS FILE: Make sure that the timestamp in the file name (that serves as a version) is the latest timestamp, and that no new migration have been added in the meanwhile.
-- Adding migrations out of order may cause this migration to never execute or behave in an unexpected way.
-- Migrations should NOT BE EDITED. Add a new migration to apply changes.

-- Update essential_items table to match new entity structure
-- Rename columns to match the new property names

-- Rename item_number to item_no (keeping the same database column name but updating for consistency)
-- The entity now uses itemNo field which maps to item_number column - no database change needed

-- Rename item_description to description (database column change needed)
ALTER TABLE essential_items RENAME COLUMN item_description TO description;

-- Rename comments to notes (database column change needed)  
ALTER TABLE essential_items RENAME COLUMN comments TO notes;

-- Add comments to document the changes
COMMENT ON COLUMN essential_items.description IS 'Description of the essential item requirement';
COMMENT ON COLUMN essential_items.notes IS 'Additional notes or comments about the essential item compliance';

-- Update column comments to reflect the new field mappings
COMMENT ON COLUMN essential_items.item_number IS 'Sequential number of the essential item (maps to itemNo field in entity)';

-- Note: The assessment_requests table structure remains mostly the same since the facilityInfo 
-- fields are mapped to the existing columns (contactPerson, email, phoneNumber, address, requestedDate).
-- The main change is that hotelUuid is no longer in the DTO structure but the hotel_id foreign key remains.

-- Update the check constraint to be more flexible for compliance values
ALTER TABLE essential_items DROP CONSTRAINT IF EXISTS chk_essential_item_compliance;
ALTER TABLE essential_items ADD CONSTRAINT chk_essential_item_compliance 
    CHECK (compliance IS NULL OR compliance IN ('compliant', 'non-compliant', 'partially-compliant', ''));

-- Add new table for uploaded documents metadata if needed in the future
-- This is prepared for the uploadedDocuments array in the payload
-- For now, we'll use the existing file_uploads table and essential_item_evidence table

-- Update assessment request status enum to include new statuses if needed
ALTER TABLE assessment_requests DROP CONSTRAINT IF EXISTS chk_assessment_request_status;
ALTER TABLE assessment_requests ADD CONSTRAINT chk_assessment_request_status 
    CHECK (status IN ('PENDING', 'SUBMITTED', 'UNDER_REVIEW', 'APPROVED', 'REJECTED', 'SCHEDULED'));

-- Add index for better performance on description searches
CREATE INDEX IF NOT EXISTS idx_essential_items_description ON essential_items(description);
CREATE INDEX IF NOT EXISTS idx_essential_items_notes ON essential_items(notes);

-- Update table comments to reflect the new payload structure
COMMENT ON TABLE assessment_requests IS 'Stores assessment requests with facility information and compliance items. Now supports new payload structure with facilityInfo object.';
COMMENT ON TABLE essential_items IS 'Stores individual compliance items with updated field names (itemNo, description, notes) to match new payload structure.';