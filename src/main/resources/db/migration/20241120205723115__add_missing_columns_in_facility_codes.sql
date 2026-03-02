-- WHEN COMMITTING OR REVIEWING THIS FILE: Make sure that the timestamp in the file name (that serves as a version) is the latest timestamp, and that no new migration have been added in the meanwhile.
-- Adding migrations out of order may cause this migration to never execute or behave in an unexpected way.
-- Migrations should NOT BE EDITED. Add a new migration to apply changes.

-- Check if 'id' column exists and modify its type to BIGINT if it doesn't exist or needs adjustment
DO $$
BEGIN
    IF NOT EXISTS (SELECT 1 FROM information_schema.columns 
                   WHERE table_name = 'facility_codes' AND column_name = 'id') THEN
        RAISE NOTICE 'Column id does not exist';
    ELSE
        -- Modify the 'id' column to BIGINT if it is not already BIGINT
        ALTER TABLE facility_codes ALTER COLUMN id SET DATA TYPE BIGINT;
    END IF;
END $$;

-- Check if 'created_by' column exists and add it if necessary
DO $$
BEGIN
    IF NOT EXISTS (SELECT 1 FROM information_schema.columns 
                   WHERE table_name = 'facility_codes' AND column_name = 'created_by') THEN
        ALTER TABLE facility_codes ADD COLUMN created_by VARCHAR(50);
    END IF;
END $$;

-- Check if 'is_deleted' column exists and add it if necessary
DO $$
BEGIN
    IF NOT EXISTS (SELECT 1 FROM information_schema.columns 
                   WHERE table_name = 'facility_codes' AND column_name = 'is_deleted') THEN
        ALTER TABLE facility_codes ADD COLUMN is_deleted BOOLEAN DEFAULT FALSE;
    END IF;
END $$;
DO $$
BEGIN
    IF NOT EXISTS (SELECT 1 FROM information_schema.columns 
                   WHERE table_name = 'facility_codes' AND column_name = 'updated_by') THEN
        ALTER TABLE facility_codes ADD COLUMN updated_by VARCHAR(255);
    END IF;
END $$;
