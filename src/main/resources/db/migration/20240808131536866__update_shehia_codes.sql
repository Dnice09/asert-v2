-- WHEN COMMITTING OR REVIEWING THIS FILE: Make sure that the timestamp in the file name (that serves as a version) is the latest timestamp, and that no new migration have been added in the meanwhile.
-- Adding migrations out of order may cause this migration to never execute or behave in an unexpected way.
-- Migrations should NOT BE EDITED. Add a new migration to apply changes.

DO $$
DECLARE
    ward RECORD;
    shehia RECORD;
    district_id BIGINT;
    counter INT;
BEGIN
    FOR district_id IN (SELECT id FROM admin_hierarchies WHERE admin_hierarchy_level_id = (SELECT id FROM admin_hierarchy_levels WHERE position = 4)) LOOP
        RAISE NOTICE 'Processing district_id: %', district_id;
        counter := 1;
        FOR ward IN (SELECT id FROM admin_hierarchies WHERE parent_id = district_id AND admin_hierarchy_level_id = (SELECT id FROM admin_hierarchy_levels WHERE position = 5) ORDER BY name) LOOP
            RAISE NOTICE 'Processing ward_id: %', ward.id;
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
