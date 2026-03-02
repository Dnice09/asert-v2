-- WHEN COMMITTING OR REVIEWING THIS FILE: Make sure that the timestamp in the file name (that serves as a version) is the latest timestamp, and that no new migration have been added in the meanwhile.
-- Adding migrations out of order may cause this migration to never execute or behave in an unexpected way.
-- Migrations should NOT BE EDITED. Add a new migration to apply changes.

ALTER TABLE facility_staffs
    ADD COLUMN first_name VARCHAR(255) NULL;
ALTER TABLE facility_staffs
    ADD COLUMN middle_name VARCHAR(255) NULL;
ALTER TABLE facility_staffs
    ADD COLUMN last_name VARCHAR(255) NULL;
ALTER TABLE facility_staffs
    ADD COLUMN license_number VARCHAR(255) NULL;
ALTER TABLE facility_staffs
    ADD COLUMN zssf_number VARCHAR(255) NULL;
ALTER TABLE facility_staffs
    ADD COLUMN attachment_id BIGINT NULL;
ALTER TABLE facility_staffs
    ADD COLUMN contract_id BIGINT NULL;
ALTER TABLE facility_staffs
    DROP COLUMN staff_id;

ALTER TABLE facility_staffs
    ADD COLUMN mobile_phone VARCHAR(255) NULL;

ALTER TABLE facility_staffs
    ADD COLUMN email VARCHAR(255) NULL;

ALTER TABLE facility_staffs
    ADD COLUMN sex VARCHAR(255) NULL;

ALTER TABLE facility_staffs
    ADD COLUMN qualification VARCHAR(255) NULL;
