-- WHEN COMMITTING OR REVIEWING THIS FILE: Make sure that the timestamp in the file name (that serves as a version) is the latest timestamp, and that no new migration have been added in the meanwhile.
-- Adding migrations out of order may cause this migration to never execute or behave in an unexpected way.
-- Migrations should NOT BE EDITED. Add a new migration to apply changes.

-- Fix evidence mappings for assessment requests
-- The backend code was using incorrect upload type to item number mappings
-- This migration updates the essential_item_evidence records to link to the correct essential items

-- Create a temporary mapping table
CREATE TEMP TABLE upload_type_item_mapping AS
SELECT unnest(ARRAY['BUILDING_PLAN_DOCUMENTS', 'OPERATING_LICENSES', 'EIA_REPORTS_AUDITS', 'VERMIN_PROOFING_DOCS', 'WATER_SUPPLY_DOCS', 'ELECTRICAL_SAFETY_DOCS', 'QUALIFIED_MANAGEMENT_DOCS', 'QUALIFIED_DEPARTMENTAL_HEADS_DOCS', 'HEALTH_MEDICAL_EXAM_DOCS', 'INSURANCE_DOCS']) AS upload_type,
       unnest(ARRAY[1, 2, 3, 11, 12, 16, 18, 19, 20, 22]) AS correct_item_no;

-- Update evidence records to link to correct essential items based on the file upload's upload_type
UPDATE essential_item_evidence eie
SET essential_item_id = (
    SELECT ei.id
    FROM essential_items ei
    INNER JOIN file_uploads fu ON fu.id = eie.file_upload_id
    INNER JOIN upload_type_item_mapping m ON m.upload_type = fu.upload_type
    WHERE ei.assessment_request_id = (
        SELECT assessment_request_id
        FROM essential_items
        WHERE id = eie.essential_item_id
    )
    AND ei.item_number = m.correct_item_no
    LIMIT 1
)
WHERE eie.file_upload_id IS NOT NULL
AND EXISTS (
    SELECT 1
    FROM file_uploads fu
    INNER JOIN upload_type_item_mapping m ON m.upload_type = fu.upload_type
    WHERE fu.id = eie.file_upload_id
    AND m.upload_type IN ('BUILDING_PLAN_DOCUMENTS', 'OPERATING_LICENSES', 'EIA_REPORTS_AUDITS',
                          'VERMIN_PROOFING_DOCS', 'WATER_SUPPLY_DOCS', 'ELECTRICAL_SAFETY_DOCS',
                          'QUALIFIED_MANAGEMENT_DOCS', 'QUALIFIED_DEPARTMENTAL_HEADS_DOCS',
                          'HEALTH_MEDICAL_EXAM_DOCS', 'INSURANCE_DOCS')
);

-- Drop the temporary mapping table
DROP TABLE upload_type_item_mapping;
