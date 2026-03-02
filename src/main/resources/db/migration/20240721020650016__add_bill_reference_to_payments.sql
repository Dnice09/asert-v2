-- WHEN COMMITTING OR REVIEWING THIS FILE: Make sure that the timestamp in the file name (that serves as a version) is the latest timestamp, and that no new migration have been added in the meanwhile.
-- Adding migrations out of order may cause this migration to never execute or behave in an unexpected way.
-- Migrations should NOT BE EDITED. Add a new migration to apply changes.

-- 1. Add the bill_reference column to payments table
ALTER TABLE payments
ADD COLUMN bill_reference UUID;

-- 2. Update the bill_reference column with the corresponding bill's uuid value
UPDATE payments p
SET bill_reference = b.uuid
FROM bills b
WHERE p.bill_id = b.id;

-- 3. Alter the column to not allow nulls after it has been populated
ALTER TABLE payments
ALTER COLUMN bill_reference SET NOT NULL;

-- 4. Add unique constraint to bill_reference column
ALTER TABLE payments
ADD CONSTRAINT unique_bill_reference UNIQUE (bill_reference);
