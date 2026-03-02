-- WHEN COMMITTING OR REVIEWING THIS FILE: Make sure that the timestamp in the file name (that serves as a version) is the latest timestamp, and that no new migration have been added in the meanwhile.
-- Adding migrations out of order may cause this migration to never execute or behave in an unexpected way.
-- Migrations should NOT BE EDITED. Add a new migration to apply changes.

DO $$
DECLARE
    rec RECORD;
    dependent RECORD;
BEGIN
    -- List of tables to delete
    FOR rec IN
        SELECT DISTINCT unnest(ARRAY[
            'applications',
            'bills',
            'catchment_areas',
            'facility_attachments',
            'facility_codes',
            'facility_equipments',
            'facility_infrastructures',
            'facility_premises',
            'facility_staffs',
            'order_equipments',
            'staff_change_requests',
            'order_infrastructures',
            'order_staffs',
            'notifications',
            'pre_reg_reviews',
            'pre_reg_attachments',
            'pre_registrations',
            'pre_registration_equipment_requirements',
            'pre_registration_infrastructure_requirements',
            'pre_registration_premise_requirements',
            'pre_registration_service_requirements',
            'pre_registration_staff_requirements',
            'payments',
            'application_types',
            'bill_types',
            'equipments',
            'equipment_categories',
            'facility_fund_sources',
            'facility_levels',
            'facility_level_groups',
            'facility_operation_statuses',
            'facility_ownership_authorities',
            'facility_referral_point_transports',
            'facility_registration_statuses',
            'facility_types',
            'infrastructures',
            'premises',
            'services',
            'service_categories',
            'staff_titles',
            'staffs'
        ]) AS table_name
    LOOP
        -- Drop any table that has a foreign key pointing to the target table
        FOR dependent IN
            SELECT DISTINCT tc.table_name
            FROM information_schema.table_constraints tc
            JOIN information_schema.key_column_usage kcu
              ON tc.constraint_name = kcu.constraint_name
            JOIN information_schema.constraint_column_usage ccu
              ON ccu.constraint_name = tc.constraint_name
            WHERE tc.constraint_type = 'FOREIGN KEY'
              AND ccu.table_name = rec.table_name
        LOOP
            -- Drop dependent table if it still exists
            IF EXISTS (
                SELECT 1 FROM information_schema.tables
                WHERE table_schema = 'public' AND table_name = dependent.table_name
            ) THEN
                RAISE NOTICE 'Dropping dependent table: %', dependent.table_name;
                EXECUTE format('DROP TABLE IF EXISTS %I CASCADE;', dependent.table_name);
            END IF;
        END LOOP;

        -- Drop the main table if it exists
        IF EXISTS (
            SELECT 1 FROM information_schema.tables
            WHERE table_schema = 'public' AND table_name = rec.table_name
        ) THEN
            RAISE NOTICE 'Dropping main table: %', rec.table_name;
            EXECUTE format('DROP TABLE IF EXISTS %I CASCADE;', rec.table_name);
        END IF;
    END LOOP;
END
$$;
