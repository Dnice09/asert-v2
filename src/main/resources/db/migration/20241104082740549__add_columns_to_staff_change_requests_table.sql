-- WHEN COMMITTING OR REVIEWING THIS FILE: Make sure that the timestamp in the file name (that serves as a version) is the latest timestamp, and that no new migration have been added in the meanwhile.
-- Adding migrations out of order may cause this migration to never execute or behave in an unexpected way.
-- Migrations should NOT BE EDITED. Add a new migration to apply changes.

ALTER TABLE staff_change_requests
    ADD COLUMN email VARCHAR(255) NOT NULL;

ALTER TABLE staff_change_requests
    ADD COLUMN first_name VARCHAR(255) NOT NULL;

ALTER TABLE staff_change_requests
    ADD COLUMN middle_name VARCHAR(255);

ALTER TABLE staff_change_requests
    ADD COLUMN last_name VARCHAR(255) NOT NULL;

ALTER TABLE staff_change_requests
    ADD COLUMN sex VARCHAR(255) NOT NULL;

ALTER TABLE staff_change_requests
    ADD COLUMN license_number VARCHAR(255) NOT NULL UNIQUE;

ALTER TABLE staff_change_requests
    ADD COLUMN qualification VARCHAR(255) NOT NULL;

ALTER TABLE staff_change_requests
    ADD COLUMN mobile_phone VARCHAR(255) NOT NULL;

