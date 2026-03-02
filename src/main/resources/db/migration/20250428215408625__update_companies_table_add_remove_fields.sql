-- WHEN COMMITTING OR REVIEWING THIS FILE: Make sure that the timestamp in the file name (that serves as a version) is the latest timestamp, and that no new migration have been added in the meanwhile.
-- Adding migrations out of order may cause this migration to never execute or behave in an unexpected way.
-- Migrations should NOT BE EDITED. Add a new migration to apply changes.

-- Rename the company_name column to name (first create new column, then migrate data, then drop old column)
ALTER TABLE companies ADD COLUMN name character varying(255);
UPDATE companies SET name = company_name;
ALTER TABLE companies ALTER COLUMN name SET NOT NULL;
ALTER TABLE companies DROP COLUMN company_name;

-- Add new columns
ALTER TABLE companies ADD COLUMN trading_name character varying(255);
ALTER TABLE companies ADD COLUMN tin character varying(255);
ALTER TABLE companies ADD COLUMN vat character varying(255);
ALTER TABLE companies ADD COLUMN registration_type character varying(255);
ALTER TABLE companies ADD COLUMN registration_date date;
ALTER TABLE companies ADD COLUMN registration_status character varying(255) NOT NULL DEFAULT 'PENDING';

-- Rename registration_number to certificate_registration_number
ALTER TABLE companies RENAME COLUMN registration_number TO certificate_registration_number;

-- Drop columns that are no longer needed
ALTER TABLE companies DROP COLUMN initials;
ALTER TABLE companies DROP COLUMN admin_hierarchy_id;
ALTER TABLE companies DROP COLUMN bpra_cetrificate_number;
ALTER TABLE companies DROP COLUMN responsible_first_name;
ALTER TABLE companies DROP COLUMN responsible_middle_name;
ALTER TABLE companies DROP COLUMN responsible_last_name;
ALTER TABLE companies DROP COLUMN responsible_phone;
ALTER TABLE companies DROP COLUMN responsible_email;
ALTER TABLE companies DROP COLUMN address;
ALTER TABLE companies DROP COLUMN phone;

-- Modify the foreign key constraints
ALTER TABLE companies DROP CONSTRAINT IF EXISTS fk_companies_on_admin_hierarchy;

-- Drop unique constraint on the email column if you need to (uncomment if needed)
-- ALTER TABLE companies DROP CONSTRAINT companies_email_key;
