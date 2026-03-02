-- WHEN COMMITTING OR REVIEWING THIS FILE: Make sure that the timestamp in the file name (that serves as a version) is the latest timestamp, and that no new migration have been added in the meanwhile.
-- Adding migrations out of order may cause this migration to never execute or behave in an unexpected way.
-- Migrations should NOT BE EDITED. Add a new migration to apply changes.

-- Change the due_date column from DATE to TIMESTAMP WITHOUT TIME ZONE

ALTER TABLE bills
    ALTER COLUMN due_date TYPE TIMESTAMP WITHOUT TIME ZONE;

-- Change the generation_date column from DATE to TIMESTAMP WITHOUT TIME ZONE
ALTER TABLE bills
    ALTER COLUMN generation_date TYPE TIMESTAMP WITHOUT TIME ZONE;

-- Change the expiry_date column from DATE to TIMESTAMP WITHOUT TIME ZONE
ALTER TABLE bills
    ALTER COLUMN expiry_date TYPE TIMESTAMP WITHOUT TIME ZONE;

-- Change the paid_date column from DATE to TIMESTAMP WITHOUT TIME ZONE
ALTER TABLE bills
    ALTER COLUMN paid_date TYPE TIMESTAMP WITHOUT TIME ZONE;

