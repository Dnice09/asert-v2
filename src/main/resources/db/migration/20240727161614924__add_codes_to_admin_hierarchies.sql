-- WHEN COMMITTING OR REVIEWING THIS FILE: Make sure that the timestamp in the file name (that serves as a version) is the latest timestamp, and that no new migration have been added in the meanwhile.
-- Adding migrations out of order may cause this migration to never execute or behave in an unexpected way.
-- Migrations should NOT BE EDITED. Add a new migration to apply changes.
--
-- Step 1: Drop the unique constraint on the old code column
ALTER TABLE admin_hierarchies DROP CONSTRAINT IF EXISTS uc_admin_hierarchies_code;

-- Step 2: Make the existing code column nullable
ALTER TABLE admin_hierarchies ALTER COLUMN code DROP NOT NULL;

-- Step 3: Remove the existing code column
ALTER TABLE admin_hierarchies DROP COLUMN IF EXISTS code CASCADE;

-- Step 4: Add a new code column
ALTER TABLE admin_hierarchies ADD COLUMN code TEXT;

-- Step 5: Update islet codes
UPDATE admin_hierarchies ah
SET code = CASE
    WHEN ah.name ILIKE '%unguja%' THEN '2'
    WHEN ah.name ILIKE '%pemba%' THEN '1'
    ELSE NULL
END
FROM admin_hierarchy_levels ahl
WHERE ah.admin_hierarchy_level_id = ahl.id AND ahl.position = 2;

-- Step 6: Update region codes
UPDATE admin_hierarchies ah
SET code = CASE
    WHEN ah.name ILIKE '%kaskazini pemba%' THEN '01'
    WHEN ah.name ILIKE '%kusini pemba%' THEN '02'
    WHEN ah.name ILIKE '%kaskazini unguja%' THEN '03'
    WHEN ah.name ILIKE '%mjini magharibi%' THEN '04'
    WHEN ah.name ILIKE '%kusini unguja%' THEN '05'
    ELSE NULL
END
FROM admin_hierarchy_levels ahl
WHERE ah.admin_hierarchy_level_id = ahl.id AND ahl.position = 3;

-- Step 7: Update district codes
UPDATE admin_hierarchies ah
SET code = CASE
    -- Pemba districts
    WHEN ah.name ILIKE '%Mkoani Municipal Council%' THEN '01'
    WHEN ah.name ILIKE '%Chake Municipal Council%' THEN '02'
    WHEN ah.name ILIKE '%Wete Municipal Council%' THEN '03'
    WHEN ah.name ILIKE '%Micheweni District Council%' THEN '04'
    -- Unguja districts
    WHEN ah.name ILIKE '%Central Municipal Council%' THEN '05'
    WHEN ah.name ILIKE '%South District Council%' THEN '06'
    WHEN ah.name ILIKE '%North "A" Municipal Council%' THEN '07'
    WHEN ah.name ILIKE '%Urban Municipal Council%' THEN '08'
    WHEN ah.name ILIKE '%West "A" Municipal Council%' THEN '09'
    WHEN ah.name ILIKE '%West "B" Municipal Council%' THEN '10'
    WHEN ah.name ILIKE '%North "B" Municipal Council%' THEN '11'
    ELSE NULL
END
FROM admin_hierarchy_levels ahl
WHERE ah.admin_hierarchy_level_id = ahl.id AND ahl.position = 4;

-- Step 8: Update shehia codes considering wards
DO $$
DECLARE
    ward RECORD;
    shehia RECORD;
    district_id BIGINT;
    counter INT;
BEGIN
    FOR district_id IN (SELECT id FROM admin_hierarchies WHERE admin_hierarchy_level_id = (SELECT id FROM admin_hierarchy_levels WHERE position = 4)) LOOP
        RAISE NOTICE 'Processing district_id: %', district_id;
        FOR ward IN (SELECT id FROM admin_hierarchies WHERE parent_id = district_id AND admin_hierarchy_level_id = (SELECT id FROM admin_hierarchy_levels WHERE position = 5) ORDER BY name) LOOP
            RAISE NOTICE 'Processing ward_id: %', ward.id;
            counter := 1;
            FOR shehia IN (SELECT id FROM admin_hierarchies WHERE parent_id = ward.id AND admin_hierarchy_level_id = (SELECT id FROM admin_hierarchy_levels WHERE position = 6) ORDER BY name) LOOP
                RAISE NOTICE 'Updating shehia_id: % with counter: %', shehia.id, counter;
                UPDATE admin_hierarchies
                SET code = LPAD(counter::TEXT, 2, '0')
                WHERE id = shehia.id;
                counter := counter + 1;
            END LOOP;
        END LOOP;
    END LOOP;
END $$;

-- Ensure code column is indexed for fast lookups without enforcing uniqueness
CREATE INDEX idx_admin_hierarchies_code ON admin_hierarchies(code);

-- Reset the sequence for the admin_hierarchies table
SELECT setval(pg_get_serial_sequence('admin_hierarchies', 'id'), coalesce(max(id), 1) + 1, false) FROM admin_hierarchies;

