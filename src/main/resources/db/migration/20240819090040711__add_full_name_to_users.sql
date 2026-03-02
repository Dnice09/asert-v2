-- WHEN COMMITTING OR REVIEWING THIS FILE: Make sure that the timestamp in the file name (that serves as a version) is the latest timestamp, and that no new migration have been added in the meanwhile.
-- Adding migrations out of order may cause this migration to never execute or behave in an unexpected way.
-- Migrations should NOT BE EDITED. Add a new migration to apply changes.
-- Migration Script: Update full_name for users where it is currently null

UPDATE users
SET full_name =
    CASE
        WHEN middle_name IS NOT NULL THEN CONCAT(first_name, ' ', middle_name, ' ', last_name)
        ELSE CONCAT(first_name, ' ', last_name)
    END
WHERE full_name IS NULL;
